package ca.uwaterloo.joos.ast.expr;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ca.uwaterloo.joos.ast.AST.ASTConstructException;
import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.descriptor.ChildListDescriptor;
import ca.uwaterloo.joos.ast.descriptor.SimpleDescriptor;
import ca.uwaterloo.joos.ast.expr.UnaryExpression.UnaryOperator;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

public class InfixExpression extends Expression {
	
	public static final SimpleDescriptor OPERATOR = new SimpleDescriptor(InfixOperator.class);
	public static final ChildListDescriptor OPERANDS = new ChildListDescriptor(Expression.class);
	
	public static enum InfixOperator {
		OR, AND, BOR, BAND, EQ, NEQ, LT, GT, LEQ, GEQ, INSTANCEOF, PLUS, MINUS, STAR, SLASH, PERCENT,   
	}

	public InfixExpression(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}
	
	private InfixOperator stringToInfixOperator(String name) throws ASTConstructException {
		for(InfixOperator operator: InfixOperator.values()) {
			if(operator.name().equals(name)) return operator;
		}
		return null;
	}
	
	public UnaryOperator getOperator() throws ChildTypeUnmatchException {
		return (UnaryOperator) this.getChildByDescriptor(OPERATOR);
	}
	
	@SuppressWarnings("unchecked")
	public List<Expression> getOperands() throws ChildTypeUnmatchException {
		return (List<Expression>) this.getChildByDescriptor(OPERANDS);
	}
	
	public static Set<String> getAcceptingKinds() {
		Set<String> acceptingKinds = new HashSet<String>();

		acceptingKinds.add("corexpr");
		acceptingKinds.add("candexpr");
		acceptingKinds.add("inorexpr");
		acceptingKinds.add("andexpr");
		acceptingKinds.add("equalexpr");
		acceptingKinds.add("relationexpr");
		acceptingKinds.add("addiexpr");
		acceptingKinds.add("multiexpr");
		
		return acceptingKinds;
	}

	@Override
	public List<Node> processTreeNode(TreeNode treeNode) throws Exception {
		if(treeNode.children.size() == 3) {
			Node first = treeNode.children.get(0);
			Node second = treeNode.children.get(1);
			Node third = treeNode.children.get(2);
			
			Expression operand1 = Expression.newExpression(first, this);
			InfixOperator operator = stringToInfixOperator(second.getKind().toUpperCase());
			Expression operand2 = Expression.newExpression(third, this);
			
			this.addChild(OPERANDS, operand1);
			this.addChild(OPERATOR, operator);
			this.addChild(OPERANDS, operand2);
		}
		else {
			throw new ASTConstructException("Infix Expression is expecting a treeNode with valid format");
		}
		return null;
	}
	
}
