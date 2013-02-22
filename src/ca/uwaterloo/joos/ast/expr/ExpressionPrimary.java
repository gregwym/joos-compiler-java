package ca.uwaterloo.joos.ast.expr;

import java.util.List;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.descriptor.ChildDescriptor;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

public class ParenPrimary extends Primary {
	
	public static final ChildDescriptor EXPRSSION = new ChildDescriptor(Expression.class);
	
	public Expression getExpression() throws ChildTypeUnmatchException {
		return (Expression) this.getChildByDescriptor(EXPRSSION);
	}

	public ParenPrimary(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}

	@Override
	public List<Node> processTreeNode(TreeNode treeNode) throws Exception {
		if (treeNode.getKind().equals("expr")) {
			Expression expr = Expression.newExpression(treeNode, this);
			this.addChild(EXPRSSION, expr);
		} else {
			return super.processTreeNode(treeNode);
		}
		return null;
	}
}
