package ca.uwaterloo.joos.ast.visitor;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.ASTNode.ChildTypeUnmatchException;
import ca.uwaterloo.joos.ast.decl.MethodDeclaration;
import ca.uwaterloo.joos.ast.decl.PackageDeclaration;
import ca.uwaterloo.joos.ast.decl.TypeDeclaration;
import ca.uwaterloo.joos.symbolTable.SymbolTable;

public class DeepDeclVisitor extends SemanticsVisitor {

	private String name = null;

	public DeepDeclVisitor() {
		super();
	}

	public void willVisit(ASTNode node) throws Exception {

		if (node instanceof PackageDeclaration) {
			PackageDeclaration PNode = (PackageDeclaration) node;
			name = PNode.getPackageName();
			
			// Get the symbol table for the given package 
			// Create one if not exists
			SymbolTable table = SymbolTable.getScope(name);
			
			// Push current scope into the view stack
			this.pushScope(table);
		} else if (node instanceof TypeDeclaration) {
			SymbolTable currentScope = this.getCurrentScope();
			String name = ((TypeDeclaration) node).getIdentifier();
			name = currentScope.getName() + "." + name + "{}";
			
			// Second: get the class description scope
			SymbolTable table = SymbolTable.getScope(name);
			
			// Push current scope into the view stack
			this.pushScope(table);
		} else if (node instanceof MethodDeclaration) {
			// Make a new symbol table which builds
			String name = this.getCurrentScope().signatureOfMethod((MethodDeclaration) node);
			SymbolTable scope = SymbolTable.getScope(name);
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
			ASTVisitor blockVisitor = new BlockVisitor(this.getCurrentScope());
			node.accept(blockVisitor);
			
			return false;
		}

		return true;
	}

}
