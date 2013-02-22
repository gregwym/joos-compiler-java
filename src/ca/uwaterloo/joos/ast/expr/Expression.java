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
public abstract class Expression extends ASTNode {

	public Expression(Node node, ASTNode parent) throws Exception {
		super(node, parent);
		// TODO Auto-generated constructor stub
	}

	private static class ExpressionTraverser implements Traverser {
		public Expression node;
		private ASTNode parent;

		public ExpressionTraverser(ASTNode parent) {
			this.parent = parent;
		}

		@Override
		public List<Node> processTreeNode(TreeNode treeNode) throws Exception {
			int numOfChild = treeNode.children.size();
			if (numOfChild == 3) {
				String kind = treeNode.getKind();
				if (kind.equals("assign")) {
					node = new AssignmentExpression(treeNode, parent);
				} else if (InfixExpression.getAcceptingKinds().contains(kind)) {
					node = new InfixExpression(treeNode, parent);
				}
			} else if (treeNode.getKind().equals("classcreateexpr")) {
				node = new ClassCreateExpression(treeNode, parent);
			} else if (treeNode.getKind().equals("methodinvoke")) {
				node = new MethodInvokeExpression(treeNode, parent);
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

	public static Expression newExpression(Node node, ASTNode parent) throws Exception {

		ExpressionTraverser traverser = new ExpressionTraverser(parent);
		ParseTreeTraverse traverse = new ParseTreeTraverse(traverser);
		traverse.traverse(node);

		if (traverser.node == null) {
			throw new ASTConstructException(node + " is not / does not contain an expression");
		}

		return traverser.node;
	}

}
