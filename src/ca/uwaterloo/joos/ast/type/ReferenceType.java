package ca.uwaterloo.joos.ast.type;

import java.util.Set;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

public class ReferenceType extends Type {

	public ReferenceType(Node referenceNod, ASTNode parent) throws Exception {
		super(referenceNod, parent);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Set<Node> processTreeNode(TreeNode treeNode) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processLeafNode(LeafNode leafNode) throws Exception {
		// TODO Auto-generated method stub

	}

}
