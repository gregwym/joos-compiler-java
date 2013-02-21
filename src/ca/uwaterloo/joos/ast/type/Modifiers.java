package ca.uwaterloo.joos.ast.type;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ca.uwaterloo.joos.ast.AST.ASTConstructException;
import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.descriptor.SimpleListDescriptor;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;
import ca.uwaterloo.joos.parser.ParseTreeTraverse;
import ca.uwaterloo.joos.parser.ParseTreeTraverse.Traverser;

public class Modifiers extends ASTNode {
	
	public static final SimpleListDescriptor MODIFIERS = new SimpleListDescriptor(Modifier.class);

	public static enum Modifier {
		ABSTRACT, FINAL, NATIVE, PUBLIC, PROTECTED, STATIC
	}

	public Modifiers(Node modifiersNode, ASTNode parent) throws ASTConstructException {
		super(parent);
		
		assert modifiersNode instanceof TreeNode : "Modifiers is expecting a TreeNode";
				
		ParseTreeTraverse traverse = new ParseTreeTraverse(new Traverser(this) {

			public Set<Node> processTreeNode(TreeNode treeNode) {
				Set<Node> offers = new HashSet<Node>();
				for (Node n : treeNode.children) 
					offers.add(n);
				return offers;
			}

			public void processLeafNode(LeafNode leafNode) throws ASTConstructException {
				List<Object> modifiers = getModifiers();
				if (modifiers == null) {
					modifiers = new ArrayList<Object>();
					addChild(MODIFIERS, modifiers);
				}
				
				Modifier modifier = stringToModifier(leafNode.token.getKind().toUpperCase());
				modifiers.add(modifier);
				logger.fine("Modifier added: " + modifier.name());
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
	
	/**
	 * @return the modifiers
	 * @throws ChildTypeUnmatchException 
	 */
	public List<Object> getModifiers() throws ChildTypeUnmatchException {
		return this.getChildByDescriptor(MODIFIERS);
	}
}
