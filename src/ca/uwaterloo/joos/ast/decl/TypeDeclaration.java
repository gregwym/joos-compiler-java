package ca.uwaterloo.joos.ast.decl;

import java.util.ArrayList;
import java.util.List;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.Modifiers;
import ca.uwaterloo.joos.ast.body.ClassBody;
import ca.uwaterloo.joos.ast.body.TypeBody;
import ca.uwaterloo.joos.ast.descriptor.ChildDescriptor;
import ca.uwaterloo.joos.ast.descriptor.ChildListDescriptor;
import ca.uwaterloo.joos.ast.type.ReferenceType;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

public abstract class TypeDeclaration extends ASTNode {

	protected static final ChildDescriptor MODIFIERS = new ChildDescriptor(Modifiers.class);
	protected static final ChildListDescriptor INTERFACES = new ChildListDescriptor(ReferenceType.class);
	protected static final ChildDescriptor BODY = new ChildDescriptor(TypeBody.class);

	public TypeDeclaration(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}

	public Modifiers getModifiers() throws ChildTypeUnmatchException {
		return (Modifiers) this.getChildByDescriptor(TypeDeclaration.MODIFIERS);
	}

	@SuppressWarnings("unchecked")
	public List<ReferenceType> getInterfaces() throws ChildTypeUnmatchException {
		return (List<ReferenceType>) this.getChildByDescriptor(TypeDeclaration.INTERFACES);
	}

	public TypeBody getBody() throws ChildTypeUnmatchException {
		return (TypeBody) this.getChildByDescriptor(TypeDeclaration.BODY);
	}

	@Override
	public List<Node> processTreeNode(TreeNode treeNode) throws Exception {
		List<Node> offers = new ArrayList<Node>();
		if (treeNode.productionRule.getLefthand().equals("modifiers")) {
			Modifiers modifiers = new Modifiers(treeNode, this);
			addChild(MODIFIERS, modifiers);
		} else if (treeNode.productionRule.getLefthand().equals("interfaces")) {
			// TODO Add interfaces types
		} else if (treeNode.productionRule.getLefthand().equals("classbody")) {
			ClassBody body = new ClassBody(treeNode, this);
			addChild(BODY, body);
		} else {
			offers.addAll(treeNode.children);
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
