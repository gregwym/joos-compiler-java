/**
 * 
 */
package ca.uwaterloo.joos.ast.decl;

import java.util.HashSet;
import java.util.Set;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.descriptor.ChildDescriptor;
import ca.uwaterloo.joos.ast.type.Modifiers;
import ca.uwaterloo.joos.ast.type.Type;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

/**
 * @author Greg Wang
 * 
 */
public abstract class BodyDeclaration extends ASTNode {

	protected static final ChildDescriptor MODIFIERS = new ChildDescriptor(Modifiers.class);
	protected static final ChildDescriptor TYPE = new ChildDescriptor(Type.class);

	public BodyDeclaration(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}

	public Modifiers getModifiers() throws ChildTypeUnmatchException {
		return (Modifiers) this.getChildByDescriptor(BodyDeclaration.MODIFIERS);
	}

	public Type getType() throws ChildTypeUnmatchException {
		return (Type) this.getChildByDescriptor(BodyDeclaration.TYPE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ca.uwaterloo.joos.parser.ParseTreeTraverse.Traverser#processTreeNode(
	 * ca.uwaterloo.joos.parser.ParseTree.TreeNode)
	 */
	@Override
	public Set<Node> processTreeNode(TreeNode treeNode) throws Exception {
		Set<Node> offers = new HashSet<Node>();
		if (treeNode.productionRule.getLefthand().equals("modifiers")) {
			Modifiers modifiers = new Modifiers(treeNode, this);
			addChild(MODIFIERS, modifiers);
		} else if (treeNode.productionRule.getLefthand().equals("type")) {
//			Type type = new Type(treeNode, this);
//			addChild(TYPE, type);
		} else {
			for (Node n : treeNode.children)
				offers.add(n);
		}
		return offers;
	}
	

	/* (non-Javadoc)
	 * @see ca.uwaterloo.joos.parser.ParseTreeTraverse.Traverser#processLeafNode(ca.uwaterloo.joos.parser.ParseTree.LeafNode)
	 */
	@Override
	public void processLeafNode(LeafNode leafNode) throws Exception {
		if (leafNode.token.getKind().equals("ID")) {
			setIdentifier(leafNode.token.getLexeme());
		}
	}
}
