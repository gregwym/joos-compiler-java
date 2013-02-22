package ca.uwaterloo.joos.ast.statement;

import java.util.List;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.descriptor.ChildDescriptor;
import ca.uwaterloo.joos.ast.expr.Expression;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

public class ExpressionStatement extends Statement {
	
	public static final ChildDescriptor EXPRSSION = new ChildDescriptor(Expression.class);
	
	public Expression getExpression() throws ChildTypeUnmatchException {
		return (Expression) this.getChildByDescriptor(EXPRSSION);
	}

	public ExpressionStatement(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}
	
	@Override
	public List<Node> processTreeNode(TreeNode treeNode) throws Exception {
		// TODO enable `expr` for return statement after all statements are implemented
		if (treeNode.getKind().equals("stmntexpr") /*|| treeNode.getKind().equals("expr")*/) {
			Expression expr = Expression.newExpression(treeNode, this);
			this.addChild(EXPRSSION, expr);
		} else {
			return super.processTreeNode(treeNode);
		}
		return null;
	}

}
