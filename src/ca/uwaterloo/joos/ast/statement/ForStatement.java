/**
 * 
 */
package ca.uwaterloo.joos.ast.statement;

import java.util.ArrayList;
import java.util.List;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.ASTNode.ChildTypeUnmatchException;
import ca.uwaterloo.joos.ast.decl.LocalVariableDeclaration;
import ca.uwaterloo.joos.ast.descriptor.ChildDescriptor;
import ca.uwaterloo.joos.ast.expr.Expression;
import ca.uwaterloo.joos.ast.expr.ForInit;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

/**
 * @author wenzhuman
 * 
 */
public class ForStatement extends Statement {
	public static final ChildDescriptor INIT = new ChildDescriptor(ForInit.class);
	public static final ChildDescriptor TEST = new ChildDescriptor(Expression.class);
	public static final ChildDescriptor UPDATE = new ChildDescriptor(Expression.class);
	public static final ChildDescriptor STATEMENT = new ChildDescriptor(Statement.class);

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
		if (kind.equals("forinit")) {
			TreeNode child = (TreeNode) treeNode.children.get(0);
			if (child.getKind().equals("stmntexpr")) {
				this.addChild(INIT, Expression.newExpression(child, this));
			}
			if (child.getKind().equals("localvardecl")) {
				this.addChild(INIT, new LocalVariableDeclaration(child, this));
			}
		} else if (kind.equals("fortest")) {
			this.addChild(TEST, Expression.newExpression(treeNode, this));
		} else if (kind.equals("forupdate")) {
			this.addChild(UPDATE, Expression.newExpression(treeNode, this));
		} else if (kind.equals("stmnt") | kind.equals("stmntnoshort")) {
			this.addChild(STATEMENT, Statement.newStatement(treeNode, this));
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

	public Statement getForStatement() throws ChildTypeUnmatchException {
		return (Statement) this.getChildByDescriptor(STATEMENT);
	}

}
