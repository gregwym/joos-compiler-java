package ca.uwaterloo.joos.ast.visitor;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.decl.TypeDeclaration;

public abstract class TypeDeclVisitor extends ASTVisitor {

	@Override
	public void willVisit(ASTNode node) throws Exception {

	}

	@Override
	public void didVisit(ASTNode node) throws Exception {

	}
	public boolean visit(ASTNode node) throws Exception {

		if (node instanceof TypeDeclaration) {
			TypeDeclaration typeDeclNode = (TypeDeclaration) node;

			this.visitClassDecl(typeDeclNode);
		}
		return true;
	}

	protected abstract void visitClassDecl(TypeDeclaration node) throws Exception;

}
