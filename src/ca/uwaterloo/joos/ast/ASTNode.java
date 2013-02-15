package ca.uwaterloo.joos.ast;

import java.util.logging.Logger;

import ca.uwaterloo.joos.Main;
import ca.uwaterloo.joos.ast.visitor.ASTVisitor;


public abstract class ASTNode {
	protected static final Logger logger = Main.getLogger(ASTNode.class);
	
	protected ASTNode parent = null;
	protected String identifier = new String();

	public ASTNode(ASTNode parent) {
		this.parent = parent;
	}
	
	/**
	 * @return the parent
	 */
	public ASTNode getParent() {
		return parent;
	}
	
	/**
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}
	
	public abstract void accept(ASTVisitor visitor);
	
	@Override
	public String toString() {
		return "<ASTNode> id: " + this.identifier;
	}
	
	public String toString(int level) {
		String str = "";
		for(int i = 0; i < level; i++) {
			str += "  ";
		}
		str += "<" + this.getClass().getSimpleName() + "> ";
		str += this.identifier + " ";
		if(this.parent != null) str += "parent: " + this.parent.getClass().getSimpleName() + " ";
		return str;
	}
}
