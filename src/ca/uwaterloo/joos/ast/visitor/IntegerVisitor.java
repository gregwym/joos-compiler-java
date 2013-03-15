/**
 * 
 */
package ca.uwaterloo.joos.ast.visitor;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.expr.primary.LiteralPrimary;

/**
 * @author wenzhuman
 *
 */
public abstract class IntegerVisitor extends ASTVisitor {

	/**
	 * 
	 */
	public boolean visit(ASTNode node) throws Exception {

		if (node instanceof LiteralPrimary) {
			LiteralPrimary literalNode = (LiteralPrimary) node;
			if (literalNode.getLiteralType().name().equals("INTLIT")) {
				this.visitInteger(literalNode);
			}
		}
		return true;
	}

	protected abstract void visitInteger(LiteralPrimary node) throws Exception;

}
