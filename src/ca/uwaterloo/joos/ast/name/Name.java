package ca.uwaterloo.joos.ast.name;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.expr.Expression;
import ca.uwaterloo.joos.ast.expr.Lefthand;
import ca.uwaterloo.joos.parser.ParseTree.Node;

public abstract class Name extends Expression implements Lefthand {

	public Name(Node node, ASTNode parent) throws Exception {
		super(node, parent);
		this.setIdentifier(this.getName());
	}

	public abstract String getName() throws Exception;

}
