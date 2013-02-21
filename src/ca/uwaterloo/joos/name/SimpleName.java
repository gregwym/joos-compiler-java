package ca.uwaterloo.joos.name;

import java.util.Set;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

public class SimpleName extends Name {

	public SimpleName(Node node, ASTNode parent) throws Exception {
		super(node, parent);

	}

}
