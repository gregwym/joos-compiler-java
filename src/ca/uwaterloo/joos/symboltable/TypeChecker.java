package ca.uwaterloo.joos.symboltable;

import java.util.Stack;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.decl.FieldDeclaration;
import ca.uwaterloo.joos.ast.decl.TypeDeclaration;
import ca.uwaterloo.joos.ast.decl.VariableDeclaration;
import ca.uwaterloo.joos.ast.expr.AssignmentExpression;
import ca.uwaterloo.joos.ast.expr.CastExpression;
import ca.uwaterloo.joos.ast.expr.ClassCreateExpression;
import ca.uwaterloo.joos.ast.expr.InfixExpression;
import ca.uwaterloo.joos.ast.expr.MethodInvokeExpression;
import ca.uwaterloo.joos.ast.expr.UnaryExpression;
import ca.uwaterloo.joos.ast.expr.UnaryExpression.UnaryOperator;
import ca.uwaterloo.joos.ast.expr.name.QualifiedName;
import ca.uwaterloo.joos.ast.expr.name.SimpleName;
import ca.uwaterloo.joos.ast.expr.primary.ArrayAccess;
import ca.uwaterloo.joos.ast.expr.primary.ArrayCreate;
import ca.uwaterloo.joos.ast.expr.primary.FieldAccess;
import ca.uwaterloo.joos.ast.expr.primary.LiteralPrimary;
import ca.uwaterloo.joos.ast.expr.primary.ThisPrimary;
import ca.uwaterloo.joos.ast.statement.Block;
import ca.uwaterloo.joos.ast.type.ArrayType;
import ca.uwaterloo.joos.ast.type.PrimitiveType;
import ca.uwaterloo.joos.ast.type.PrimitiveType.Primitive;
import ca.uwaterloo.joos.ast.type.ReferenceType;
import ca.uwaterloo.joos.ast.type.Type;

public class TypeChecker extends SemanticsVisitor {
	
	private Stack<Type> typeStack;
	private int checkType = 0;

	public TypeChecker(SymbolTable table) {
		super(table);
		this.typeStack = new Stack<Type>();
	}

	@Override
	public boolean visit(ASTNode node) throws Exception {
		if(node instanceof TypeDeclaration) {
			if(this.getCurrentScope().getName().contains("java")) return false;
		}
		if(node instanceof Type) {
			return false;
		}
		return super.visit(node);
	}

	@Override
	public void willVisit(ASTNode node) throws Exception {
		if(node instanceof FieldDeclaration || node instanceof Block) {
			this.checkType++;
		}
		super.willVisit(node);
	}
	
	protected void pushType(Type type) throws Exception {
		logger.info("Pushing " + type.getFullyQualifiedName());
		this.typeStack.push(type);
	}
	
	protected Type popType() throws Exception {
		Type type = this.typeStack.pop();
		logger.info("Poping " + type.getFullyQualifiedName());
		return type;
	}
	
	protected Type getCurrentType() {
		if(!this.typeStack.isEmpty()) return this.typeStack.peek();
		return null;
	}

