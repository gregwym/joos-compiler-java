package ca.uwaterloo.joos.name;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;

public class SimpleName extends Name {

	public SimpleName(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}

}
