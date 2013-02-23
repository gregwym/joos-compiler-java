/**
 * 
 */
package ca.uwaterloo.joos.ast.statement;

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
	}

	@Override
	public List<Node> processTreeNode(TreeNode treeNode) throws Exception {
		String kind = treeNode.getKind();

		if (kind.equals("expr")) {
			this.addChild(WHILECONDITION, Expression.newExpression(treeNode, this));
		} else if (kind.equals("stmntnoshort") || kind.equals("stmnt")) {
			this.addChild(WHILESTATEMENTS, Statement.newStatement(treeNode, this));
		} else {
			return super.processTreeNode(treeNode);
		}

		return null;
	}

	@Override
	public void processLeafNode(LeafNode leafNode) throws Exception {

	}
}
