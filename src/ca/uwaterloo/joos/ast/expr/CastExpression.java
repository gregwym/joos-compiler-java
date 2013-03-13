package ca.uwaterloo.joos.ast.expr;

import java.util.ArrayList;
import java.util.List;

import ca.uwaterloo.joos.ast.AST.ASTConstructException;
import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.descriptor.ChildDescriptor;
import ca.uwaterloo.joos.ast.expr.name.Name;
import ca.uwaterloo.joos.ast.type.ArrayType;
import ca.uwaterloo.joos.ast.type.PrimitiveType;
import ca.uwaterloo.joos.ast.type.ReferenceType;
import ca.uwaterloo.joos.ast.type.Type;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

public class CastExpression extends Expression {

	public static final ChildDescriptor TYPE = new ChildDescriptor(Type.class);
	public static final ChildDescriptor EXPR = new ChildDescriptor(Expression.class);

	public CastExpression(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}

	public Type getType() throws ChildTypeUnmatchException {
		return (Type) this.getChildByDescriptor(TYPE);
	}

	public Expression getExpression() throws ChildTypeUnmatchException {
		return (Expression) this.getChildByDescriptor(EXPR);
	}

	@Override
	public List<Node> processTreeNode(TreeNode treeNode) throws Exception {
		String kind = treeNode.getKind();

		if (kind.equals("expr")) {
			Expression expr = Expression.newExpression(treeNode, this);
			if (expr instanceof Name) {
				ReferenceType type = new ReferenceType(treeNode, this);
				this.addChild(TYPE, type);
			} else {
				throw new ASTConstructException("Can not cast to Expression other than a Name, but got " + expr);
			}
		} else if (kind.equals("name")) {
			ArrayType type = new ArrayType(treeNode, this);
			this.addChild(TYPE, type);
		} else if (kind.equals("primitivetype")) {
			PrimitiveType type = new PrimitiveType(treeNode, this);
			this.addChild(TYPE, type);
		} else if (kind.equals("castexpr") && (treeNode.children.get(2) instanceof TreeNode)) {
			TreeNode dimsNode = (TreeNode) treeNode.children.get(2);
			if(dimsNode.getKind().equals("dims") && dimsNode.children.size() > 0) {
				ArrayType type = new ArrayType(treeNode.children.get(1), this);
				this.addChild(TYPE, type);
				
				List<Node> offers = new ArrayList<Node>();
				offers.add(treeNode.children.get(4));
				return offers;
			} else {
				return super.processTreeNode(treeNode);
			}
		} else if (kind.startsWith("unaryexpr")) {
			Expression expr = Expression.newExpression(treeNode, this);
			this.addChild(EXPR, expr);
		} else {
			return super.processTreeNode(treeNode);
		}
		return null;
	}
}
