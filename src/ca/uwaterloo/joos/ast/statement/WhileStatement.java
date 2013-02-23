/**
 * 
 */
package ca.uwaterloo.joos.ast.statement;

import java.util.ArrayList;
import java.util.List;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.descriptor.ChildDescriptor;
import ca.uwaterloo.joos.ast.expr.Expression;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

/**
 * @author wenzhuman
 *
 */
public class WhileStatement extends Statement {
	public static final ChildDescriptor WHILECONDITION = new ChildDescriptor(Expression.class);
	public static final ChildDescriptor WHILESTATEMENTS = new ChildDescriptor(Statement.class);
	/**
	 * @param node
	 * @param parent
	 * @throws Exception
	 */
	public WhileStatement(Node node, ASTNode parent) throws Exception {
		super(node, parent);
		// TODO Auto-generated constructor stub
	}
	@Override
	public List<Node> processTreeNode(TreeNode treeNode) throws Exception {

		TreeNode ifcondition = (TreeNode) treeNode.children.get(2);
		TreeNode ifstatements = (TreeNode) treeNode.children.get(4);

		if (ifcondition.getKind().equals("expr")) {
			this.addChild(WHILECONDITION, Expression.newExpression(ifcondition, this));
		}
		if (ifstatements.getKind().equals("stmntnoshort")|ifstatements.getKind().equals("stmnt")) {

			this.addChild(WHILESTATEMENTS, Statement.newStatement(ifstatements, this));

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
}
