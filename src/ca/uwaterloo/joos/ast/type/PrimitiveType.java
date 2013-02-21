package ca.uwaterloo.joos.ast.type;

import java.util.HashSet;
import java.util.Set;

import ca.uwaterloo.joos.ast.AST.ASTConstructException;
import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.descriptor.SimpleDescriptor;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

public class PrimitiveType extends Type {

	public static final SimpleDescriptor TYPE = new SimpleDescriptor(Primitive.class);

	public static enum Primitive {
		BOOLEAN, BYTE, CHAR, INT, SHORT
	}

	public PrimitiveType(Node primitiveNod, ASTNode parent) throws Exception {
		super(primitiveNod, parent);
	}

	private Primitive stringToType(String name) throws ASTConstructException {
		for (Primitive type : Primitive.values()) {
			if (type.name().equals(name))
				return type;
		}
		throw new ASTConstructException("Unknown primitive type " + name);
	}

	public Primitive getPrimitiveType() throws ChildTypeUnmatchException {
		return (Primitive) this.getChildByDescriptor(PrimitiveType.TYPE);
	}

	@Override
	public Set<Node> processTreeNode(TreeNode treeNode) throws Exception {
		Set<Node> offers = new HashSet<Node>();
		for (Node n : treeNode.children)
			offers.add(n);
		return offers;
	}

	@Override
	public void processLeafNode(LeafNode leafNode) throws Exception {
		Primitive type = stringToType(leafNode.token.getKind().toUpperCase());
		addChild(TYPE, type);
	}
}
