/**
 * 
 */
package ca.uwaterloo.joos.ast.body;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.visitor.ASTVisitor;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

/**
 * @author Greg Wang
 *
 */
public class Block extends ASTNode {

	/**
	 * @param parent
	 */
	public Block(TreeNode treeNode, ASTNode parent) {
		super(parent);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see ca.uwaterloo.joos.ast.ASTNode#accept(ca.uwaterloo.joos.ast.visitor.ASTVisitor)
	 */
	@Override
	public void accept(ASTVisitor visitor) throws Exception {
		// TODO Auto-generated method stub

	}

}
