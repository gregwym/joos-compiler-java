package ca.uwaterloo.joos.ast.decl;

import java.util.HashSet;
import java.util.Set;

import ca.uwaterloo.joos.ast.AST.ASTConstructException;
import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.ChildDescriptor;
import ca.uwaterloo.joos.ast.ChildListDescriptor;
import ca.uwaterloo.joos.ast.body.InterfaceBody;
import ca.uwaterloo.joos.ast.type.InterfaceType;
import ca.uwaterloo.joos.ast.type.Modifiers;
import ca.uwaterloo.joos.ast.visitor.ASTVisitor;
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
					childrenList.put(MODIFIERS , new Modifiers(treeNode, parent));
				}
				else if (treeNode.productionRule.getLefthand().equals("extendsinterfaces")) {
//					interface = 
				}
				else if (treeNode.productionRule.getLefthand().equals("interfacebody")) {
					childrenList.put(INTERFACEBODY, new InterfaceBody(treeNode, parent));
				}
				else {
					for (Node n : treeNode.children) 
						offers.add(n);
				}
				return offers;
			}
	
			public void processLeafNode(LeafNode leafNode) throws ASTConstructException {
				if(leafNode.token.getKind().equals("ID")) {
					identifier = leafNode.token.getLexeme();
				}
			}
		    
		});
	
		traverse.traverse(node);
	}
	
	/*@Override
	public String toString(int level) {
		String str = super.toString(level);
		str += "extends: ";
//		for(String id: this.interfaces)
//			str += id + " ";
		str += "\n";
		str += this.modifiers.toString(level + 1);
//		str += this.body.toString(level + 1);
		return str;
	}*/
	
	/* (non-Javadoc)
	 * @see ca.uwaterloo.joos.ast.ASTNode#accept(ca.uwaterloo.joos.ast.ASTVisitor)
	 */
	@Override
	public void accept(ASTVisitor visitor) throws Exception{
		visitor.willVisit(this);
		if(visitor.visit(this)) {
			//super.accept(visitor);
		}
		visitor.didVisit(this);
	}
}

