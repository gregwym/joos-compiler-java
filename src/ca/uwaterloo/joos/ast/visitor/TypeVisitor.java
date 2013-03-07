package ca.uwaterloo.joos.ast.visitor;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.type.Type;

public abstract class TypeVisitor extends ASTVisitor {

	/* (non-Javadoc)
	 * @see ca.uwaterloo.joos.ast.visitor.ASTVisitor#visit(ca.uwaterloo.joos.ast.ASTNode)
	 */
	public boolean visit(ASTNode node) throws Exception{
		if(node instanceof Type) {
			this.visitType((Type) node);
		}
		return true;
	}

	protected abstract void visitType(Type type) throws Exception;
}
