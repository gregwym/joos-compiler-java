package ca.uwaterloo.joos.ast.type;

import java.util.Set;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;
import ca.uwaterloo.joos.scanner.Token;

public class PrimitiveType extends Type{
	public Token typeOfPrimitive;
	public PrimitiveType(Node primitiveNod, ASTNode parent) throws Exception {
		super(primitiveNod,parent);
	}
	@Override
	public Set<Node> processTreeNode(TreeNode treeNode) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void processLeafNode(LeafNode leafNode) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
