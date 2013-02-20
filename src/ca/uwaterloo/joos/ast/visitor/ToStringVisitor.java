/**
 * 
 */
package ca.uwaterloo.joos.ast.visitor;

import ca.uwaterloo.joos.ast.ASTNode;

/**
 * @author Greg Wang
 * 
 */
public class ToStringVisitor extends ASTVisitor {

	private String theString = new String();
	private int level = 0;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ca.uwaterloo.joos.ast.visitor.ASTVisitor#visit(ca.uwaterloo.joos.ast.
	 * ASTNode)
	 */
	public boolean visit(ASTNode node) throws Exception {
		for (int i = 0; i < level; i++) {
			this.theString += "  ";
		}
		this.theString += node.toString() + "\n";
		return true;
	}

	/**
	 * Get the string representation of the AST with given node as the root
	 * 
	 * @return Generated string representation of the AST
	 */
	public String getString() {
		return this.theString;
	}

	/* (non-Javadoc)
	 * @see ca.uwaterloo.joos.ast.visitor.ASTVisitor#willVisit(ca.uwaterloo.joos.ast.ASTNode)
	 */
	@Override
	public void willVisit(ASTNode node) {
		this.level++;
	}

	/* (non-Javadoc)
	 * @see ca.uwaterloo.joos.ast.visitor.ASTVisitor#didVisit(ca.uwaterloo.joos.ast.ASTNode)
	 */
	@Override
	public void didVisit(ASTNode node) {
		this.level--;
	}

}
