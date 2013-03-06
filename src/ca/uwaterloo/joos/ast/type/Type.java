package ca.uwaterloo.joos.ast.type;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;

public abstract class Type extends ASTNode {

	public Type(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}
	
	@Override
	public abstract String getIdentifier();

}
