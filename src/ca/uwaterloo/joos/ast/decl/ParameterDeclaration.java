package ca.uwaterloo.joos.ast.decl;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;

public class ParameterDeclaration extends VariableDeclaration {

	public ParameterDeclaration(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}

}
