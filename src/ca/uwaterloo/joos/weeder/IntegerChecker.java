/**
 * 
 */
package ca.uwaterloo.joos.weeder;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.expr.primary.LiteralPrimary;
import ca.uwaterloo.joos.ast.visitor.IntegerVisitor;
import ca.uwaterloo.joos.weeder.Weeder.WeedException;

/**
 * @author wenzhuman
 * 
 */
public class IntegerChecker extends IntegerVisitor {

	/**
	 * @throws Exception
	 * 
	 */
	@Override
	public void willVisit(ASTNode node) {

	}

	@Override
	public void didVisit(ASTNode node) {

	}

	@Override
	protected void visitInteger(LiteralPrimary node) throws Exception {
		String intPositiveThreshold = "2147483647";
		String intNegThreshold = "2147483648";
		if (node.getParent().getClass().getSimpleName().equals("UnaryExpression")) {
			checkIntRange(node.getValue(), intNegThreshold);
		} else {
			checkIntRange(node.getValue(), intPositiveThreshold);
		}

	}

	private void checkIntRange(String intString, String intergerThreshold) throws Exception {

		if (intString.length() == intergerThreshold.length()) {
			for (int i = 0; i < intergerThreshold.length(); i++) {
				if ((int) intString.charAt(i) > (int) intergerThreshold.charAt(i)) {

					throw new WeedException("Interger out of Range");
				}
				if ((int) intString.charAt(i) < (int) intergerThreshold.charAt(i)) {
					break;
				}
			}
		}
		if (intString.length() > intergerThreshold.length()) {
			throw new Exception("2:Interger out of Range");
		}
	}
}
