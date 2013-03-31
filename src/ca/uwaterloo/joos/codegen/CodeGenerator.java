package ca.uwaterloo.joos.codegen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import ca.uwaterloo.joos.Main;
import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.FileUnit;
import ca.uwaterloo.joos.ast.Modifiers;
import ca.uwaterloo.joos.ast.Modifiers.Modifier;
import ca.uwaterloo.joos.ast.decl.FieldDeclaration;
import ca.uwaterloo.joos.ast.decl.LocalVariableDeclaration;
import ca.uwaterloo.joos.ast.decl.MethodDeclaration;
import ca.uwaterloo.joos.ast.decl.ParameterDeclaration;
import ca.uwaterloo.joos.ast.decl.TypeDeclaration;
import ca.uwaterloo.joos.ast.decl.VariableDeclaration;
import ca.uwaterloo.joos.ast.expr.ClassCreateExpression;
import ca.uwaterloo.joos.ast.expr.Expression;
import ca.uwaterloo.joos.ast.expr.InfixExpression;
import ca.uwaterloo.joos.ast.expr.InfixExpression.InfixOperator;
import ca.uwaterloo.joos.ast.expr.MethodInvokeExpression;
import ca.uwaterloo.joos.ast.expr.UnaryExpression;
import ca.uwaterloo.joos.ast.expr.name.Name;
import ca.uwaterloo.joos.ast.expr.name.QualifiedName;
import ca.uwaterloo.joos.ast.expr.name.SimpleName;
import ca.uwaterloo.joos.ast.expr.primary.LiteralPrimary;
import ca.uwaterloo.joos.ast.expr.primary.Primary;
import ca.uwaterloo.joos.ast.statement.ReturnStatement;
import ca.uwaterloo.joos.symboltable.SemanticsVisitor;
import ca.uwaterloo.joos.symboltable.SymbolTable;
import ca.uwaterloo.joos.symboltable.TableEntry;
import ca.uwaterloo.joos.symboltable.TypeScope;

public class CodeGenerator extends SemanticsVisitor {
	public static final Logger logger = Main.getLogger(CodeGenerator.class);
	
	protected static final String BOOLEAN_TRUE = "0xffffffff";
	protected static final String BOOLEAN_FALSE = "0x0";
	protected static final String NULL = "0x0";
	
	protected File asmFile = null;
	protected Set<String> externs = null;
	protected List<String> texts = null;
	protected List<String> data = null;
	
	private String methodLabel = null;
	private Integer literalCount = 0;
	private Integer comparisonCount = 0;

	public CodeGenerator(SymbolTable table) {
		super(table);
		logger.setLevel(Level.FINER);
		externs = new HashSet<String>();
		texts = new ArrayList<String>();
		data = new ArrayList<String>();
		
		// Place the runtime.s externs
		this.externs.add("__malloc");
		this.externs.add("__debexit");
		this.externs.add("__exception");
		this.externs.add("NATIVEjava.io.OutputStream.nativeWrite");
		
		this.texts.add("");
		this.texts.add("section .text");
		this.texts.add("");
		
		this.data.add("");
		this.data.add("section .data");
		this.data.add("");
	}

	@Override
	public void willVisit(ASTNode node) throws Exception {
		super.willVisit(node);
		
		if (node instanceof TypeDeclaration) {
			// Construct output file
			String filename = this.getCurrentScope().getName();
			filename = filename.replace('.', '/');
			filename = "./output/" + filename + ".s";
			logger.finer(filename);
			this.asmFile = new File(filename);
		} else if (node instanceof MethodDeclaration) {
			Modifiers modifiers = ((MethodDeclaration) node).getModifiers();
			if (!modifiers.containModifier(Modifier.NATIVE) && !modifiers.containModifier(Modifier.ABSTRACT)) {
				// Define method labels
				this.methodLabel = this.methodLabel(this.getCurrentScope().getName());
				if(((MethodDeclaration) node).getName().getSimpleName().equals("test") && 
						modifiers.containModifier(Modifier.STATIC)) {
					this.methodLabel = "_start";
				} 
				
				this.texts.add("global " + this.methodLabel);
				this.texts.add(this.methodLabel + ":");
				
				// Preamble
				this.texts.add("push ebp\t\t\t; Preamble");
				this.texts.add("mov ebp, esp");
				
				// Allocate space for local variables
				this.texts.add("sub esp, " + (((MethodDeclaration) node).totalLocalVariables * 4));
				
				// Push registers
				// this.texts.add("push eax");		// Leave eax as return value
				this.texts.add("push ebx");
				this.texts.add("push ecx");
				this.texts.add("push edx");
				this.texts.add("");
			}
		}
	}

