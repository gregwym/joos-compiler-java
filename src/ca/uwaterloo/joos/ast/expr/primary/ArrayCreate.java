package ca.uwaterloo.joos.ast.expr.primary;

import java.util.List;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.descriptor.ChildDescriptor;
import ca.uwaterloo.joos.ast.expr.Expression;
import ca.uwaterloo.joos.ast.type.PrimitiveType;
import ca.uwaterloo.joos.ast.type.ReferenceType;
import ca.uwaterloo.joos.ast.type.Type;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

public class ArrayCreate extends Primary {
	
	public static final ChildDescriptor TYPE = new ChildDescriptor(Type.class);
	public static final ChildDescriptor DIMENSION = new ChildDescriptor(Expression.class);
	
	public Type getType() throws ChildTypeUnmatchException {
		return (Type) this.getChildByDescriptor(TYPE);
	}
	
	public Expression getDimension() throws ChildTypeUnmatchException {
		return (Expression) this.getChildByDescriptor(DIMENSION);
	}

	public ArrayCreate(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}
	
	@Override
	public List<Node> processTreeNode(TreeNode treeNode) throws Exception {
		String kind = treeNode.getKind();

		if (kind.equals("name")) {
			ReferenceType type = new ReferenceType(treeNode, this);
			this.addChild(TYPE, type);
		} else if (kind.equals("primitivetype")) {
			PrimitiveType type = new PrimitiveType(treeNode, this);
			this.addChild(TYPE, type);
		} else if (kind.equals("expr")) {
			Expression dim = Expression.newExpression(treeNode, this);
			this.addChild(DIMENSION, dim);
		} else {
			return super.processTreeNode(treeNode);
		}
		return null;
	}

}
