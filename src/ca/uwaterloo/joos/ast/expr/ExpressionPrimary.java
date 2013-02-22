package ca.uwaterloo.joos.ast.expr;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.descriptor.ChildDescriptor;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

public class ExpressionPrimary extends Primary {
	
	public static final ChildDescriptor EXPRSSION = new ChildDescriptor(Expression.class);
	
	public Expression getExpression() throws ChildTypeUnmatchException {
		return (Expression) this.getChildByDescriptor(EXPRSSION);
	}

	public ExpressionPrimary(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}
	
	public static Set<String> getAcceptingKinds() {
		Set<String> acceptingKinds = new HashSet<String>();

		acceptingKinds.add("expr");
		acceptingKinds.add("classcreateexpr");
		acceptingKinds.add("methodinvoke");
		
		return acceptingKinds;
	}

	@Override
	public List<Node> processTreeNode(TreeNode treeNode) throws Exception {
		if (ExpressionPrimary.getAcceptingKinds().contains(treeNode.getKind())) {
			Expression expr = Expression.newExpression(treeNode, this);
			this.addChild(EXPRSSION, expr);
		} else {
			return super.processTreeNode(treeNode);
		}
		return null;
	}
}
