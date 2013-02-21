/**
 * 
 */
package ca.uwaterloo.joos.ast.body;

import java.util.List;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

/**
 * @author Greg Wang
 *
 */
public class Block extends ASTNode {

	/**
	 * @param parent
	 * @throws Exception 
	 */
	public Block(Node node, ASTNode parent) throws Exception {
		super(node, parent);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Node> processTreeNode(TreeNode treeNode) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processLeafNode(LeafNode leafNode) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
