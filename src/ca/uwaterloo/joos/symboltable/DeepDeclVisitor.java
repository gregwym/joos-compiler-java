package ca.uwaterloo.joos.symboltable;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.ASTNode.ChildTypeUnmatchException;
import ca.uwaterloo.joos.ast.decl.MethodDeclaration;
import ca.uwaterloo.joos.ast.decl.PackageDeclaration;
import ca.uwaterloo.joos.ast.decl.TypeDeclaration;
import ca.uwaterloo.joos.ast.visitor.ASTVisitor;

public class DeepDeclVisitor extends SemanticsVisitor {

	public DeepDeclVisitor(SymbolTable table) {
		super(table);
	}

	public void willVisit(ASTNode node) throws Exception {

		if (node instanceof PackageDeclaration) {
			PackageDeclaration packDecl = (PackageDeclaration) node;
			
			// Get the symbol table for the given package 
			// Create one if not exists
			Scope scope = this.table.getPackageByDecl(packDecl);
			
			// Push current scope into the view stack
			this.pushScope(scope);
		} else if (node instanceof TypeDeclaration) {
			PackageScope currentScope = (PackageScope) this.getCurrentScope();
			String name = currentScope.getName() + "." + ((TypeDeclaration) node).getIdentifier();
			
			// Get the scope
			TypeScope scope = this.table.getType(name);
			
			scope.setWithinPackage(currentScope);
			
			// Push current scope into the view stack
			this.pushScope(scope);
		} else if (node instanceof MethodDeclaration) {
			// Make a new symbol table which builds
			String name = this.getCurrentScope().signatureOfMethod((MethodDeclaration) node);
			Scope scope = this.table.getScope(name);
			
			scope.appendScope(this.getCurrentScope(), 0);
			
			this.pushScope(scope);
		}
		
	}

	public void didVisit(ASTNode node) {
		if (node instanceof TypeDeclaration) {
			this.popScope();
		} else if (node instanceof MethodDeclaration) {
			this.popScope();
		}
	}

	public boolean visit(ASTNode node) throws ChildTypeUnmatchException, Exception {
		if (node instanceof MethodDeclaration) {
			ASTVisitor blockVisitor = new BlockVisitor(this.viewStack, this.table);
			node.accept(blockVisitor);
			
			return false;
		}

		return true;
	}

}
