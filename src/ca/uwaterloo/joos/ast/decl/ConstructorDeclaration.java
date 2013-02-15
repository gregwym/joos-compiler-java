/**
 * 
 */
package ca.uwaterloo.joos.ast.decl;

import ca.uwaterloo.joos.ast.AST.ASTConstructException;
import ca.uwaterloo.joos.ast.ASTNode;
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
	public ConstructorDeclaration(Node declNode, ASTNode parent) throws ASTConstructException {
		super(declNode, parent);
		// TODO Auto-generated constructor stub
	}

}
