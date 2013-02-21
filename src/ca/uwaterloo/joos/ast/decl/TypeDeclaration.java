package ca.uwaterloo.joos.ast.decl;

import java.util.ArrayList;
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
import ca.uwaterloo.joos.parser.ParseTreeTraverse;

public abstract class TypeDeclaration extends ASTNode {

	protected static final ChildDescriptor MODIFIERS = new ChildDescriptor(Modifiers.class);
	protected static final ChildListDescriptor INTERFACES = new ChildListDescriptor(InterfaceType.class);
	protected static final ChildDescriptor BODY = new ChildDescriptor(TypeBody.class);

	public TypeDeclaration(Node node, ASTNode parent) throws Exception {
		super(parent);
		assert node instanceof TreeNode : "TypeDeclaration is expecting a TreeNode";

		ParseTreeTraverse traverse = new ParseTreeTraverse(this);

		traverse.traverse(node);
	}

	public Modifiers getModifiers() throws ChildTypeUnmatchException {
		return (Modifiers) this.getChildByDescriptor(TypeDeclaration.MODIFIERS);
	}

	public List<ASTNode> getInterfaces() throws ChildTypeUnmatchException {
		return this.getChildByDescriptor(TypeDeclaration.INTERFACES);
	}

	public TypeBody getBody() throws ChildTypeUnmatchException {
		return (TypeBody) this.getChildByDescriptor(TypeDeclaration.BODY);
	}

	@Override
	public Set<Node> processTreeNode(TreeNode treeNode) throws Exception {
		Set<Node> offers = new HashSet<Node>();
		if (treeNode.productionRule.getLefthand().equals("modifiers")) {
			addChild(MODIFIERS, new Modifiers(treeNode, this));
		} else if (treeNode.productionRule.getLefthand().equals("interfaces")) {
			List<ASTNode> interfaces = getInterfaces();
			if (interfaces == null) {
				interfaces = new ArrayList<ASTNode>();
				addChild(INTERFACES, interfaces);
			}
			InterfaceType interfaceType = new InterfaceType(this);
			interfaces.add(interfaceType);
		} else if (treeNode.productionRule.getLefthand().equals("classbody")) {
//			addChild(BODY, new ClassBody(treeNode, this));
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