	@Override
	public boolean visit(ASTNode node) throws Exception {
		if (node instanceof MethodInvokeExpression) {
			this.generateMethodInvoke((MethodInvokeExpression) node);
			return false;
		} else if (node instanceof ClassCreateExpression){
			this.generateClassCreate((ClassCreateExpression) node);
			return false;
		} else if (node instanceof InfixExpression) {
			this.generateInfixExpression((InfixExpression) node);
			return false;
		} else if (node instanceof LiteralPrimary) {
			this.generateLiteral((LiteralPrimary) node);
			return false;
		} else if (node instanceof UnaryExpression) {
			this.generateUnaryExpression((UnaryExpression) node);
			return false;
		}
		return super.visit(node);
	}

	@Override
	public void didVisit(ASTNode node) throws Exception {
		if (node instanceof FileUnit) {
			// File content generated, write to file
			File dir = this.asmFile.getParentFile();
			if(dir != null) {
				dir.mkdirs();
			}
			this.asmFile.createNewFile();
			BufferedWriter asmWriter = new BufferedWriter(new FileWriter(this.asmFile));
			for(String line: this.externs) {
				asmWriter.write("extern " + line);
				asmWriter.newLine();
			}
			
			for(String line: this.texts) {
				if(!line.startsWith("global")) {
					line = "\t" + line;
					if(!line.endsWith(":")) {
						line = "\t" + line;
					}
				}
				asmWriter.write(line);
				asmWriter.newLine();
			}
			
			for(String line: this.data) {
				asmWriter.write(line);
				asmWriter.newLine();
			}
			asmWriter.close();
		} else if (node instanceof TypeDeclaration) {
			this.texts.add("global " + this.getCurrentScope().getName() + "_VTABLE");
			this.texts.add(this.getCurrentScope().getName() + "_VTABLE:");
			// TODO: append vtable contents
			this.texts.add("");
		} else if (node instanceof MethodDeclaration) {
			Modifiers modifiers = ((MethodDeclaration) node).getModifiers();
			if (!modifiers.containModifier(Modifier.NATIVE) && !modifiers.containModifier(Modifier.ABSTRACT)) {
				// Postamble
				this.texts.add(this.methodLabel + "_END:");
				// Pop registers
				this.texts.add("pop edx\t\t\t; Postamble");
				this.texts.add("pop ecx");
				this.texts.add("pop ebx");
				// this.texts.add("pop eax");		// Leave eax as return value 
				
				// Deallocate space for local variables
				this.texts.add("add esp, " + (((MethodDeclaration) node).totalLocalVariables * 4));
				
				// Restore frame pointer
				this.texts.add("pop ebp");
				
				if(this.methodLabel.equals("_start")) {
					this.texts.add("call __debexit");
				} else {
					this.texts.add("ret");
				}
				this.texts.add("");
			}
		} else if (node instanceof ReturnStatement) {
			this.texts.add("jmp " + this.methodLabel + "_END");
		} 
		super.didVisit(node);
	}
	
	private String methodLabel(String methodSignature) {
		String label = methodSignature.replaceAll("[(),]", "_");
		label = label.replaceAll("\\[\\]", "_ARRAY");
		return label;
	}
	
