package ca.uwaterloo.joos.ast.statement;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;

public abstract class Statement extends ASTNode {

	public Statement(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}

}
