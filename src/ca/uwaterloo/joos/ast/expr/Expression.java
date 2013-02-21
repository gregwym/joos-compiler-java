/**
 * 
 */
package ca.uwaterloo.joos.ast.expr;

import java.util.Set;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

/**
 * @author Greg Wang
 *
 */
public class Expression extends ASTNode {

	/**
	 * @param node
	 * @param parent
	 * @throws Exception
	 */
	public Expression(Node node, ASTNode parent) throws Exception {
		super(node, parent);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see ca.uwaterloo.joos.parser.ParseTreeTraverse.Traverser#processTreeNode(ca.uwaterloo.joos.parser.ParseTree.TreeNode)
	 */
	@Override
	public Set<Node> processTreeNode(TreeNode treeNode) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see ca.uwaterloo.joos.parser.ParseTreeTraverse.Traverser#processLeafNode(ca.uwaterloo.joos.parser.ParseTree.LeafNode)
	 */
	@Override
	public void processLeafNode(LeafNode leafNode) throws Exception {
		// TODO Auto-generated method stub

	}

}
