package ca.uwaterloo.joos.ast.expr.primary;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;

public class ThisPrimary extends Primary {

	public ThisPrimary(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}

}
