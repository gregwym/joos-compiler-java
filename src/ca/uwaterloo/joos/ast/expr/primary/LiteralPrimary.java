package ca.uwaterloo.joos.ast.expr.primary;

import ca.uwaterloo.joos.ast.AST.ASTConstructException;
import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.descriptor.SimpleDescriptor;
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
	
	public LiteralType getType() throws ChildTypeUnmatchException {
		return (LiteralType) this.getChildByDescriptor(TYPE);
	}
	
	public String getValue() throws ChildTypeUnmatchException {
		return (String) this.getChildByDescriptor(VALUE);
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
