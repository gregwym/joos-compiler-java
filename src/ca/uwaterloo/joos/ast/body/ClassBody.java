package ca.uwaterloo.joos.ast.body;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;

public class ClassBody extends TypeBody {

	public ClassBody(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}
}
