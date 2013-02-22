/**
 * 
 */
package ca.uwaterloo.joos.ast.expr;

import java.util.ArrayList;
import java.util.List;

import ca.uwaterloo.joos.ast.AST.ASTConstructException;
import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;
import ca.uwaterloo.joos.parser.ParseTreeTraverse;
import ca.uwaterloo.joos.parser.ParseTreeTraverse.Traverser;

/**
 * @author Greg Wang
 *
 */
public class Expression extends ASTNode {

	public Expression(Node node, ASTNode parent) throws Exception {
		super(node, parent);
		// TODO Auto-generated constructor stub
	}
	

	private static class ExpressionTraverser implements Traverser {
		public ASTNode node;
		private ASTNode parent;
		
		public ExpressionTraverser(ASTNode parent) {
			this.parent = parent;
		}

		@Override
		public List<Node> processTreeNode(TreeNode treeNode) throws Exception {
			if (treeNode.productionRule.getLefthand().equals("assignexpr")) {
//				node = new AssignmentExpression(treeNode, parent);
			} else {
				List<Node> offers = new ArrayList<Node>();
				offers.addAll(treeNode.children);
				return offers;
			}
			return null;
		}

		@Override
		public void processLeafNode(LeafNode leafNode) throws Exception {
			
		}
	}
	
	public static ASTNode newExpression(Node node, ASTNode parent) throws Exception {
		
		ExpressionTraverser traverser = new ExpressionTraverser(parent);
		ParseTreeTraverse traverse = new ParseTreeTraverse(traverser);
		traverse.traverse(node);
		
		if (traverser.node == null) {
			throw new ASTConstructException(node + " is not / does not contain a primary");
		}
		
		return traverser.node;
	}

}
