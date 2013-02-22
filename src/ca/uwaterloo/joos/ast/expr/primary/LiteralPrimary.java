package ca.uwaterloo.joos.ast.expr.primary;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.descriptor.SimpleDescriptor;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;

public class LiteralPrimary extends Primary {

	public static final SimpleDescriptor TYPE = new SimpleDescriptor(LiteralType.class);
	public static final SimpleDescriptor VALUE = new SimpleDescriptor(String.class);

	public static enum LiteralType {
		BOOL, CHAR, INT, NULL, STRING
	}

	public LiteralPrimary(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}

	@Override
	public void processLeafNode(LeafNode leafNode) throws Exception {
		if (leafNode.token.getKind().equals("BOOLLIT")) {
			this.addChild(TYPE, LiteralType.BOOL);
		} else if (leafNode.token.getKind().equals("CHARLIT")) {
			this.addChild(TYPE, LiteralType.CHAR);
		} else if (leafNode.token.getKind().equals("INTLIT")) {
			this.addChild(TYPE, LiteralType.INT);
		} else if (leafNode.token.getKind().equals("NULL")) {
			this.addChild(TYPE, LiteralType.NULL);
		} else if (leafNode.token.getKind().equals("STRINGLIT")) {
			this.addChild(TYPE, LiteralType.STRING);
		}
		this.addChild(VALUE, leafNode.token.getLexeme());
	}

}
