package ca.uwaterloo.joos.ast.decl;

import java.util.ArrayList;
import java.util.List;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.descriptor.ChildDescriptor;
import ca.uwaterloo.joos.ast.name.QualifiedName;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

public class PackageDeclaration extends ASTNode {
	public static final ChildDescriptor PACKAGENAME = new ChildDescriptor(QualifiedName.class);
	public PackageDeclaration(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}

	@Override
	public List<Node> processTreeNode(TreeNode treeNode) throws Exception {
		List<Node> offers = new ArrayList<Node>();
		if (treeNode.productionRule.getLefthand().equals("packagedecl")) {
			QualifiedName packageName = new QualifiedName(treeNode, this);
			addChild(PACKAGENAME, packageName);
		} else {
			offers.addAll(treeNode.children);
		}
		return offers;
	}

	@Override
	public void processLeafNode(LeafNode leafNode) throws Exception {

		
	}
}
