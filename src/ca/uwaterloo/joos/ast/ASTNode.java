package ca.uwaterloo.joos.ast;


public class ASTNode {
	
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
		return "<ASTNode> parent: " + this.parent.getClass().getName();
	}
	
	public String toString(int level) {
		String str = "";
		for(int i = 0; i < level; i++) {
			str += "  ";
		}
		return str;
	}
}
