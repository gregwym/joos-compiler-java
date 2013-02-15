/**
 * 
 */
package ca.uwaterloo.joos.ast.decl;

import ca.uwaterloo.joos.ast.AST.ASTConstructException;
import ca.uwaterloo.joos.parser.ParseTree.Node;

/**
 * @author Greg Wang
 *
 */
public class ConstructorDeclaration extends MethodDeclaration {

	/**
	 * @throws ASTConstructException 
	 * 
	 */
	public ConstructorDeclaration(Node declNode) throws ASTConstructException {
		super(declNode);
		// TODO Auto-generated constructor stub
	}

}
