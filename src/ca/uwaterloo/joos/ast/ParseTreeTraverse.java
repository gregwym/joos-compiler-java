/**
 * 
 */
package ca.uwaterloo.joos.ast;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import ca.uwaterloo.joos.ast.AST.ASTConstructException;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

/**
 * @author Greg Wang
 *
 */
public class ParseTreeTraverse extends ASTNode {
	
	public static interface Traverser {
		public Set<Node> processTreeNode(TreeNode treeNode) throws ASTConstructException;
		public void processLeafNode(LeafNode treeNode) throws ASTConstructException;
	}
	
	private Traverser traverser;
	
	public ParseTreeTraverse(Traverser traverser) {
		this.traverser = traverser;
	}

	public void traverse(Node root) throws ASTConstructException {
		Queue<Node> nodeQueue = new LinkedList<Node>();
		nodeQueue.offer(root);

		while (!nodeQueue.isEmpty()) {
			Node node = nodeQueue.poll();
			logger.finer("Dequeued node " + node.toString());
			
			if (node instanceof TreeNode) {
				TreeNode treeNode = (TreeNode) node;
				Set<Node> offer = this.traverser.processTreeNode(treeNode);
				for(Node n: offer) {
					nodeQueue.offer(n);
				}
			}
			else if(node instanceof LeafNode) {
				LeafNode leafNode = (LeafNode) node;
				this.traverser.processLeafNode(leafNode);
			}
		}
	}
}
