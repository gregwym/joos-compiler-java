package ca.uwaterloo.joos.ast.decl;

import java.util.Set;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

public class PackageDeclaration extends ASTNode {
	public PackageDeclaration(Node node, ASTNode parent) throws Exception {
		super(node, parent);
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
