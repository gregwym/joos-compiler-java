package ca.uwaterloo.joos.ast.visitor;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.decl.*;
import ca.uwaterloo.joos.symbolTable.SymbolTable;

public class TopDeclVisitor extends SemanticsVisitor {

	public TopDeclVisitor(SymbolTable ist) {
		super(ist);
		// TODO Auto-generated constructor stub
	}

	public boolean visit(TypeDeclaration td){
		
		return true;
	}
	
	public boolean visit(ClassDeclaration td){
		
		return true;
	}
	
	public boolean visit(MethodDeclaration md){
		
		return true;
	}
	
	public boolean visit(VariableDeclaration vd){
		
		return true;
	}

	@Override
	public void willVisit(ASTNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void didVisit(ASTNode node) {
		// TODO Auto-generated method stub

	}

}
