/**
 * 
 */
package ca.uwaterloo.joos.ast.statement;

import java.util.List;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.decl.LocalVariableDeclaration;
import ca.uwaterloo.joos.ast.descriptor.ChildDescriptor;
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

	/**
	 * @param node
	 * @param parent
	 * @throws Exception
	 */
	public ForStatement(Node node, ASTNode parent) throws Exception {
		super(node, parent);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Node> processTreeNode(TreeNode treeNode) throws Exception {
		String kind = treeNode.getKind();
		if (kind.equals("forinit")) {
			TreeNode child = (TreeNode) treeNode.children.get(0);
			TreeNode childchild = (TreeNode) child.children.get(0);
			if (childchild.getKind().equals("assignexpr")) {
				this.addChild(FORINITEXPR, new AssignmentExpression(child, this));
			} else if (childchild.getKind().equals("localvardecl")) {
				this.addChild(FORINITEXPR, new LocalVariableDeclaration(child, this));
			}
		} else if (kind.equals("fortest")) {
			TreeNode child = (TreeNode) treeNode.children.get(0);
			TreeNode childchild = (TreeNode) child.children.get(0);
			if (childchild.getKind().equals("assignexpr")) {
				this.addChild(FORTEST, new AssignmentExpression(child, this));
			}
		} else if (kind.equals("forupdate")) {
			TreeNode child = (TreeNode) treeNode.children.get(0);
			TreeNode childchild = (TreeNode) child.children.get(0);
			if (childchild.getKind().equals("stmntexpr")) {
				this.addChild(FORUPDATE, new AssignmentExpression(child, this));
			}
		}
		return null;
	}

	@Override
	public void processLeafNode(LeafNode leafNode) throws Exception {

	}

}
