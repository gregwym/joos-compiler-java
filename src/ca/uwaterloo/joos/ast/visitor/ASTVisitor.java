/**
 * 
 */
package ca.uwaterloo.joos.ast.visitor;

import ca.uwaterloo.joos.ast.ASTNode;

/**
 * @author Greg Wang
 *
 */
public abstract class ASTVisitor {

	/**
	 * 
	 */
	public ASTVisitor() {
	}
	
	public abstract void willVisit(ASTNode node);

	public boolean visit(ASTNode node) throws Exception {
		return true;
	}
	
	public abstract void didVisit(ASTNode node);
}
