package ca.uwaterloo.joos.ast.name;

import java.util.ArrayList;
import java.util.List;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

public abstract class Name extends ASTNode {

	public Name(Node node, ASTNode parent) throws Exception {
		super(node, parent);
		this.setIdentifier(this.getName());
	}

	public abstract String getName() throws Exception;

	@Override
	public List<Node> processTreeNode(TreeNode treeNode) throws Exception {
		List<Node> offers = new ArrayList<Node>();
		offers.addAll(treeNode.children);
		return offers;
	}

}
