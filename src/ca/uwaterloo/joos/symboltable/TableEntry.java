package ca.uwaterloo.joos.symboltable;

import ca.uwaterloo.joos.ast.ASTNode;


public class TableEntry{
	//An entry in the symbols hash map

	private ASTNode node;
	
	public ASTNode getNode(){
		return node;
	}
	
	public TableEntry(ASTNode inode){
		node = inode;
	}

}
