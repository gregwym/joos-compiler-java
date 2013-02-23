package ca.uwaterloo.joos.ast.expr;

import java.util.ArrayList;
import java.util.List;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.descriptor.ChildDescriptor;
import ca.uwaterloo.joos.ast.descriptor.ChildListDescriptor;
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
	public static final ChildListDescriptor ARGUMENTS = new ChildListDescriptor(Expression.class);

	public MethodInvokeExpression(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}

	public Name getName() throws ChildTypeUnmatchException {
		return (Name) this.getChildByDescriptor(NAME);
	}

	public Primary getPrimary() throws ChildTypeUnmatchException {
		return (Primary) this.getChildByDescriptor(PRIMARY);
	}
	
	@SuppressWarnings("unchecked")
	public List<Expression> getArguments() throws ChildTypeUnmatchException {
		return (List<Expression>) this.getChildByDescriptor(ARGUMENTS);
	}

	@Override
	public List<Node> processTreeNode(TreeNode treeNode) throws Exception {
		String kind = treeNode.getKind();
		if (kind.equals("simplename")) {
			Name name = new SimpleName(treeNode, this);
			this.addChild(NAME, name);
		} else if (kind.equals("qualifiedname")) {
			Name name = new QualifiedName(treeNode, this);
			this.addChild(NAME, name);
		} else if (kind.equals("primary")) {
			Primary primary = Primary.newPrimary(treeNode, this);
			this.addChild(PRIMARY, primary);
		} else if (kind.equals("expr")) {
			Expression arg = Expression.newExpression(treeNode, this);
			this.addChild(ARGUMENTS, arg);
		} else {
			List<Node> offers = new ArrayList<Node>();
			offers.addAll(treeNode.children);
			return offers;
		}
		return null;
	}

	@Override
	public void processLeafNode(LeafNode leafNode) throws Exception {
		String kind = leafNode.getKind();
		if (kind.equals("ID")) {
			Name name = new SimpleName(leafNode, this);
			this.addChild(NAME, name);
		}
	}

}
