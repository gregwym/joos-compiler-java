package ca.uwaterloo.joos.ast.visitor;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.decl.MethodDeclaration;

public abstract class MethodDeclVisitor extends ASTVisitor {

	public MethodDeclVisitor() {

	}

	@Override
	public void willVisit(ASTNode node) {

	}

	@Override
	public void didVisit(ASTNode node) {

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
