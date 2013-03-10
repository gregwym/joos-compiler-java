package ca.uwaterloo.joos.symboltable;

import ca.uwaterloo.joos.ast.ASTNode;


public class TableEntry{
	//An entry in the symbols hash map

	private String name;
	private ASTNode node;
	
	public TableEntry(String name, ASTNode inode){
		this.name = name;
		this.node = inode;
	}
	
	public ASTNode getNode(){
		return node;
	}

	public String getName() {
		return name;
	}

}
