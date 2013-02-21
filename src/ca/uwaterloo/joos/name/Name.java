package ca.uwaterloo.joos.name;

import java.util.HashSet;
import java.util.Set;

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
	public Set<Node> processTreeNode(TreeNode treeNode) throws Exception {
		Set<Node> offers = new HashSet<Node>();
		for (Node n : treeNode.children)
			offers.add(n);
		return offers;
	}

}
