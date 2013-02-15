package ca.uwaterloo.joos.ast;

import java.util.logging.Logger;

import ca.uwaterloo.joos.Main;


public class ASTNode {
	protected static final Logger logger = Main.getLogger(ASTNode.class);
	protected ASTNode parent = null;
	protected String identifier = new String();

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

	public ASTNode() {

	}
	
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
		return str;
	}
}
