package ca.uwaterloo.joos.ast.expr;

import java.util.ArrayList;
import java.util.List;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.descriptor.ChildDescriptor;
import ca.uwaterloo.joos.ast.name.Name;
import ca.uwaterloo.joos.ast.name.QualifiedName;
import ca.uwaterloo.joos.ast.name.SimpleName;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

public class AssignmentExpression extends Expression {
	
	public static final ChildDescriptor LEFTHAND = new ChildDescriptor(Lefthand.class);
	public static final ChildDescriptor EXPR = new ChildDescriptor(Expression.class);
	
	public AssignmentExpression(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}
	
	@Override
	public List<Node> processTreeNode(TreeNode treeNode) throws Exception {
		if (treeNode.getKind().equals("simplename")) {
			Name name = new SimpleName(treeNode, this);
			this.addChild(LEFTHAND, name);
		} else if (treeNode.getKind().equals("qualifiedname")) {
			Name name = new QualifiedName(treeNode, this);
			this.addChild(LEFTHAND, name);
		} else if (treeNode.getKind().equals("arrayaccess") || 
				treeNode.getKind().equals("fieldaccess")) {
			// TODO Turn of after expr finished
//			Lefthand primary = (Lefthand) Primary.newPrimary(treeNode, this);
//			this.addChild(LEFTHAND, primary);
		} else if (treeNode.getKind().equals("assignexpr")) {
			// TODO Turn of after expr finished
//			Expression expr = Expression.newExpression(treeNode, this);
//			this.addChild(EXPR, expr);
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
