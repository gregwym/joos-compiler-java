package ca.uwaterloo.joos.name;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;

public class QualifiedName extends Name {

	public QualifiedName(Node qualifiedName, ASTNode parent) throws Exception {
		super(qualifiedName, parent);

	}

	public String getQualifiedName() {
		return getIdentifier();
	}

}
