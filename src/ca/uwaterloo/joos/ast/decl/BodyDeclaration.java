/**
 *
 */
package ca.uwaterloo.joos.ast.decl;

import java.util.Arrays;
import java.util.List;

import ca.uwaterloo.joos.ast.AST.ASTConstructException;
import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.Modifiers;
import ca.uwaterloo.joos.ast.descriptor.ChildDescriptor;
import ca.uwaterloo.joos.ast.name.Name;
import ca.uwaterloo.joos.ast.name.SimpleName;
import ca.uwaterloo.joos.ast.type.ArrayType;
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

	protected static final ChildDescriptor NAME = new ChildDescriptor(Name.class);
	protected static final ChildDescriptor MODIFIERS = new ChildDescriptor(Modifiers.class);
	protected static final ChildDescriptor TYPE = new ChildDescriptor(Type.class);

	public BodyDeclaration(Node node, ASTNode parent) throws Exception {
		super(node, parent);
		Name name = this.getName();
		if (name == null) {
			throw new ASTConstructException("BodyDeclaration always expecting a name");
		}
		this.setIdentifier(name.getName());
	}

	public Name getName() throws ChildTypeUnmatchException {
		return (Name) this.getChildByDescriptor(BodyDeclaration.NAME);
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
	public List<Node> processTreeNode(TreeNode treeNode) throws Exception {
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
			return super.processTreeNode(treeNode);
		}
		return null;
	}


	/* (non-Javadoc)
	 * @see ca.uwaterloo.joos.parser.ParseTreeTraverse.Traverser#processLeafNode(ca.uwaterloo.joos.parser.ParseTree.LeafNode)
	 */
	@Override
	public void processLeafNode(LeafNode leafNode) throws Exception {
		if (leafNode.token.getKind().equals("ID")) {
			Name name = new SimpleName(leafNode, this);
			this.addChild(BodyDeclaration.NAME, name);
		}
	}
}
