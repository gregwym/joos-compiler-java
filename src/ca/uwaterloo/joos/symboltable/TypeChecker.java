package ca.uwaterloo.joos.symboltable;

import java.util.Stack;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.decl.TypeDeclaration;
import ca.uwaterloo.joos.ast.decl.VariableDeclaration;
import ca.uwaterloo.joos.ast.expr.AssignmentExpression;
import ca.uwaterloo.joos.ast.expr.ClassCreateExpression;
import ca.uwaterloo.joos.ast.expr.primary.ArrayCreate;
import ca.uwaterloo.joos.ast.expr.primary.LiteralPrimary;
import ca.uwaterloo.joos.ast.type.Type;

public class TypeChecker extends SemanticsVisitor {
	
	private Stack<Type> typeStack;

	public TypeChecker(SymbolTable table) {
		super(table);
		this.typeStack = new Stack<Type>();
	}

	@Override
	public boolean visit(ASTNode node) throws Exception {
		if(node instanceof TypeDeclaration) {
			if(this.getCurrentScope().getName().contains("java")) return false;
		}
		return super.visit(node);
	}

	@Override
	public void willVisit(ASTNode node) throws Exception {
		// TODO Auto-generated method stub
		super.willVisit(node);
	}
	
	protected void pushType(Type type) {
		this.typeStack.push(type);
	}
	
	protected Type popType() {
		Type type = this.typeStack.pop();
		return type;
	}
	
	protected Type getCurrentType() {
		if(!this.typeStack.isEmpty()) return this.typeStack.peek();
		return null;
	}

	@Override
	public void didVisit(ASTNode node) throws Exception {
		/* Type Providers */
		if(node instanceof ClassCreateExpression) {
			this.pushType(((ClassCreateExpression) node).getType());
		} else if(node instanceof LiteralPrimary) {
			this.pushType(((LiteralPrimary) node).getType());
		} 
		
		/* Type Consumers and Providers */
		else if(node instanceof ArrayCreate) {
			Type dimType = this.popType();
			if(!dimType.getFullyQualifiedName().equals("INT")) {
				throw new Exception("Dimension of array expecting INT but got" + dimType.getFullyQualifiedName());
			}
			this.pushType(((ArrayCreate) node).getType());
		}
		
		/* Type Consumers */
		else if(node instanceof VariableDeclaration) {
			Type initType = this.popType();
			Type varType = ((VariableDeclaration) node).getType();
			if(!initType.getFullyQualifiedName().equals(varType.getFullyQualifiedName())) {
				throw new Exception("Initialing " + varType.getFullyQualifiedName() + " with " + initType.getFullyQualifiedName());
			}
		} 
		else if(node instanceof AssignmentExpression) {
			Type exprType = this.popType();
			Type lhsType = this.popType();
			if(!exprType.getFullyQualifiedName().equals(lhsType.getFullyQualifiedName())) {
				throw new Exception("Assigning " + lhsType + " with " + exprType);
			}
		}
		super.didVisit(node);
	}

}
