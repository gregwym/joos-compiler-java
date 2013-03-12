package ca.uwaterloo.joos.ast.expr.name;

import java.util.ArrayList;
import java.util.List;

import ca.uwaterloo.joos.ast.AST.ASTConstructException;
import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.expr.Expression;
import ca.uwaterloo.joos.ast.expr.Lefthand;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;
import ca.uwaterloo.joos.parser.ParseTreeTraverse;
import ca.uwaterloo.joos.parser.ParseTreeTraverse.Traverser;

public abstract class Name extends Expression implements Lefthand {
	
	public String originalDeclaration;

	public Name(Node node, ASTNode parent) throws Exception {
		super(node, parent);
		this.setIdentifier(this.getName());
	}

	public abstract String getName() throws Exception;
	
	public abstract String getSimpleName() throws Exception;

	private static class NameTraverser implements Traverser {
		public Name node;
		private ASTNode parent;

		public NameTraverser(ASTNode parent) {
			this.parent = parent;
		}

		@Override
		public List<Node> processTreeNode(TreeNode treeNode) throws Exception {
			String kind = treeNode.productionRule.getLefthand();
			if (kind.equals("simplename")) {
				node = new SimpleName(treeNode, parent);
			} else if (kind.equals("qualifiedname")) {
				node = new QualifiedName(treeNode, parent);
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

	public static Name newName(Node node, ASTNode parent) throws Exception {

		NameTraverser traverser = new NameTraverser(parent);
		ParseTreeTraverse traverse = new ParseTreeTraverse(traverser);
		traverse.traverse(node);

		if (traverser.node == null) {
			throw new ASTConstructException(node + " is not / does not contain a name");
		}

		return traverser.node;
	}
}
