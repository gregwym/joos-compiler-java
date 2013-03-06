package ca.uwaterloo.joos.ast.visitor;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.decl.MethodDeclaration;
import ca.uwaterloo.joos.ast.expr.primary.LiteralPrimary;

public abstract class MethodDeclVisitor extends ASTVisitor {

	public MethodDeclVisitor() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void willVisit(ASTNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void didVisit(ASTNode node) {
		// TODO Auto-generated method stub

	}
	public boolean visit(ASTNode node) throws Exception {

		if (node instanceof MethodDeclaration) {
			MethodDeclaration methodDeclNode = (MethodDeclaration) node;
			
				this.visitMethodDecl(methodDeclNode);
		}
		return true;
	}

	protected abstract void visitMethodDecl(MethodDeclaration node) throws Exception;
}
