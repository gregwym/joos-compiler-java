
/**
 * 
 */
package ca.uwaterloo.joos.parser;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import ca.uwaterloo.joos.ast.AST.ASTConstructException;
import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;


/**
 * @author Greg Wang
 *
 */
public class ParseTreeTraverse {
	
	public static abstract class Traverser {
		protected ASTNode parent;
		
		public Traverser(ASTNode parent) {
			this.parent = parent;
		}
		
		public abstract Set<Node> processTreeNode(TreeNode treeNode) throws ASTConstructException;
		public abstract void processLeafNode(LeafNode treeNode) throws ASTConstructException;
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
