package ca.uwaterloo.joos.ast.expr;

import java.util.List;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.descriptor.ChildDescriptor;
import ca.uwaterloo.joos.ast.descriptor.ChildListDescriptor;
import ca.uwaterloo.joos.ast.type.ReferenceType;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

public class ClassCreateExpression extends Expression {
	
	public static final ChildDescriptor TYPE = new ChildDescriptor(ReferenceType.class);
	public static final ChildListDescriptor ARGUMENTS = new ChildListDescriptor(Expression.class);

	public ClassCreateExpression(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}
	
	public ReferenceType getType() throws ChildTypeUnmatchException {
		return (ReferenceType) this.getChildByDescriptor(TYPE);
	}
	
	@SuppressWarnings("unchecked")
	public List<Expression> getArguments() throws ChildTypeUnmatchException {
		return (List<Expression>) this.getChildByDescriptor(ARGUMENTS);
	}
	
	@Override
	public List<Node> processTreeNode(TreeNode treeNode) throws Exception {
		String kind = treeNode.getKind();
		if (kind.equals("name")) {
			ReferenceType type = new ReferenceType(treeNode, this);
			this.addChild(TYPE, type);
		} else if (kind.equals("expr")) {
			Expression arg = Expression.newExpression(treeNode, this);
			this.addChild(ARGUMENTS, arg);
		} else {
			return super.processTreeNode(treeNode);
		}
		return null;
	}
}
