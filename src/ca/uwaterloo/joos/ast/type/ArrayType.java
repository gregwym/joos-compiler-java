package ca.uwaterloo.joos.ast.type;

import java.util.List;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.descriptor.ChildDescriptor;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

public class ArrayType extends Type {
	public static final ChildDescriptor TYPE = new ChildDescriptor(Type.class);

	public ArrayType(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}

	public Type getType() throws ChildTypeUnmatchException {
		return (Type) this.getChildByDescriptor(ArrayType.TYPE);
	}

	@Override
	public List<Node> processTreeNode(TreeNode treeNode) throws Exception {
		if (treeNode.productionRule.getLefthand().equals("primitivetype")) {
			Type type = new PrimitiveType(treeNode, this);
			this.addChild(TYPE, type);
		}
		if (treeNode.productionRule.getLefthand().equals("name")) {
			Type type = new ReferenceType(treeNode, this);
			this.addChild(TYPE, type);
		} else {
			return super.processTreeNode(treeNode);
		}
		return null;
	}
}
