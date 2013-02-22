package ca.uwaterloo.joos.ast.decl;

import java.util.List;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.descriptor.ChildDescriptor;
import ca.uwaterloo.joos.ast.expr.Expression;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

public abstract class VariableDeclaration extends BodyDeclaration {

	protected static final ChildDescriptor INITIAL = new ChildDescriptor(Expression.class);

	public VariableDeclaration(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ca.uwaterloo.joos.parser.ParseTreeTraverse.Traverser#processTreeNode(
	 * ca.uwaterloo.joos.parser.ParseTree.TreeNode)
	 */
	@Override
	public List<Node> processTreeNode(TreeNode treeNode) throws Exception {
		if (treeNode.productionRule.getLefthand().equals("expr")) {
			// TODO enable initial expression after implemented all expressions
//			Expression initial = Expression.newExpression(treeNode, this);
//			this.addChild(INITIAL, initial);
		} else {
			return super.processTreeNode(treeNode);
		}
		return null;
	}
}
