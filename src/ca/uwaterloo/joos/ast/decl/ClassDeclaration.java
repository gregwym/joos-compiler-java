package ca.uwaterloo.joos.ast.decl;

import java.util.HashSet;
import java.util.Set;

import ca.uwaterloo.joos.ast.AST.ASTConstructException;
import ca.uwaterloo.joos.ast.ParseTreeTraverse;
import ca.uwaterloo.joos.ast.ParseTreeTraverse.Traverser;
import ca.uwaterloo.joos.ast.body.ClassBody;
import ca.uwaterloo.joos.ast.type.Modifiers;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

public class ClassDeclaration extends TypeDeclaration {
	private String superClass;

	public ClassDeclaration(Node node) throws ASTConstructException {
		assert node instanceof TreeNode : "FileUnit is expecting a TreeNode";
			
		ParseTreeTraverse traverse = new ParseTreeTraverse(new Traverser() {
	
			public Set<Node> processTreeNode(TreeNode treeNode) throws ASTConstructException {
				Set<Node> offers = new HashSet<Node>();
				if (treeNode.productionRule.getLefthand().equals("modifiers")) {
					modifiers = new Modifiers(treeNode);
				}
				else if (treeNode.productionRule.getLefthand().equals("classbody")) {
					body = new ClassBody(treeNode);
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

	/**
	 * @return the superClass
	 */
	public String getSuperClass() {
		return superClass;
	}
	
	@Override
	public String toString(int level) {
		String str = super.toString(level);
		str += "extends: " + this.superClass;
		str += " implements: ";
//		for(String id: this.interfaces)
//			str += id + " ";
		str += "\n";
		str += this.modifiers.toString(level + 1);
		str += this.body.toString(level + 1);
		return str;
	}
}
