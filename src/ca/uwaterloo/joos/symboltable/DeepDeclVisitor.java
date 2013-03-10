package ca.uwaterloo.joos.symboltable;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.ASTNode.ChildTypeUnmatchException;
import ca.uwaterloo.joos.ast.decl.MethodDeclaration;
import ca.uwaterloo.joos.ast.statement.Block;

public class DeepDeclVisitor extends SemanticsVisitor {
	
	private int blockCount;

	public DeepDeclVisitor(SymbolTable table) {
		super(table);
		this.blockCount = 0;
	}

	@Override
	public void willVisit(ASTNode node) throws Exception {

		if (node instanceof MethodDeclaration) {
			TypeScope currentScope = (TypeScope) this.getCurrentScope();
			// Make a new symbol table which builds
			String name = currentScope.signatureOfMethod((MethodDeclaration) node);
			Scope scope = this.table.addBlock(name, currentScope);
						
			this.pushScope(scope);
		} else {
			super.willVisit(node);
		}
		
	}

	@Override
	public boolean visit(ASTNode node) throws ChildTypeUnmatchException, Exception {
		if (node instanceof MethodDeclaration) {
			
			return false;
		}

		return true;
	}

	@Override
	public void didVisit(ASTNode node) {
		if (node instanceof MethodDeclaration) {
			this.popScope();
		} else if (node instanceof Block) {
			this.popScope();
		} else {
			super.didVisit(node);
		}
	}
}
