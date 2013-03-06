package ca.uwaterloo.joos.symbolTable;

import java.util.List;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.ASTNode.ChildTypeUnmatchException;
import ca.uwaterloo.joos.ast.FileUnit;
import ca.uwaterloo.joos.ast.decl.ImportDeclaration;
import ca.uwaterloo.joos.ast.decl.MethodDeclaration;
import ca.uwaterloo.joos.ast.decl.OnDemandImport;
import ca.uwaterloo.joos.ast.decl.PackageDeclaration;
import ca.uwaterloo.joos.ast.decl.SingleImport;
import ca.uwaterloo.joos.ast.decl.TypeDeclaration;
import ca.uwaterloo.joos.ast.visitor.ASTVisitor;

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
			
			table.appendScope(currentScope);
			
			List<ImportDeclaration> imports = ((FileUnit) node.getParent()).getImportDeclarations();
			for(ImportDeclaration anImport: imports) {
				if(anImport instanceof SingleImport) {
					SymbolTable importTable = SymbolTable.getScope(anImport.getImportName().getName() + "{}");
					if(importTable != null) {
						table.addPublicMembers(importTable);
					}
					else {
						throw new Exception("Unknown Single Import " + anImport.getIdentifier());
					}
				} else if(anImport instanceof OnDemandImport) {
					SymbolTable importTable = SymbolTable.getScope(anImport.getImportName().getName());
					if(importTable != null) {
						table.addPublicMembers(importTable);
					}
					else {
						throw new Exception("Unknown Single Import " + anImport.getIdentifier());
					}
				}
			}
			
			// Push current scope into the view stack
			this.pushScope(table);
		} else if (node instanceof MethodDeclaration) {
			// Make a new symbol table which builds
			String name = this.getCurrentScope().signatureOfMethod((MethodDeclaration) node);
			SymbolTable scope = SymbolTable.getScope(name);
			
			scope.appendScope(this.getCurrentScope());
			
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
			ASTVisitor blockVisitor = new BlockVisitor(this.viewStack);
			node.accept(blockVisitor);
			
			return false;
		}

		return true;
	}

}