	private void generateMethodInvoke(MethodInvokeExpression methodInvoke) throws Exception {
		// Push parameters to stack
		List<Expression> args = methodInvoke.getArguments();
		int i = args.size();
		for(i--; i >= 0; i--) {
			Expression arg = args.get(i);
			// Generate code for arg
			arg.accept(this);
			this.texts.add("push eax\t\t\t; Push parameter #" + i + " to stack");
		}
		
		// Push THIS to stack, THIS should be the address of the object
		Primary primary = methodInvoke.getPrimary();
		Name name = methodInvoke.getName();
		if(primary != null) {
			// If primary is not null, means is invoking method on a primary
			primary.accept(this);
		} else if (name instanceof QualifiedName) {
			logger.finest("Generating method invoke for name " + name + " with #" + ((QualifiedName)name).originalDeclarations.size() + " entries");
			this.texts.add("mov eax, [ebp + 8]");
			List<TableEntry> originalDeclarations = ((QualifiedName) name).originalDeclarations;
			for(TableEntry entry: originalDeclarations) {
				VariableDeclaration varDecl = (VariableDeclaration) entry.getNode();
				if (varDecl instanceof ParameterDeclaration) {
					this.texts.add("mov eax, [ebp + " + (8 + varDecl.getIndex() * 4) + "]\t\t\t; Calling " + name.getName());
				} else if (varDecl instanceof FieldDeclaration) {
					this.texts.add("mov eax, [eax + " + (4 + varDecl.getIndex() * 4) + "]\t\t\t; Calling " + name.getName());
				} else if (varDecl instanceof LocalVariableDeclaration) {
					this.texts.add("mov eax, [ebp - " + (varDecl.getIndex() * 4) + "]\t\t\t; Calling " + name.getName());
				}
			}
		}  else if (name instanceof SimpleName) {
			// Invoking method within same Type, THIS is parameter #0
			logger.finest("Generating method invoke for simple name " + name);
			this.texts.add("mov eax, [ebp + 8]\t\t\t; Calling " + name.getName());
		}
		this.texts.add("push eax");
		
		// Invoke the method
		// TODO: call from vtable
		String methodName = methodInvoke.fullyQualifiedName;
		String methodLabel = this.methodLabel(methodName);
		if(methodLabel.equals("java.io.OutputStream.nativeWrite_INT__")) {
			methodLabel = "NATIVEjava.io.OutputStream.nativeWrite";
		}
		this.texts.add("call " + methodLabel);
		
		// Pop THIS from stack
		this.texts.add("pop edx\t\t\t; Pop THIS");
		// Pop parameters from stack
		for(i = 0; i < args.size(); i++) {
			this.texts.add("pop edx\t\t\t; Pop parameters #" + i + " from stack");
		}
		
		// Add to extern if is not local method
		if (!this.getCurrentScope().getParentTypeScope().getSymbols().containsKey(methodName)) {
			this.externs.add(methodLabel);
		}
		this.texts.add("");
	}
	
	private void generateClassCreate(ClassCreateExpression classCreate) throws Exception {
		// Push parameters to stack
		List<Expression> args = classCreate.getArguments();
		int i = args.size();
		for(i--; i >= 0; i--) {
			Expression arg = args.get(i);
			// Generate code for arg
			arg.accept(this);
			this.texts.add("push eax\t\t\t; Push parameter #" + i + " to stack");
		}
		
		// Allocate space for the new object
		TypeScope typeScope = this.table.getType(classCreate.getType().getFullyQualifiedName());
		TypeDeclaration typeDecl = (TypeDeclaration) typeScope.getReferenceNode();
		this.texts.add("mov eax, " + (4 + typeDecl.totalFieldDeclarations * 4) + "\t\t\t; Size of the object");
		this.texts.add("call __malloc");
		this.texts.add("push eax\t\t\t; Push new object pointer as THIS");
		
		// Invoke the constructor
		String constructorName = classCreate.fullyQualifiedName;
		String constructorLabel = this.methodLabel(constructorName);
		this.texts.add("call " + constructorLabel);
		
		// Pop THIS from stack
		this.texts.add("pop edx\t\t\t; Pop THIS");
		// Pop parameters from stack
		for(i = 0; i < args.size(); i++) {
			this.texts.add("pop edx\t\t\t; Pop parameters #" + i + " from stack");
		}
		
		// Add to extern if is not local method
		if (!this.getCurrentScope().getParentTypeScope().getSymbols().containsKey(constructorName)) {
			this.externs.add(constructorLabel);
		}
		this.texts.add("");
	}
	
