/**
 * 
 */
package ca.uwaterloo.joos.ast;

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

	public boolean visit(ASTNode node) {
		return true;
	}
	
	public abstract void didVisit(ASTNode node);
}
