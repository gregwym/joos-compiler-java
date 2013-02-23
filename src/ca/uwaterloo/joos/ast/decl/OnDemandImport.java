package ca.uwaterloo.joos.ast.decl;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;

public class OnDemandImport extends ImportDeclaration {

	public OnDemandImport(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}

}