	private void generateInfixExpression(InfixExpression infixExpr) throws Exception {
		InfixOperator operator = infixExpr.getOperator();
		// Instance of
		if (operator.equals(InfixOperator.INSTANCEOF)) {
			// TODO instanceof
			return;
		}
		
		List<Expression> operands = infixExpr.getOperands();
		// Generate code for the second operand and push to the stack
		operands.get(1).accept(this);
		this.texts.add("push eax\t\t\t; Push second operand value");
		
		// Generate code for the first operand and result stay in eax
		operands.get(0).accept(this);
		this.texts.add("pop edx\t\t\t; Pop second operand value to edx");
		
		switch(operator) {
		case AND:
			// TODO: lazy and
			this.texts.add("or eax, edx");
			break;
		case BAND:
			this.texts.add("and eax, edx");
			break;
		case BOR:
			this.texts.add("or eax, edx");
			break;
		case EQ:
			this.texts.add("cmp eax, edx");
			this.texts.add("je " + "__COMPARISON_TRUE_" + comparisonCount);
			this.texts.add("mov eax, " + BOOLEAN_FALSE);
			this.texts.add("jmp " + "__COMPARISON_FALSE_" + comparisonCount);
			this.texts.add("__COMPARISON_TRUE_" + comparisonCount + ":");
			this.texts.add("mov eax, " + BOOLEAN_TRUE);
			this.texts.add("__COMPARISON_FALSE_" + comparisonCount + ":");
			this.comparisonCount++;
			break;
		case GEQ:
			this.texts.add("cmp eax, edx");
			this.texts.add("jge " + "__COMPARISON_TRUE_" + comparisonCount);
			this.texts.add("mov eax, " + BOOLEAN_FALSE);
			this.texts.add("jmp " + "__COMPARISON_FALSE_" + comparisonCount);
			this.texts.add("__COMPARISON_TRUE_" + comparisonCount + ":");
			this.texts.add("mov eax, " + BOOLEAN_TRUE);
			this.texts.add("__COMPARISON_FALSE_" + comparisonCount + ":");
			this.comparisonCount++;
			break;
		case GT:
			this.texts.add("cmp eax, edx");
			this.texts.add("jg " + "__COMPARISON_TRUE_" + comparisonCount);
			this.texts.add("mov eax, " + BOOLEAN_FALSE);
			this.texts.add("jmp " + "__COMPARISON_FALSE_" + comparisonCount);
			this.texts.add("__COMPARISON_TRUE_" + comparisonCount + ":");
			this.texts.add("mov eax, " + BOOLEAN_TRUE);
			this.texts.add("__COMPARISON_FALSE_" + comparisonCount + ":");
			this.comparisonCount++;
			break;
		case LEQ:
			this.texts.add("cmp eax, edx");
			this.texts.add("jle " + "__COMPARISON_TRUE_" + comparisonCount);
			this.texts.add("mov eax, " + BOOLEAN_FALSE);
			this.texts.add("jmp " + "__COMPARISON_FALSE_" + comparisonCount);
			this.texts.add("__COMPARISON_TRUE_" + comparisonCount + ":");
			this.texts.add("mov eax, " + BOOLEAN_TRUE);
			this.texts.add("__COMPARISON_FALSE_" + comparisonCount + ":");
			this.comparisonCount++;
			break;
		case LT:
			this.texts.add("cmp eax, edx");
			this.texts.add("jl " + "__COMPARISON_TRUE_" + comparisonCount);
			this.texts.add("mov eax, " + BOOLEAN_FALSE);
			this.texts.add("jmp " + "__COMPARISON_FALSE_" + comparisonCount);
			this.texts.add("__COMPARISON_TRUE_" + comparisonCount + ":");
			this.texts.add("mov eax, " + BOOLEAN_TRUE);
			this.texts.add("__COMPARISON_FALSE_" + comparisonCount + ":");
			this.comparisonCount++;
			break;
		case MINUS:
			// eax = first operand - second operand
			this.texts.add("sub eax, edx");
			break;
		case NEQ:
			break;
		case OR:
			// TODO: lazy or
			this.texts.add("and eax, edx");
			break;
		case PERCENT:
			// eax = first operand % second operand
			this.texts.add("cmp edx, 0\t\t\t; Check zero divider");
			this.texts.add("je __exception\t\t\t; Throw exception");
			this.texts.add("mov ebx, 0");
			this.texts.add("xchg edx, ebx\t\t\t; Set edx to 0, and ebx to be the divider");
			this.texts.add("idiv ebx\t\t\t; Divide edx:eax with ebx");
			this.texts.add("mov eax, edx\t\t\t; Move the remainder to eax");
			break;
		case PLUS:
			// TODO: String addition
			// eax = first operand + second operand
			this.texts.add("add eax, edx");
			break;
		case SLASH:
			// eax = first operand / second operand
			this.texts.add("cmp edx, 0\t\t\t; Check zero divider");
			this.texts.add("je __exception\t\t\t; Throw exception");
			this.texts.add("mov ebx, 0");
			this.texts.add("xchg edx, ebx\t\t\t; Set edx to 0, and ebx to be the divider");
			this.texts.add("idiv ebx\t\t\t; Divide edx:eax with ebx, quotient will be in eax");
			break;
		case STAR:
			// eax = first operand * second operand
			this.texts.add("imul eax, edx");
			break;
		default:
			throw new Exception("Unkown infix operator type " + operator);
		}
		this.texts.add("");
	}
	
