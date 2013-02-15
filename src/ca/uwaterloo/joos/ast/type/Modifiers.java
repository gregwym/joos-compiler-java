package ca.uwaterloo.joos.ast.type;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ca.uwaterloo.joos.ast.AST.ASTConstructException;
import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.visitor.ASTVisitor;
import ca.uwaterloo.joos.parser.ParseTreeTraverse;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;
import ca.uwaterloo.joos.parser.ParseTreeTraverse.Traverser;

public class Modifiers extends ASTNode {

	public static enum Modifier {
		ABSTRACT, FINAL, NATIVE, PUBLIC, PROTECTED, STATIC
	}

	private List<Modifier> modifiers;

	/**
	 * @return the modifiers
	 */
	public List<Modifier> getModifiers() {
		return modifiers;
	}

	public Modifiers(Node modifiersNode, ASTNode parent) throws ASTConstructException {
		super(parent);
		
		assert modifiersNode instanceof TreeNode : "Modifiers is expecting a TreeNode";
		
		this.modifiers = new ArrayList<Modifier>();
		
		ParseTreeTraverse traverse = new ParseTreeTraverse(new Traverser(this) {

			public Set<Node> processTreeNode(TreeNode treeNode) {
				Set<Node> offers = new HashSet<Node>();
				for (Node n : treeNode.children) 
					offers.add(n);
				return offers;
			}

			public void processLeafNode(LeafNode leafNode) throws ASTConstructException {
				Modifier modifier = stringToModifier(leafNode.token.getKind().toUpperCase());
				logger.fine("Modifier added: " + modifier.name());
				modifiers.add(modifier);
			}
		    
		});

		traverse.traverse(modifiersNode);
	}
	
	private Modifier stringToModifier(String name) throws ASTConstructException {
		for(Modifier modifier: Modifier.values()) {
			if(modifier.name().equals(name)) return modifier;
		}
		throw new ASTConstructException("Unknown modifier " + name);
	}

	@Override
	public String toString(int level) {
		String str = super.toString(level);
		for (Modifier modifier : this.modifiers)
			str += modifier.name() + " ";
		str += "\n";
		return str;
	}
	
	/* (non-Javadoc)
	 * @see ca.uwaterloo.joos.ast.ASTNode#accept(ca.uwaterloo.joos.ast.ASTVisitor)
	 */
	@Override
	public void accept(ASTVisitor visitor) {
		visitor.willVisit(this);
		if(visitor.visit(this)) {
			
		}
		visitor.didVisit(this);
	}
}
