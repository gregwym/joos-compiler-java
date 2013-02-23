/**
 * 
 */
package ca.uwaterloo.joos.ast.statement;

import java.util.ArrayList;
import java.util.List;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.decl.LocalVariableDeclaration;
import ca.uwaterloo.joos.ast.descriptor.ChildDescriptor;
import ca.uwaterloo.joos.ast.descriptor.ChildListDescriptor;
import ca.uwaterloo.joos.ast.expr.AssignmentExpression;
import ca.uwaterloo.joos.ast.expr.CastExpression;
import ca.uwaterloo.joos.ast.expr.Expression;
import ca.uwaterloo.joos.ast.expr.name.SimpleName;
import ca.uwaterloo.joos.ast.expr.primary.Primary;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

/**
 * @author wenzhuman
 * 
 */
public class ForStatement extends Statement {
	public static final ChildDescriptor FORINITEXPR = new ChildDescriptor(Expression.class);
	public static final ChildDescriptor FORTEST = new ChildDescriptor(Expression.class);
	public static final ChildDescriptor FORUPDATE = new ChildDescriptor(Expression.class);
	public static final ChildDescriptor FORSTATEMENT = new ChildDescriptor(Statement.class);

	/**
	 * @param node
	 * @param parent
	 * @throws Exception
	 */
	public ForStatement(Node node, ASTNode parent) throws Exception {
		super(node, parent);

	}

	@Override
	public List<Node> processTreeNode(TreeNode treeNode) throws Exception {
		String kind = treeNode.getKind();
		if (treeNode.children.size() == 9) {
			TreeNode forstatement = (TreeNode) treeNode.children.get(8);
			if (forstatement.getKind().equals("stmnt") | forstatement.getKind().equals("stmntnoshort")) {
				this.addChild(FORSTATEMENT, Statement.newStatement(forstatement, this));
			}
		}
		if (kind.equals("forinit")) {
			TreeNode child = (TreeNode) treeNode.children.get(0);
			if (child.getKind().equals("stmntexpr")) {
				this.addChild(FORINITEXPR, Expression.newExpression(child, this));
			}
		} else if (kind.equals("fortest")) {
			TreeNode child = (TreeNode) treeNode.children.get(0);
			if (child.getKind().equals("expr")) {
				this.addChild(FORTEST, Expression.newExpression(child, this));
			}
		} else if (kind.equals("forupdate")) {
			TreeNode child = (TreeNode) treeNode.children.get(0);
			if (child.getKind().equals("stmntexpr")) {
				this.addChild(FORUPDATE, Expression.newExpression(child, this));
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

}
