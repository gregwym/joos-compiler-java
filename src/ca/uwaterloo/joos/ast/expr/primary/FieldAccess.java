package ca.uwaterloo.joos.ast.expr.primary;

import java.util.List;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.descriptor.ChildDescriptor;
import ca.uwaterloo.joos.ast.expr.Lefthand;
import ca.uwaterloo.joos.ast.expr.name.Name;
import ca.uwaterloo.joos.ast.expr.name.SimpleName;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

public class FieldAccess extends Primary implements Lefthand {

	public static final ChildDescriptor NAME = new ChildDescriptor(Name.class);
	public static final ChildDescriptor PRIMARY = new ChildDescriptor(Primary.class);

	public FieldAccess(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}
	
	public Name getName() throws ChildTypeUnmatchException {
		return (Name) this.getChildByDescriptor(NAME);
	}

	public Primary getPrimary() throws ChildTypeUnmatchException {
		return (Primary) this.getChildByDescriptor(PRIMARY);
	}

	@Override
	public List<Node> processTreeNode(TreeNode treeNode) throws Exception {
		String kind = treeNode.getKind();
		if (kind.equals("primary")) {
			Primary primary = Primary.newPrimary(treeNode, this);
			this.addChild(PRIMARY, primary);
		} else {
			return super.processTreeNode(treeNode);
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
