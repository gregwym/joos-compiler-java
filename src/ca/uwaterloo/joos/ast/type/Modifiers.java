package ca.uwaterloo.joos.ast.type;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import ca.uwaterloo.joos.ast.AST.ASTConstructException;
import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

public class Modifiers extends ASTNode {

	public static enum Modifier {
		ABSTRACT, FINAL, NATIVE, PUBLIC, PROTECTED, STATIC
	}

	List<Modifier> modifiers;

	public Modifiers(Node modifiersNode) throws ASTConstructException {
		assert modifiersNode instanceof TreeNode : "Modifiers is expecting a TreeNode";
		
		this.modifiers = new ArrayList<Modifier>();
	
		Queue<Node> nodeQueue = new LinkedList<Node>();
		nodeQueue.offer(modifiersNode);

		while (!nodeQueue.isEmpty()) {
			Node node = nodeQueue.poll();
			logger.finer("Dequeued node " + node.toString());
			
			if (node instanceof TreeNode) {
				TreeNode treeNode = (TreeNode) node;
				for(Node n: treeNode.children) {
					nodeQueue.offer(n);
				}
			}
			else if(node instanceof LeafNode) {
				LeafNode leafNode = (LeafNode) node;
				Modifier modifier = this.stringToModifier(leafNode.token.getKind().toUpperCase());
				logger.fine("Modifier added: " + modifier.name());
				this.modifiers.add(modifier);
			}
		}
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
		str += "<Modifiers> ";
		for (Modifier modifier : this.modifiers)
			str += modifier.name() + " ";
		str += "\n";
		return str;
	}
}
