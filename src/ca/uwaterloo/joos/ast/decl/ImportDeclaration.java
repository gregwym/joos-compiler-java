package ca.uwaterloo.joos.ast.decl;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.visitor.ASTVisitor;

public class ImportDeclaration extends ASTNode{
	
	public ImportDeclaration(ASTNode parent) {
		super(parent);
	}
	
	/* (non-Javadoc)
	 * @see ca.uwaterloo.joos.ast.ASTNode#accept(ca.uwaterloo.joos.ast.ASTVisitor)
	 */
	@Override
	public void accept(ASTVisitor visitor) {
		visitor.willVisit(this);
		if(visitor.visit(this)) {
			
		}
		visitor.didVisit(this);
	}
}
