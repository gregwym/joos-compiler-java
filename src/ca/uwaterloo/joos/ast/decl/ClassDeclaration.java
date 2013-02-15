package ca.uwaterloo.joos.ast.decl;

import java.util.HashSet;
import java.util.Set;

import ca.uwaterloo.joos.ast.AST.ASTConstructException;
import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.body.ClassBody;
import ca.uwaterloo.joos.ast.type.Modifiers;
import ca.uwaterloo.joos.ast.visitor.ASTVisitor;
import ca.uwaterloo.joos.parser.ParseTreeTraverse;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;
import ca.uwaterloo.joos.parser.ParseTreeTraverse.Traverser;

public class ClassDeclaration extends TypeDeclaration {
	private String superClass;

	public ClassDeclaration(Node node, ASTNode parent) throws ASTConstructException {	
		super(parent);
		
		assert node instanceof TreeNode : "ClassDeclaration is expecting a TreeNode";
			
		ParseTreeTraverse traverse = new ParseTreeTraverse(new Traverser(this) {
	
			public Set<Node> processTreeNode(TreeNode treeNode) throws ASTConstructException {
				Set<Node> offers = new HashSet<Node>();
				if (treeNode.productionRule.getLefthand().equals("modifiers")) {
					modifiers = new Modifiers(treeNode, parent);
				}
				else if (treeNode.productionRule.getLefthand().equals("classbody")) {
					body = new ClassBody(treeNode, parent);
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
		for(String id: this.interfaces)
			str += id + " ";
		str += "\n";
		str += this.modifiers.toString(level + 1);
		str += this.body.toString(level + 1);
		return str;
	}
	
	/* (non-Javadoc)
	 * @see ca.uwaterloo.joos.ast.ASTNode#accept(ca.uwaterloo.joos.ast.ASTVisitor)
	 */
	@Override
	public void accept(ASTVisitor visitor) {
		visitor.willVisit(this);
		if(visitor.visit(this)) {
			super.accept(visitor);
//			this.superClass.accept(visitor);
		}
		visitor.didVisit(this);
	}
}
