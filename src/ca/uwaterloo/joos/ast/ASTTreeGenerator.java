package ca.uwaterloo.joos.ast;

import java.util.ArrayList;
import java.util.List;

import ca.uwaterloo.joos.parser.ParseTree;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

public class ASTTreeGenerator {
	// private ASTTree astTree;
	private FileNode fileNode;
	private static List<Node> targetNode = new ArrayList<Node>();

	public void GenerateASTTree(ParseTree parseTree) {
		// astTree = new ASTTree((TreeNode) parseTree.root);
		Node parseTreeRoot = parseTree.root;
		if (parseTreeRoot instanceof TreeNode) {
			List<Node> children = ((TreeNode) parseTreeRoot).children;
			// List<Node> targetNode = null;
			for (Node child : children) {
				if (child instanceof TreeNode) {
					TreeNode childTreeNode = (TreeNode) child;
					if (childTreeNode.productionRule.getLefthand().equals(
							"file")) {
						fileNode = new FileNode(childTreeNode);
					}
				}
			}

		}

	}

}
