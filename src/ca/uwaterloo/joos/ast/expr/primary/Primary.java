package ca.uwaterloo.joos.ast.expr.primary;

import java.util.ArrayList;
import java.util.List;

import ca.uwaterloo.joos.ast.AST.ASTConstructException;
import ca.uwaterloo.joos.ast.expr.Expression;
import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;
import ca.uwaterloo.joos.parser.ParseTreeTraverse;
import ca.uwaterloo.joos.parser.ParseTreeTraverse.Traverser;

public abstract class Primary extends Expression {

	public Primary(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}
	
	private static class PrimaryTraverser implements Traverser {
		public Primary node;
		private ASTNode parent;
		
		public PrimaryTraverser(ASTNode parent) {
			this.parent = parent;
		}

		@Override
		public List<Node> processTreeNode(TreeNode treeNode) throws Exception {
			String kind = treeNode.productionRule.getLefthand();
			if (kind.equals("literal")) {
				node = new LiteralPrimary(treeNode, parent);
			} else if (ExpressionPrimary.getAcceptingKinds().contains(kind)) {
				node = new ExpressionPrimary(treeNode, parent);
			} else if (kind.equals("arraycreate")) {
//				node = new ArrayCreate(treeNode, parent);
			} else if (kind.equals("arrayaccess")) {
//				node = new ArrayAccess(treeNode, parent);
				// TODO part of lefthand
			} else if (kind.equals("fieldaccess")) {
//				node = new FieldAccess(treeNode, parent);
				// TODO part of lefthand
			} else {
				List<Node> offers = new ArrayList<Node>();
				offers.addAll(treeNode.children);
				return offers;
			}
			return null;
		}

		@Override
		public void processLeafNode(LeafNode leafNode) throws Exception {
			if (leafNode.token.getKind().equals("THIS")) {
				node = new ThisPrimary(leafNode, parent);
			} 
		}
	}
	
	public static Primary newPrimary(Node node, ASTNode parent) throws Exception {
		
		PrimaryTraverser traverser = new PrimaryTraverser(parent);
		ParseTreeTraverse traverse = new ParseTreeTraverse(traverser);
		traverse.traverse(node);
		
		if (traverser.node == null) {
			throw new ASTConstructException(node + " is not / does not contain a primary");
		}
		
		return traverser.node;
	}

}
