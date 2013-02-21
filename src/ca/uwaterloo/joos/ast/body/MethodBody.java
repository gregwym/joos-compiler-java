package ca.uwaterloo.joos.ast.body;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.visitor.ASTVisitor;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

public class MethodBody extends ASTNode{
	
	public MethodBody(Node node, ASTNode parent) {
		super(parent);
		
		assert node instanceof TreeNode : "InterfaceBody is expecting a TreeNode";
		
	}
	
	/* (non-Javadoc)
	 * @see ca.uwaterloo.joos.ast.ASTNode#accept(ca.uwaterloo.joos.ast.ASTVisitor)
	 */
	@Override
	public void accept(ASTVisitor visitor) throws Exception{
		visitor.willVisit(this);
		visitor.visit(this);
		visitor.didVisit(this);
	}
	
	@Override
	public Set<Node> processTreeNode(TreeNode treeNode) throws Exception {
		return null;
	}

	@Override
	public void processLeafNode(LeafNode leafNode) throws Exception {
		
	}

}
