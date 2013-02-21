package ca.uwaterloo.joos.ast.decl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.body.ClassBody;
import ca.uwaterloo.joos.ast.body.TypeBody;
import ca.uwaterloo.joos.ast.descriptor.ChildDescriptor;
import ca.uwaterloo.joos.ast.descriptor.ChildListDescriptor;
import ca.uwaterloo.joos.ast.type.InterfaceType;
import ca.uwaterloo.joos.ast.type.Modifiers;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

public abstract class TypeDeclaration extends ASTNode {

	protected static final ChildDescriptor MODIFIERS = new ChildDescriptor(Modifiers.class);
	protected static final ChildListDescriptor INTERFACES = new ChildListDescriptor(InterfaceType.class);
	protected static final ChildDescriptor BODY = new ChildDescriptor(TypeBody.class);

	public TypeDeclaration(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}

	public Modifiers getModifiers() throws ChildTypeUnmatchException {
		return (Modifiers) this.getChildByDescriptor(TypeDeclaration.MODIFIERS);
	}

	@SuppressWarnings("unchecked")
	public List<InterfaceType> getInterfaces() throws ChildTypeUnmatchException {
		return (List<InterfaceType>) this.getChildByDescriptor(TypeDeclaration.INTERFACES);
	}

	public TypeBody getBody() throws ChildTypeUnmatchException {
		return (TypeBody) this.getChildByDescriptor(TypeDeclaration.BODY);
	}

	@Override
	public Set<Node> processTreeNode(TreeNode treeNode) throws Exception {
		Set<Node> offers = new HashSet<Node>();
		if (treeNode.productionRule.getLefthand().equals("modifiers")) {
			Modifiers modifiers = new Modifiers(treeNode, this);
			addChild(MODIFIERS, modifiers);
		} else if (treeNode.productionRule.getLefthand().equals("interfaces")) {
//			List<InterfaceType> interfaces = getInterfaces();
//			if (interfaces == null) {
//				interfaces = new ArrayList<InterfaceType>();
//				addChild(INTERFACES, interfaces);
//			}
//			InterfaceType interfaceType = new InterfaceType(this);
//			interfaces.add(interfaceType);
		} else if (treeNode.productionRule.getLefthand().equals("classbody")) {
			ClassBody body = new ClassBody(treeNode, this);
			addChild(BODY, body);
		} else {
			for (Node n : treeNode.children)
				offers.add(n);
		}
		return offers;
	}

	@Override
	public void processLeafNode(LeafNode leafNode) throws Exception {
		if (leafNode.token.getKind().equals("ID")) {
			setIdentifier(leafNode.token.getLexeme());
		}
	}
}
