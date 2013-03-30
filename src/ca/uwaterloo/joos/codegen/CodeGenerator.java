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
import ca.uwaterloo.joos.ast.decl.MethodDeclaration;
import ca.uwaterloo.joos.ast.decl.TypeDeclaration;
import ca.uwaterloo.joos.ast.expr.Expression;
import ca.uwaterloo.joos.ast.expr.InfixExpression;
import ca.uwaterloo.joos.ast.expr.InfixExpression.InfixOperator;
import ca.uwaterloo.joos.ast.expr.MethodInvokeExpression;
import ca.uwaterloo.joos.ast.expr.UnaryExpression;
import ca.uwaterloo.joos.ast.expr.primary.LiteralPrimary;
import ca.uwaterloo.joos.ast.statement.ReturnStatement;
import ca.uwaterloo.joos.symboltable.SemanticsVisitor;
import ca.uwaterloo.joos.symboltable.SymbolTable;

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
			
			// Add externs
			// TODO add externs
		} else if (node instanceof MethodDeclaration) {
			Modifiers modifiers = ((MethodDeclaration) node).getModifiers();
			if (!modifiers.containModifier(Modifier.NATIVE) && !modifiers.containModifier(Modifier.ABSTRACT)) {
				// Define method labels
				this.methodLabel = this.methodLable(this.getCurrentScope().getName());
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
				// TODO local variable
				
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
				asmWriter.write(line);
				asmWriter.newLine();
			}
			
			for(String line: this.data) {
				asmWriter.write(line);
				asmWriter.newLine();
			}
			asmWriter.close();
		} else if (node instanceof MethodDeclaration) {
			Modifiers modifiers = ((MethodDeclaration) node).getModifiers();
			if (!modifiers.containModifier(Modifier.NATIVE) && !modifiers.containModifier(Modifier.ABSTRACT)) {
				// Postamble
				// Pop registers
				this.texts.add("pop edx\t\t\t; Postamble");
				this.texts.add("pop ecx");
				this.texts.add("pop ebx");
				// this.texts.add("pop eax");		// Leave eax as return value 
				
				// Deallocate space for local variables
				// TODO local variables
				
				// Restore frame pointer
				this.texts.add("pop ebp");
				
				this.texts.add(this.methodLabel + "_END:");
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
	
	private String methodLable(String methodSignature) {
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
		
		// Invoke the method
		String methodName = methodInvoke.fullyQualifiedName;
		String methodLabel = this.methodLable(methodName);
		if(methodLabel.equals("java.io.OutputStream.nativeWrite_INT__")) {
			methodLabel = "NATIVEjava.io.OutputStream.nativeWrite";
		}
		this.texts.add("call " + methodLabel);
		
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
			this.texts.add("idiv ebx\t\t\t; Divid edx:eax with ebx");
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
			this.texts.add("idiv ebx\t\t\t; Divid edx:eax with ebx");
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
