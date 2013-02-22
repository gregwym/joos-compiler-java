/**
 * 
 */
package ca.uwaterloo.joos.parser;

import java.util.List;
import java.util.Stack;

import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

import com.google.common.collect.Lists;

/**
 * @author Greg Wang
 * 
 */
public class ParseTreeTraverse {

	public static interface Traverser {
		public List<Node> processTreeNode(TreeNode treeNode) throws Exception;

		public void processLeafNode(LeafNode leafNode) throws Exception;
	}

	private Traverser traverser;

	/**
	 * @return the traverser
	 */
	public Traverser getTraverser() {
		return traverser;
	}

	/**
	 * @param traverser the traverser to set
	 */
	public void setTraverser(Traverser traverser) {
		this.traverser = traverser;
	}

	public ParseTreeTraverse(Traverser traverser) {
		this.traverser = traverser;
	}

	public void traverse(Node root) throws Exception {
		Stack<Node> nodeStack = new Stack<Node>();
		if (root != null)
			nodeStack.push(root);

		while (!nodeStack.isEmpty()) {
			Node node = nodeStack.pop();

			if (node instanceof TreeNode) {
				TreeNode treeNode = (TreeNode) node;
				List<Node> offer = this.traverser.processTreeNode(treeNode);
				if (offer != null) {
					nodeStack.addAll(Lists.reverse(offer));
				}
			} else if (node instanceof LeafNode) {
				LeafNode leafNode = (LeafNode) node;
				this.traverser.processLeafNode(leafNode);
			}
		}
	}
}
