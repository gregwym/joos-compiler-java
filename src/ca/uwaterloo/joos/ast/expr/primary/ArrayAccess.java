package ca.uwaterloo.joos.ast.expr.primary;

import java.util.List;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.descriptor.ChildDescriptor;
import ca.uwaterloo.joos.ast.expr.Expression;
import ca.uwaterloo.joos.ast.expr.Lefthand;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

public class ArrayAccess extends Primary implements Lefthand {
	
	public static final ChildDescriptor EXPRESSION = new ChildDescriptor(Expression.class);
	public static final ChildDescriptor INDEX = new ChildDescriptor(Expression.class);
	
	public Expression getExpression() throws ChildTypeUnmatchException {
		return (Expression) this.getChildByDescriptor(EXPRESSION);
	}
	
	public Expression getIndex() throws ChildTypeUnmatchException {
		return (Expression) this.getChildByDescriptor(INDEX);
	}

	public ArrayAccess(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}
	
	@Override
	public List<Node> processTreeNode(TreeNode treeNode) throws Exception {
		String kind = treeNode.getKind();

		if (kind.equals("name") || kind.equals("primnoarray")) {
			Expression expr = Expression.newExpression(treeNode, this);
			this.addChild(EXPRESSION, expr);
		} else if (kind.equals("expr")) {
			Expression index = Expression.newExpression(treeNode, this);
			this.addChild(INDEX, index);
		} else {
			return super.processTreeNode(treeNode);
		}
		return null;
	}

}
