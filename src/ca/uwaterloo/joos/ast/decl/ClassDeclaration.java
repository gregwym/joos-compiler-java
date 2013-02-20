package ca.uwaterloo.joos.ast.decl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ca.uwaterloo.joos.ast.AST.ASTConstructException;
import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.body.ClassBody;
import ca.uwaterloo.joos.ast.descriptor.ChildDescriptor;
import ca.uwaterloo.joos.ast.type.ClassType;
import ca.uwaterloo.joos.ast.type.InterfaceType;
import ca.uwaterloo.joos.ast.type.Modifiers;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;
import ca.uwaterloo.joos.parser.ParseTreeTraverse;
import ca.uwaterloo.joos.parser.ParseTreeTraverse.Traverser;

public class ClassDeclaration extends TypeDeclaration {

	protected static final ChildDescriptor SUPER = new ChildDescriptor(ClassType.class);

	public ClassDeclaration(Node node, ASTNode parent) throws ASTConstructException {
		super(parent);

		assert node instanceof TreeNode : "ClassDeclaration is expecting a TreeNode";

		ParseTreeTraverse traverse = new ParseTreeTraverse(new Traverser(this) {

			public Set<Node> processTreeNode(TreeNode treeNode) throws ASTConstructException {
				Set<Node> offers = new HashSet<Node>();
				if (treeNode.productionRule.getLefthand().equals("modifiers")) {
					addChild(MODIFIERS, new Modifiers(treeNode, parent));
				} else if (treeNode.productionRule.getLefthand().equals("interfaces")) {
					List<ASTNode> interfaces = getInterfaces();
					if (interfaces == null) {
						interfaces = new ArrayList<ASTNode>();
						addChild(INTERFACES, interfaces);
					}
					InterfaceType interfaceType = new InterfaceType(parent);
					interfaces.add(interfaceType);
				} else if (treeNode.productionRule.getLefthand().equals("super")) {
					ClassType superType = new ClassType(parent);
					addChild(SUPER, superType);
				} else if (treeNode.productionRule.getLefthand().equals("classbody")) {
					addChild(BODY, new ClassBody(treeNode, parent));
				} else {
					for (Node n : treeNode.children)
						offers.add(n);
				}
				return offers;
			}

			public void processLeafNode(LeafNode leafNode) throws ASTConstructException {
				if (leafNode.token.getKind().equals("ID")) {
					setIdentifier(leafNode.token.getLexeme());
				}
			}

		});

		traverse.traverse(node);
	}

	/**
	 * @return the superClass
	 * @throws ChildTypeUnmatchException 
	 */
	public ClassType getSuperClass() throws ChildTypeUnmatchException {
		return (ClassType) this.getChildByDescriptor(ClassDeclaration.SUPER);
	}
}
