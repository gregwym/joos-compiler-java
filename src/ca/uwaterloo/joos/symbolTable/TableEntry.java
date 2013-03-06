package ca.uwaterloo.joos.symbolTable;

import ca.uwaterloo.joos.ast.ASTNode;


public class TableEntry{
	//An entry mapped to in the symboltable hashmap
	//TODO remove.
	private ASTNode 	node;
	private int 		local_level = 0;			//For a local symbol declaration
	
	public ASTNode getNode(){
		return node;
	}
	
	public int getLevel(){
		return local_level;
	}
	
	public TableEntry(ASTNode inode){
		node = inode;
		local_level = 0;
	}
	
	public void setLevel(int level){
		local_level = level;
	}

}