	@Override
	public void didVisit(ASTNode node) throws Exception {
		if(this.checkType == 0) {
			super.didVisit(node);
			return;
		}
		
		logger.info("Did visit " + node);
		
		/* Type Providers */
		if(node instanceof ClassCreateExpression) {
			this.pushType(((ClassCreateExpression) node).getType());
		} else if(node instanceof LiteralPrimary) {
			this.pushType(((LiteralPrimary) node).getType());
		} else if(node instanceof ThisPrimary) {
			String typeName = this.getParentTypeScope().getName();
			this.pushType(new ReferenceType(typeName, node));
		} else if(node instanceof SimpleName) {
			Type nameType = ((SimpleName) node).getOriginalDeclaration().getType();
			this.pushType(nameType);
		} else if(node instanceof QualifiedName) {
			// TODO
		}
		
		/* Type Consumers and Providers */
		else if(node instanceof ArrayAccess) {
			Type arrayType = this.popType();
			if(!(arrayType instanceof ArrayType)) {
				throw new Exception("Accessing array, but got type " + arrayType.getFullyQualifiedName());
			}
			this.pushType(((ArrayType) arrayType).getType());
		}
		else if(node instanceof ArrayCreate) {
			Type dimType = this.popType();
			if(dimType == null) {
				throw new Exception("Dimension of array expecting INT but got NULL");
			} else if(!dimType.getFullyQualifiedName().equals("INT")) {
				throw new Exception("Dimension of array expecting INT but got " + dimType.getFullyQualifiedName());
			}
			this.pushType(((ArrayCreate) node).getType());
		} else if(node instanceof UnaryExpression) {
			Type exprType = this.popType();
			UnaryOperator operator = ((UnaryExpression) node).getOperator(); 
			if(operator.equals(UnaryOperator.NOT) && !exprType.getFullyQualifiedName().equals("BOOLEAN")) {
				throw new Exception("Unary Expression NOT expecting BOOLEAN but got " + exprType.getFullyQualifiedName());
			} else {
				this.pushType(exprType);
			}
			
			if(operator.equals(UnaryOperator.MINUS)) {
				if(!(exprType instanceof PrimitiveType) || exprType.getFullyQualifiedName().equals("BOOLEAN")) {
					throw new Exception("Unary Expression MINUS expecting BYTE, CHAR, INT or SHORT but got " + exprType.getFullyQualifiedName());
				}
				this.pushType(new PrimitiveType(Primitive.INT, node));
			}
		} else if(node instanceof CastExpression) {
			Type exprType = this.popType();
			Type castType = ((CastExpression) node).getType();
			// TODO: Check whether is valid casting;
			this.pushType(castType);
		} else if(node instanceof InfixExpression) {
			InfixExpression infix = (InfixExpression) node;
			
			Type op2Type = this.popType();
			Type op1Type = this.popType();
			// TODO: Check operands type
			if(infix.getOperator().equals(InfixExpression.InfixOperator.INSTANCEOF)) {
				this.pushType(new PrimitiveType(PrimitiveType.Primitive.BOOLEAN, infix));
			}
			else if(op1Type.equals(op2Type)){
				this.pushType(op1Type);
			} else {
				throw new Exception("Infix trying to " + infix.getOperator().name() + " " + op1Type.getFullyQualifiedName() + " with " + op2Type.getFullyQualifiedName());
			}
		} else if(node instanceof MethodInvokeExpression) {
			// TODO
//			Scope methodScope = this.getCurrentScope();
//			List<Type> argumentTypes = Lambda.extract(methodScope.getSymbols().values(), Lambda.on(TableEntry.class).getType());
//			Stack<Type> backToFront = new Stack<Type>();
//			backToFront.addAll(argumentTypes);
//
//			while (!backToFront.isEmpty()) {
//				Type exprType = this.popType();
//				Type argType = backToFront.pop();
//				if (!exprType.equals(argType)) {
//					throw new Exception("Method " + methodScope.getName() + " expecting " + argType + " but got " + exprType);
//				}
//			}
		} else if(node instanceof FieldAccess) {
			// TODO
		}
		
		/* Type Consumers */
		else if(node instanceof VariableDeclaration) {
			if(((VariableDeclaration) node).getInitial() == null) {
				this.popType();
			} else {
				Type initType = this.popType();
				Type varType = this.popType();
				if(!initType.equals(varType)) {
					throw new Exception("Initialing " + varType.getFullyQualifiedName() + " with " + initType.getFullyQualifiedName());
				}
			}
		} 
		else if(node instanceof AssignmentExpression) {
			Type exprType = this.popType();
			Type lhsType = this.popType();
			if(!exprType.equals(lhsType)) {
				throw new Exception("Assigning " + lhsType.getFullyQualifiedName() + " with " + exprType.getFullyQualifiedName());
			}
		}
		
		if(node instanceof FieldDeclaration || node instanceof Block) {
			this.checkType--;
		}
		
		super.didVisit(node);
	}

}
