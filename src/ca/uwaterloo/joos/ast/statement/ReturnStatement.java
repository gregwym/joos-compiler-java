package ca.uwaterloo.joos.ast.statement;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;

public class ReturnStatement extends ExpressionStatement {

	public ReturnStatement(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}

}
