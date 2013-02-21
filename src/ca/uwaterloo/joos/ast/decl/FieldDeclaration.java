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
public class FieldDeclaration extends VariableDeclaration {

	/**
	 * @param node
	 * @param parent
	 * @throws Exception
	 */
	public FieldDeclaration(Node node, ASTNode parent) throws Exception {
		super(node, parent);
		// TODO Auto-generated constructor stub
	}

}
