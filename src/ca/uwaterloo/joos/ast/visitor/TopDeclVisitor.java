package ca.uwaterloo.joos.ast.visitor;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.decl.BodyDeclaration;
import ca.uwaterloo.joos.ast.decl.FieldDeclaration;
import ca.uwaterloo.joos.ast.decl.MethodDeclaration;
import ca.uwaterloo.joos.ast.decl.PackageDeclaration;
import ca.uwaterloo.joos.ast.decl.TypeDeclaration;
import ca.uwaterloo.joos.symbolTable.SymbolTable;

public class TopDeclVisitor extends SemanticsVisitor {

	private String name = null;

	public TopDeclVisitor() {
		super();
	}

	public boolean visit(ASTNode node) throws Exception {
		if (node instanceof FieldDeclaration) {
			SymbolTable currentScope = this.getCurrentScope();
			if (currentScope.getVariableDecl((FieldDeclaration) node) == null)
				currentScope.addVariableDecl((FieldDeclaration) node);
			else {
				throw new Exception("TopDeclVisitor.visit(): Multiple Field Declarations with same name. Exiting with 42");
			}
		} else if (node instanceof MethodDeclaration) {
			SymbolTable currentScope = this.getCurrentScope();
			if (currentScope.getMethod((MethodDeclaration) node) == null) {
				currentScope.addMethod((MethodDeclaration) node);
			}
			else {
				throw new Exception("Duplicate declaratio of method");
			}
		}

		return !(node instanceof BodyDeclaration);
	}

	@Override
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
			
			// First: check duplicate class
			if(SymbolTable.containScope(name)) {
				throw new Exception("Duplicate declaration of class");
			}
			
			// Second: get the class description scope
			SymbolTable table = SymbolTable.getScope(name);
			
			// Third: add type declaration as package member
			currentScope.addClass(name, node);
			
			// Push current scope into the view stack
			this.pushScope(table);
		}
	}

	@Override
	public void didVisit(ASTNode node) {
		if (node instanceof TypeDeclaration) {
//			SymbolTable typeScope = 
					this.popScope();
		}
	}

}
