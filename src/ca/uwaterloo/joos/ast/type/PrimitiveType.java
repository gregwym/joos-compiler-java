package ca.uwaterloo.joos.ast.type;

import ca.uwaterloo.joos.ast.AST.ASTConstructException;
import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.descriptor.SimpleDescriptor;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;

public class PrimitiveType extends Type {

	public static final SimpleDescriptor TYPE = new SimpleDescriptor(Primitive.class);

	public static enum Primitive {
		BOOLEAN, BYTE, CHAR, INT, SHORT
	}

	public PrimitiveType(Node node, ASTNode parent) throws Exception {
		super(node, parent);
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
	public String getFullyQualifiedName() throws ChildTypeUnmatchException {
		return this.getPrimitiveType().name();
	}

	@Override
	public void processLeafNode(LeafNode leafNode) throws Exception {
		Primitive type = stringToType(leafNode.token.getKind().toUpperCase());
		addChild(TYPE, type);
	}
	
	@Override
	public String getIdentifier() {
		String name = null;
		try {
			name = this.getPrimitiveType().name();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return name;
	}
}
