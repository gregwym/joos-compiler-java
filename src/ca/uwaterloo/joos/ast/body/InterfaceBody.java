package ca.uwaterloo.joos.ast.body;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;


public class InterfaceBody extends TypeBody {
	public InterfaceBody(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}
}
