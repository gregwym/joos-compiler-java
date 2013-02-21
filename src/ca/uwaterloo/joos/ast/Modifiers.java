package ca.uwaterloo.joos.ast;

import java.util.ArrayList;
import java.util.List;

import ca.uwaterloo.joos.ast.AST.ASTConstructException;
import ca.uwaterloo.joos.ast.descriptor.SimpleListDescriptor;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

public class Modifiers extends ASTNode {

	public static final SimpleListDescriptor MODIFIERS = new SimpleListDescriptor(Modifier.class);

	public static enum Modifier {
		ABSTRACT, FINAL, NATIVE, PUBLIC, PROTECTED, STATIC
	}

	public Modifiers(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}

	private Modifier stringToModifier(String name) throws ASTConstructException {
		for(Modifier modifier: Modifier.values()) {
			if(modifier.name().equals(name)) return modifier;
		}
		throw new ASTConstructException("Unknown modifier " + name);
	}

	/**
	 * @return the modifiers
	 * @throws ChildTypeUnmatchException
	 */
	@SuppressWarnings("unchecked")
	public List<Modifier> getModifiers() throws ChildTypeUnmatchException {
		return (List<Modifier>) this.getChildByDescriptor(MODIFIERS);
	}

	@Override
	public List<Node> processTreeNode(TreeNode treeNode) throws Exception {
		List<Node> offers = new ArrayList<Node>();
		offers.addAll(treeNode.children);
		return offers;
	}

	@Override
	public void processLeafNode(LeafNode leafNode) throws Exception {
		List<Modifier> modifiers = getModifiers();
		if (modifiers == null) {
			modifiers = new ArrayList<Modifier>();
			addChild(MODIFIERS, modifiers);
		}

		Modifier modifier = stringToModifier(leafNode.token.getKind().toUpperCase());
		modifiers.add(modifier);
	}
}
