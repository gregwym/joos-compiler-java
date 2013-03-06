package ca.uwaterloo.joos.ast.visitor;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.ASTNode.ChildTypeUnmatchException;
import ca.uwaterloo.joos.ast.decl.LocalVariableDeclaration;
import ca.uwaterloo.joos.symbolTable.SymbolTable;

public class BlockVisitor extends SemanticsVisitor{
	private int level = 0;
	public BlockVisitor(SymbolTable ist) {
		super(ist);
		
	}
	
	
	public boolean visit(ASTNode node) throws ChildTypeUnmatchException, Exception{

		if (node instanceof LocalVariableDeclaration){
			
			//TODO Check for multiple definitions
			//if name is not already in view
			LocalVariableDeclaration LNode = (LocalVariableDeclaration) node;
			if (st.hasField(LNode.getName().getName())) {
				System.err.println("Multiple Declarations Exit 42");
				System.exit(42);
			}
			st.addDeclaration(LNode.getName().getName(), node, level);
			level++;
			return false;
		}
		
		
		
		return true;
	}
	
	
	
	@Override
	public void willVisit(ASTNode node){
		
	}
	
	@Override
	public void didVisit(ASTNode node){
		
	}

}
