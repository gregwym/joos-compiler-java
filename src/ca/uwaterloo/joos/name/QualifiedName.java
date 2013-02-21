package ca.uwaterloo.joos.name;

import java.util.Set;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

public class QualifiedName extends Name {

	public QualifiedName(Node qualifiedName, ASTNode parent) throws Exception {
		super(qualifiedName, parent);

	}

	public String getQualifiedName() {
		return getIdentifier();
	}

	public void setQualifiedName(String qualifiedName) {
		setIdentifier(qualifiedName);
	}

}
