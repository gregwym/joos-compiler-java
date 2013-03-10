package ca.uwaterloo.joos.symboltable;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.decl.BodyDeclaration;
import ca.uwaterloo.joos.ast.decl.FieldDeclaration;
import ca.uwaterloo.joos.ast.decl.MethodDeclaration;
import ca.uwaterloo.joos.ast.decl.TypeDeclaration;

public class TopDeclVisitor extends SemanticsVisitor {

	public TopDeclVisitor(SymbolTable table) {
		super(table);
	}

	public boolean visit(ASTNode node) throws Exception {
		if (node instanceof FieldDeclaration) {
			TypeScope currentScope = (TypeScope) this.getCurrentScope();
			currentScope.addFieldDecl((FieldDeclaration) node);
		} else if (node instanceof MethodDeclaration) {
			TypeScope currentScope = (TypeScope) this.getCurrentScope();
			currentScope.addMethod((MethodDeclaration) node);
		}

		return !(node instanceof BodyDeclaration);
	}

	@Override
	public void willVisit(ASTNode node) throws Exception {
		if (node instanceof TypeDeclaration) {
			PackageScope currentScope = (PackageScope) this.getCurrentScope();
			String name = ((TypeDeclaration) node).getIdentifier();
			name = currentScope.getName() + "." + name;

			TypeScope scope = this.table.addType(name, currentScope);

			// Add type declaration as package member
			currentScope.addType((TypeDeclaration) node);

			// Push current scope into the view stack
			this.pushScope(scope);
		} else {
			super.willVisit(node);
		}
	}
}
