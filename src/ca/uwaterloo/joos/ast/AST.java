package ca.uwaterloo.joos.ast;

import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

public class ASTTree {
	
	private ASTNode astRoot;
	
	public ASTNode getRoot(){
		return astRoot;
	}
	public ASTTree(ASTNode root){
		this.astRoot =root;
	
	}
	
}
