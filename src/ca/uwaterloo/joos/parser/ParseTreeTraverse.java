/**
 * 
 */
package ca.uwaterloo.joos.parser;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

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

	public ParseTreeTraverse(Traverser traverser) {
		this.traverser = traverser;
	}

	public void traverse(Node root) throws Exception {
		Queue<Node> nodeQueue = new LinkedList<Node>();
		nodeQueue.offer(root);

		while (!nodeQueue.isEmpty()) {
			Node node = nodeQueue.poll();

			if (node instanceof TreeNode) {
				TreeNode treeNode = (TreeNode) node;
				List<Node> offer = this.traverser.processTreeNode(treeNode);
				if (offer != null)
					for (Node n : offer) {
						nodeQueue.offer(n);
					}
			} else if (node instanceof LeafNode) {
				LeafNode leafNode = (LeafNode) node;
				this.traverser.processLeafNode(leafNode);
			}
		}
	}
}
