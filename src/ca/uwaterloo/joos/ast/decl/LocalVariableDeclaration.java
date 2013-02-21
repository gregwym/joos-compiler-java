/**
 * 
 */
package ca.uwaterloo.joos.ast.decl;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;

/**
 * @author Greg Wang
 *
 */
public class LocalVariableDeclaration extends VariableDeclaration {

	/**
	 * @param node
	 * @param parent
	 * @throws Exception
	 */
	public LocalVariableDeclaration(Node node, ASTNode parent) throws Exception {
		super(node, parent);
		// TODO Auto-generated constructor stub
	}

}
