package ca.uwaterloo.joos.ast.expr;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;

public class ClassCreateExpression extends MethodInvokeExpression {

	public ClassCreateExpression(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}

}
