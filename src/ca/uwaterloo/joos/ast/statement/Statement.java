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
		public Statement node;
		private ASTNode parent;

		public StatementTraverser(ASTNode parent) {
			this.parent = parent;
		}

		@Override
		public List<Node> processTreeNode(TreeNode treeNode) throws Exception {
			if (treeNode.getKind().equals("block")) {
				node = new Block(treeNode, parent);
			} else if (treeNode.getKind().equals("for") || 
					treeNode.getKind().equals("fornoshort")) {
				node = new ForStatement(treeNode,parent);
			} else if (treeNode.getKind().equals("ifthen")) {

			} else if (treeNode.getKind().equals("ifthenelse") || 
					treeNode.getKind().equals("ifthenelsenoshort")) {

			} else if (treeNode.getKind().equals("while") || 
					treeNode.getKind().equals("whilenoshort")) {

			} else if (treeNode.getKind().equals("retstmnt")) {
				node = new ReturnStatement(treeNode, parent);
			} else if (treeNode.getKind().equals("emptystmnt")) {
				// Empty statement can be an Expression Statement without expression
				node = new ExpressionStatement(treeNode, parent);
			} else if (treeNode.getKind().equals("exprstmnt")) {
				node = new ExpressionStatement(treeNode, parent);
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

	public static Statement newStatement(Node node, ASTNode parent) throws Exception {

		StatementTraverser traverser = new StatementTraverser(parent);
		ParseTreeTraverse traverse = new ParseTreeTraverse(traverser);
		traverse.traverse(node);

		if (traverser.node == null) {
			throw new ASTConstructException(node + " is not / does not contain a statement");
		}

		return traverser.node;
	}

}