	private void generateLiteral(LiteralPrimary literal) throws Exception {
		switch(literal.getLiteralType()) {
		case BOOLLIT:
			if(literal.getValue().equals("true")) {
				this.texts.add("mov eax, " + BOOLEAN_TRUE);
			} else {
				this.texts.add("mov eax, " + BOOLEAN_FALSE);
			}
			break;
		case CHARLIT:
			// Assuming char literal in format 'a'
			this.texts.add("mov eax, " + ((int) literal.getValue().charAt(1)));
			break;
		case INTLIT:
			// Assuming int literal within interger range
			this.texts.add("mov eax, " + Integer.valueOf(literal.getValue()));
			break;
		case NULL:
			this.texts.add("mov eax, " + NULL);
			break;
		case STRINGLIT:
			this.data.add("__STRING_LIT_" + this.literalCount + " dd " + literal.getValue());
			this.texts.add("mov eax, " + "__STRING_LIT_" + this.literalCount);
			this.literalCount++;
			break;
		default:
			break;
		}
	}
	
	private void generateUnaryExpression(UnaryExpression unaryExpr) throws Exception {
		Expression operand = unaryExpr.getOperand();
		switch(unaryExpr.getOperator()) {
		case MINUS:
			if(operand instanceof LiteralPrimary) {
				// Assuming is int literal
				this.texts.add("mov eax, " + Integer.valueOf("-" + ((LiteralPrimary) operand).getValue()));
			} else {
				operand.accept(this);
				this.texts.add("neg eax");
			}
			break;
		case NOT:
			operand.accept(this);
			this.texts.add("not eax");
			break;
		default:
			break;
		}
	}
}
