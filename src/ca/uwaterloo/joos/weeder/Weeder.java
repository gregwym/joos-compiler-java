/**
 * 
 */
package ca.uwaterloo.joos.weeder;

import java.util.ArrayList;
import java.util.List;

import ca.uwaterloo.joos.ast.AST;
import ca.uwaterloo.joos.ast.visitor.ASTVisitor;

/**
 * @author Greg Wang
 *
 */
public class Weeder {
	
	@SuppressWarnings("serial")
	public static class WeedException extends Exception {
		public WeedException(String string) {
			super(string);
		}
	}
	
	private List<ASTVisitor> checkers;

	/**
	 * 
	 */
	public Weeder() {
		this.checkers = new ArrayList<ASTVisitor>();
		this.checkers.add(new TypeDeclarationChecker());
		this.checkers.add(new ModifiersChecker());
		this.checkers.add(new IntegerChecker());
		this.checkers.add(new MethodDeclChecker());
	}

	public void weedAst(AST ast) throws Exception {
		for(ASTVisitor checker: this.checkers) {
			ast.getRoot().accept(checker);
		}
	}
}
