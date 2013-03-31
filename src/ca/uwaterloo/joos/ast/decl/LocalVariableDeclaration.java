/**
 * 
 */
package ca.uwaterloo.joos.ast.decl;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.expr.ForInit;
import ca.uwaterloo.joos.parser.ParseTree.Node;

/**
 * @author Greg Wang
 *
 */
public class LocalVariableDeclaration extends VariableDeclaration implements ForInit{
	private int index;
	/**
	 * @param node
	 * @param parent
	 * @throws Exception
	 */
	public LocalVariableDeclaration(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}
	
	public void setIndex(int idx){
		index = idx;
	}
	
	public int getIndex(){
		return index;
	}

}
