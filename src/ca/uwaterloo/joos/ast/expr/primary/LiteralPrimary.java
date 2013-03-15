package ca.uwaterloo.joos.ast.expr.primary;

import ca.uwaterloo.joos.ast.AST.ASTConstructException;
import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.descriptor.SimpleDescriptor;
import ca.uwaterloo.joos.ast.type.PrimitiveType;
import ca.uwaterloo.joos.ast.type.PrimitiveType.Primitive;
import ca.uwaterloo.joos.ast.type.ReferenceType;
import ca.uwaterloo.joos.ast.type.Type;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;

public class LiteralPrimary extends Primary {

	public static final SimpleDescriptor TYPE = new SimpleDescriptor(LiteralType.class);
	public static final SimpleDescriptor VALUE = new SimpleDescriptor(String.class);

	public static enum LiteralType {
		BOOLLIT, CHARLIT, INTLIT, NULL, STRINGLIT
	}

	public LiteralPrimary(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}
	
	public LiteralType getLiteralType() throws ChildTypeUnmatchException {
		return (LiteralType) this.getChildByDescriptor(TYPE);
	}
	
	public String getValue() throws ChildTypeUnmatchException {
		return (String) this.getChildByDescriptor(VALUE);
	}
	
	public Type getType() throws Exception {
		Primitive primitive = null;
		switch(this.getLiteralType()) {
		case BOOLLIT:
			primitive = Primitive.BOOLEAN;
			break;
		case CHARLIT:
			primitive = Primitive.CHAR;
			break;
		case INTLIT:
			primitive = Primitive.INT;
			break;
		case NULL:
			return new ReferenceType("__NULL__", this);
		case STRINGLIT:
			return new ReferenceType("java.lang.String", this);
		default:
			break;
		}
		return new PrimitiveType(primitive, this);
	}
	
	private LiteralType stringToLiteralType(String name) throws ASTConstructException {
		for(LiteralType type: LiteralType.values()) {
			if(type.name().equals(name)) return type;
		}
		return null;
	}

	@Override
	public void processLeafNode(LeafNode leafNode) throws Exception {
		LiteralType type = stringToLiteralType(leafNode.token.getKind().toUpperCase());
		if(type != null) {
			this.addChild(TYPE, type);
			this.addChild(VALUE, leafNode.token.getLexeme());
		}
		else {
			throw new ASTConstructException("Literal Primary is expecting one of LiteralType, but got " + leafNode.token.getKind().toUpperCase());
		}
	}

}
