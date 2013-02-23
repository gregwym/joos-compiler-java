package ca.uwaterloo.joos.ast.decl;

import java.util.List;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.descriptor.ChildDescriptor;
import ca.uwaterloo.joos.ast.expr.name.QualifiedName;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

public class PackageDeclaration extends ASTNode {
	public static final ChildDescriptor PACKAGENAME = new ChildDescriptor(QualifiedName.class);
	public PackageDeclaration(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}

	@Override
	public List<Node> processTreeNode(TreeNode treeNode) throws Exception {
		if (treeNode.productionRule.getLefthand().equals("qualifiedname")) {
			QualifiedName packageName = new QualifiedName(treeNode, this);
			addChild(PACKAGENAME, packageName);
		} else {
			return super.processTreeNode(treeNode);
		}
		return null;
	}
}
