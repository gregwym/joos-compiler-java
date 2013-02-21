package ca.uwaterloo.joos.name;

import java.util.HashSet;
import java.util.Set;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

public class Name extends ASTNode {

	public Name(Node node, ASTNode parent) throws Exception {
		super(node, parent);

	}

	@Override
	public Set<Node> processTreeNode(TreeNode treeNode) throws Exception {
		Set<Node> offers = new HashSet<Node>();
		for (Node n : treeNode.children)
			offers.add(n);
		return offers;
	}

	@Override
	public void processLeafNode(LeafNode leafNode) throws Exception {
		if (leafNode.token.getKind().equals("ID")) {
			setIdentifier(leafNode.token.getLexeme());
		}
	}

}
