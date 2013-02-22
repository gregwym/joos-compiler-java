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

public abstract class Primary extends ASTNode {

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
			if (treeNode.productionRule.getLefthand().equals("literal")) {
				node = new LiteralPrimary(treeNode, parent);
			} else if (treeNode.productionRule.getLefthand().equals("expr")) {
				node = new ParenPrimary(treeNode, parent);
			} else if (treeNode.productionRule.getLefthand().equals("arraycreate")) {
//				node = new ArrayCreate(treeNode, parent);
			} else if (treeNode.productionRule.getLefthand().equals("arrayaccess")) {
//				node = new ArrayAccess(treeNode, parent);
				// TODO part of lefthand
			} else if (treeNode.productionRule.getLefthand().equals("classcreateexpr")) {
//				node = new ClassCreateExpr(treeNode, parent);
				// TODO part of statement expr
			} else if (treeNode.productionRule.getLefthand().equals("fieldaccess")) {
//				node = new FieldAccess(treeNode, parent);
				// TODO part of lefthand
			} else if (treeNode.productionRule.getLefthand().equals("methodinvoke")) {
//				node = new MethodInvoke(treeNode, parent);
				// TODO part of statement expr
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
