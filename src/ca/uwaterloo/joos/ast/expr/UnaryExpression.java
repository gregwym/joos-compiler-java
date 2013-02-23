package ca.uwaterloo.joos.ast.expr;

import java.util.List;

import ca.uwaterloo.joos.ast.AST.ASTConstructException;
import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.descriptor.ChildDescriptor;
import ca.uwaterloo.joos.ast.descriptor.SimpleDescriptor;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

public class UnaryExpression extends Expression {
	
	public static final SimpleDescriptor OPERATOR = new SimpleDescriptor(UnaryOperator.class);
	public static final ChildDescriptor OPERAND = new ChildDescriptor(UnaryExpression.class);
	
	public static enum UnaryOperator {
		MINUS, NOT, 
	}
	
	public UnaryExpression(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}
	
	private UnaryOperator stringToUnaryOperator(String name) throws ASTConstructException {
		for(UnaryOperator operator: UnaryOperator.values()) {
			if(operator.name().equals(name)) return operator;
		}
		return null;
	}
	
	public UnaryOperator getOperator() throws ChildTypeUnmatchException {
		return (UnaryOperator) this.getChildByDescriptor(OPERATOR);
	}
	
	public UnaryExpression getOperand() throws ChildTypeUnmatchException {
		return (UnaryExpression) this.getChildByDescriptor(OPERAND);
	}
	
	@Override
	public List<Node> processTreeNode(TreeNode treeNode) throws Exception {
		if(treeNode.children.size() == 2) {
			Node first = treeNode.children.get(0);
			Node second = treeNode.children.get(1);
			
			UnaryOperator operator = stringToUnaryOperator(first.getKind().toUpperCase());
			this.addChild(OPERATOR, operator);
			
			Expression operand = Expression.newExpression(second, this);
			this.addChild(OPERAND, operand);
		}
		return null;
	}

	@Override
	public void processLeafNode(LeafNode leafNode) throws Exception {
		
	}

}
