package ca.uwaterloo.joos.ast.decl;

import java.util.List;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.descriptor.ChildDescriptor;
import ca.uwaterloo.joos.ast.expr.Expression;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

public abstract class VariableDeclaration extends BodyDeclaration {
	private int index; //Holds the index of the declaration
	
	protected static final ChildDescriptor INITIAL = new ChildDescriptor(Expression.class);

	public VariableDeclaration(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ca.uwaterloo.joos.parser.ParseTreeTraverse.Traverser#processTreeNode(
	 * ca.uwaterloo.joos.parser.ParseTree.TreeNode)
	 */
	
	
	public void setIndex(int idx){
		index = idx;
	}
	
	public int getIndex(){
		return index;
	}
	
	public Expression getInitial() throws ChildTypeUnmatchException{
		return (Expression) this.getChildByDescriptor(INITIAL);
	}
	
	@Override
	public List<Node> processTreeNode(TreeNode treeNode) throws Exception {
		if (treeNode.productionRule.getLefthand().equals("expr")) {
			Expression initial = Expression.newExpression(treeNode, this);
			this.addChild(INITIAL, initial);
		} else {
			return super.processTreeNode(treeNode);
		}
		return null;
	}
}
