/**
 * 
 */
package ca.uwaterloo.joos.ast.statement;

import java.util.ArrayList;
import java.util.List;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.descriptor.ChildDescriptor;
import ca.uwaterloo.joos.ast.descriptor.ChildListDescriptor;
import ca.uwaterloo.joos.ast.expr.Expression;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

/**
 * @author wenzhuman
 * 
 */
public class IfStatement extends Statement {
	public static final ChildDescriptor IFCONDITION = new ChildDescriptor(Expression.class);
	public static final ChildDescriptor IFSTATEMENTS = new ChildDescriptor(Statement.class);
	public static final ChildDescriptor ELSESTATEMENTS = new ChildDescriptor(Statement.class);

	public IfStatement(Node node, ASTNode parent) throws Exception {
		super(node, parent);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Node> processTreeNode(TreeNode treeNode) throws Exception {

		TreeNode ifcondition = (TreeNode) treeNode.children.get(2);
		TreeNode ifstatements = (TreeNode) treeNode.children.get(4);

		if (ifcondition.getKind().equals("expr")) {
			this.addChild(IFCONDITION, Expression.newExpression(ifcondition, this));
		}
		if (ifstatements.getKind().equals("stmntnoshort")|ifstatements.getKind().equals("stmnt")) {

			this.addChild(IFSTATEMENTS, Statement.newStatement(ifstatements, this));

		} else {
			List<Node> offers = new ArrayList<Node>();
			offers.addAll(treeNode.children);
			return offers;
		}
		if (treeNode.children.size() == 7) {
			TreeNode elsestatements = (TreeNode) treeNode.children.get(6);
			if (elsestatements.getKind().equals("stmnt")|elsestatements.getKind().equals("stmntnoshort")) {

				this.addChild(ELSESTATEMENTS, Statement.newStatement(elsestatements, this));
			}
		}

		return null;
	}

	@Override
	public void processLeafNode(LeafNode leafNode) throws Exception {

	}

}
