package ca.uwaterloo.joos.ast.expr;

import java.util.ArrayList;
import java.util.List;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.descriptor.ChildDescriptor;
import ca.uwaterloo.joos.ast.expr.name.Name;
import ca.uwaterloo.joos.ast.expr.name.QualifiedName;
import ca.uwaterloo.joos.ast.expr.name.SimpleName;
import ca.uwaterloo.joos.ast.expr.primary.Primary;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

public class MethodInvokeExpression extends Expression {

	public static final ChildDescriptor NAME = new ChildDescriptor(Name.class);
	public static final ChildDescriptor PRIMARY = new ChildDescriptor(Primary.class);

	public MethodInvokeExpression(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}

	@Override
	public List<Node> processTreeNode(TreeNode treeNode) throws Exception {
		if (treeNode.getKind().equals("simplename")) {
			Name name = new SimpleName(treeNode, this);
			this.addChild(NAME, name);
		} else if (treeNode.getKind().equals("qualifiedname")) {
			Name name = new QualifiedName(treeNode, this);
			this.addChild(NAME, name);
		} else if (treeNode.getKind().equals("primary")) {
			Primary primary = Primary.newPrimary(treeNode, this);
			this.addChild(PRIMARY, primary);
		} else if (treeNode.getKind().equals("methodinvoke")) {

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
