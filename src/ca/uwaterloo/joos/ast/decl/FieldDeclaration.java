package ca.uwaterloo.joos.ast.decl;

import java.util.Set;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

public class FieldDeclaration extends BodyDeclaration {

	// protected static final ChildDescriptor INITIAL = new ChildDescriptor(Expression.class);

	public FieldDeclaration(Node node, ASTNode parent) throws Exception {
		super(node, parent);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ca.uwaterloo.joos.parser.ParseTreeTraverse.Traverser#processTreeNode(
	 * ca.uwaterloo.joos.parser.ParseTree.TreeNode)
	 */
	@Override
	public Set<Node> processTreeNode(TreeNode treeNode) throws Exception {
		if (treeNode.productionRule.getLefthand().equals("expr")) {
//			Expression initial = new Expression(treeNode, this);
		} else {
			return super.processTreeNode(treeNode);
		}
		return null;
	}
}
