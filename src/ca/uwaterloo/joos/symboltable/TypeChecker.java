package ca.uwaterloo.joos.symboltable;

import ca.uwaterloo.joos.ast.ASTNode;

public class TypeChecker extends SemanticsVisitor {

	public TypeChecker(SymbolTable table) {
		super(table);
	}

	@Override
	public boolean visit(ASTNode node) throws Exception {
		
		return super.visit(node);
	}

	@Override
	public void willVisit(ASTNode node) throws Exception {
		// TODO Auto-generated method stub
		super.willVisit(node);
	}

	@Override
	public void didVisit(ASTNode node) throws Exception {
		
		super.didVisit(node);
	}

}
