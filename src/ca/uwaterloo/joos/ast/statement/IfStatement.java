/**
 * 
 */
package ca.uwaterloo.joos.ast.statement;

import java.util.ArrayList;
import java.util.List;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.descriptor.ChildDescriptor;
import ca.uwaterloo.joos.ast.expr.Expression;
import ca.uwaterloo.joos.ast.expr.name.Name;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

/**
 * @author wenzhuman
 * 
 */
public class IfStatement extends Statement {
	public static final ChildDescriptor IFCONDITION = new ChildDescriptor(Expression.class);
	public static final ChildDescriptor IFSTATEMENT = new ChildDescriptor(Statement.class);
	public static final ChildDescriptor ELSESTATEMENT = new ChildDescriptor(Statement.class);

	public IfStatement(Node node, ASTNode parent) throws Exception {
		super(node, parent);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Node> processTreeNode(TreeNode treeNode) throws Exception {
		String kind = treeNode.getKind();
		if (kind.equals("expr")) {
			this.addChild(IFCONDITION, Expression.newExpression(treeNode, this));
		} else if (kind.equals("stmnt") | kind.equals("stmntnoshort")) {
			if (getIfStatement() == null) {
				this.addChild(IFSTATEMENT, Statement.newStatement(treeNode, this));
			} else {
				this.addChild(ELSESTATEMENT, Statement.newStatement(treeNode, this));
			}

		} else {
			List<Node> offers = new ArrayList<Node>();
			offers.addAll(treeNode.children);
			return offers;
		}

		return null;
	}

	@Override
	public void processLeafNode(LeafNode leafNode) throws Exception {

	}

	public Expression getIfCondition() throws ChildTypeUnmatchException {
		return (Expression) this.getChildByDescriptor(IFCONDITION);
	}

	public Statement getIfStatement() throws ChildTypeUnmatchException {
		return (Statement) this.getChildByDescriptor(IFSTATEMENT);
	}

	public Statement getElseStatement() throws ChildTypeUnmatchException {
		return (Statement) this.getChildByDescriptor(IFSTATEMENT);
	}

}
