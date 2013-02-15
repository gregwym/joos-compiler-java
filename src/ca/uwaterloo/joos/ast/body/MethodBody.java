package ca.uwaterloo.joos.ast.body;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.visitor.ASTVisitor;

public class MethodBody extends ASTNode{
	
	public MethodBody(ASTNode parent) {
		super(parent);
	}
	
	/* (non-Javadoc)
	 * @see ca.uwaterloo.joos.ast.ASTNode#accept(ca.uwaterloo.joos.ast.ASTVisitor)
	 */
	@Override
	public void accept(ASTVisitor visitor) {
		visitor.willVisit(this);
		visitor.visit(this);
		visitor.didVisit(this);
	}

}
