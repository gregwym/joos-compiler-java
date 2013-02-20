package ca.uwaterloo.joos.ast.decl;

import java.util.HashSet;
import java.util.Set;

import ca.uwaterloo.joos.ast.AST.ASTConstructException;
import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.body.InterfaceBody;
import ca.uwaterloo.joos.ast.descriptor.ChildDescriptor;
import ca.uwaterloo.joos.ast.descriptor.ChildListDescriptor;
import ca.uwaterloo.joos.ast.type.InterfaceType;
import ca.uwaterloo.joos.ast.type.Modifiers;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;
import ca.uwaterloo.joos.parser.ParseTreeTraverse;
import ca.uwaterloo.joos.parser.ParseTreeTraverse.Traverser;

public class InterfaceDeclaration extends TypeDeclaration {
	protected static ChildDescriptor MODIFIERS = new ChildDescriptor(Modifiers.class);
	protected static ChildListDescriptor EXTENDINTERFACES = new ChildListDescriptor(InterfaceType.class);
	protected static ChildDescriptor INTERFACEBODY = new ChildDescriptor(InterfaceBody.class);
	public InterfaceDeclaration(TreeNode node, ASTNode parent) throws ASTConstructException {
		super(parent);
		assert node instanceof TreeNode : "InterfaceDeclaration is expecting a TreeNode";
			
		ParseTreeTraverse traverse = new ParseTreeTraverse(new Traverser(this) {
	
			public Set<Node> processTreeNode(TreeNode treeNode) throws ASTConstructException {
				Set<Node> offers = new HashSet<Node>();
				if (treeNode.productionRule.getLefthand().equals("modifiers")) {
					addChild(MODIFIERS , new Modifiers(treeNode, parent));
				}
				else if (treeNode.productionRule.getLefthand().equals("extendsinterfaces")) {
//					interface = 
				}
				else if (treeNode.productionRule.getLefthand().equals("interfacebody")) {
					addChild(INTERFACEBODY, new InterfaceBody(treeNode, parent));
				}
				else {
					for (Node n : treeNode.children) 
						offers.add(n);
				}
				return offers;
			}
	
			public void processLeafNode(LeafNode leafNode) throws ASTConstructException {
				if(leafNode.token.getKind().equals("ID")) {
					setIdentifier(leafNode.token.getLexeme());
				}
			}
		    
		});
	
		traverse.traverse(node);
	}
}

