/**
 *
 */
package ca.uwaterloo.joos.ast.decl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ca.uwaterloo.joos.ast.AST.ASTConstructException;
import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.descriptor.ChildDescriptor;
import ca.uwaterloo.joos.ast.type.ArrayType;
import ca.uwaterloo.joos.ast.type.Modifiers;
import ca.uwaterloo.joos.ast.type.PrimitiveType;
import ca.uwaterloo.joos.ast.type.ReferenceType;
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
		} else if (treeNode.productionRule.getLefthand().equals("primitivetype")) {
			Type type = new PrimitiveType(treeNode, this);
			addChild(TYPE, type);
		} else if (treeNode.productionRule.getLefthand().equals("referencetype")) {
			Type type = null;
			List<String> rhs = Arrays.asList(treeNode.productionRule.getRighthand());
			if(rhs.contains("arraytype")) {
				type = new ArrayType(treeNode.children.get(0), this);
			}
			else if(rhs.contains("name")) {
				type = new ReferenceType(treeNode.children.get(0), this);
			}
			else {
				throw new ASTConstructException("Unknown referencetype" + rhs);
			}
			addChild(TYPE, type);
		} else {
			offers.addAll(treeNode.children);
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
