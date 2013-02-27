/**
 * 
 */
package ca.uwaterloo.joos.ast.visitor;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.Modifiers;

/**
 * @author Greg Wang
 *
 */
public abstract class ModifiersVisitor extends ASTVisitor {
	
	/* (non-Javadoc)
	 * @see ca.uwaterloo.joos.ast.visitor.ASTVisitor#visit(ca.uwaterloo.joos.ast.ASTNode)
	 */
	public boolean visit(ASTNode node) throws Exception{
		if(node instanceof Modifiers) {
			this.visitModifiers((Modifiers) node);
		}
		return true;
	}

	protected abstract void visitModifiers(Modifiers modifiers) throws Exception;
}
