package ca.uwaterloo.joos.ast.statement;

import java.util.ArrayList;
import java.util.List;

import ca.uwaterloo.joos.ast.AST.ASTConstructException;
import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;
import ca.uwaterloo.joos.parser.ParseTreeTraverse;
import ca.uwaterloo.joos.parser.ParseTreeTraverse.Traverser;

public abstract class Statement extends ASTNode {

	public Statement(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}

	private static class StatementTraverser implements Traverser {
		public ASTNode node;
		private ASTNode parent;

		public StatementTraverser(ASTNode parent) {
			this.parent = parent;
		}

		@Override
		public List<Node> processTreeNode(TreeNode treeNode) throws Exception {
			if (treeNode.productionRule.getLefthand().equals("block")) {
				node = new Block(treeNode, parent);
			} else if (treeNode.productionRule.getLefthand().equals("for") || 
					treeNode.productionRule.getLefthand().equals("fornoshort")) {

			} else if (treeNode.productionRule.getLefthand().equals("ifthen")) {

			} else if (treeNode.productionRule.getLefthand().equals("ifthenelse") || 
					treeNode.productionRule.getLefthand().equals("ifthenelsenoshort")) {

			} else if (treeNode.productionRule.getLefthand().equals("while") || 
					treeNode.productionRule.getLefthand().equals("whilenoshort")) {

			} else if (treeNode.productionRule.getLefthand().equals("retstmnt")) {

			} else if (treeNode.productionRule.getLefthand().equals("emptystmnt")) {

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

	public static ASTNode newStatement(Node node, ASTNode parent) throws Exception {

		StatementTraverser traverser = new StatementTraverser(parent);
		ParseTreeTraverse traverse = new ParseTreeTraverse(traverser);
		traverse.traverse(node);

		if (traverser.node == null) {
			throw new ASTConstructException(node + " is not / does not contain a statement");
		}

		return traverser.node;
	}

}
