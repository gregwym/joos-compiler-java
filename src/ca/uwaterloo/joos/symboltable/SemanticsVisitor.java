package ca.uwaterloo.joos.symboltable;

import java.util.Stack;
import java.util.logging.Logger;

import ca.uwaterloo.joos.Main;
import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.decl.PackageDeclaration;
import ca.uwaterloo.joos.ast.decl.TypeDeclaration;
import ca.uwaterloo.joos.ast.visitor.ASTVisitor;

public abstract class SemanticsVisitor extends ASTVisitor {
	// SYMBOLTABLE - Link to the global SymbolTable
	
	protected static Logger logger = Main.getLogger(SymbolTable.class);
	protected Stack<Scope> viewStack;
	protected SymbolTable table;

	public SemanticsVisitor(SymbolTable table) {
		this.viewStack = new Stack<Scope>();
		this.table = table;
	}
	
	protected Scope getCurrentScope() {
		return this.viewStack.peek();
	}
	
	protected void pushScope(Scope table) {
		logger.finer("Pushing scope " + table.toString());
		this.viewStack.push(table);
	}
	
	protected Scope popScope() {
		Scope scope = this.viewStack.pop();
		logger.finer("Popping scope " + scope);
		return scope;
	}
	
	@Override 
	public void willVisit(ASTNode node) throws Exception {
		if (node instanceof PackageDeclaration) {
			PackageScope scope = this.table.getPackageByDecl((PackageDeclaration) node);
			this.pushScope(scope);
		} else if (node instanceof TypeDeclaration) {
			PackageScope currentScope = (PackageScope) this.getCurrentScope();
			String name = ((TypeDeclaration) node).getIdentifier();
			name = currentScope.getName() + "." + name;
			
			TypeScope scope = this.table.getType(name);
			this.pushScope(scope);
		}
	}
	
	@Override
	public void didVisit(ASTNode node) {
		if (node instanceof TypeDeclaration) {
			this.popScope();
		}
	}
}
