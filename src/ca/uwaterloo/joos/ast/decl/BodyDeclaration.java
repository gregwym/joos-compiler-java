/**
 * 
 */
package ca.uwaterloo.joos.ast.decl;

import java.util.LinkedList;
import java.util.Queue;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

/**
 * @author Greg Wang
 *
 */
public abstract class BodyDeclaration extends ASTNode {

	/**
	 * 
	 */
	public BodyDeclaration() {
		// TODO Auto-generated constructor stub
	}
	
	public static BodyDeclaration newBodyDeclaration(Node declNode) {
		assert declNode instanceof TreeNode : "BodyDecl is expecting a TreeNode";
	
		Queue<Node> nodeQueue = new LinkedList<Node>();
		nodeQueue.offer(declNode);
		
		while (!nodeQueue.isEmpty()) {
			Node node = nodeQueue.poll();
			logger.finer("Dequeued node " + node.toString());
		
			if (node instanceof TreeNode) {
				TreeNode treeNode = (TreeNode) node;
				logger.fine("Reach: " + treeNode);
				if(treeNode.productionRule.getLefthand().equals("constructordecl")){
					return new ConstructorDeclaration();
				}
				else if(treeNode.productionRule.getLefthand().equals("methoddecl")){
					return new MethodDeclaration();
				}
				else if(treeNode.productionRule.getLefthand().equals("fielddecl")){
					return new FieldDeclaration();
				}
				else {
					for (Node n : treeNode.children) {
						nodeQueue.offer(n);
					}
				}
			}
		}
	
		return null;
	}

	@Override
	public String toString(int level) {
		String str = super.toString(level);
		str += "<" + this.getClass().getSimpleName() + "> \n";
		return str;
	}
}
