package ca.uwaterloo.joos.ast.decl;

import java.util.ArrayList;
import java.util.List;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.descriptor.ChildDescriptor;
import ca.uwaterloo.joos.ast.name.QualifiedName;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

public class ImportDeclaration extends ASTNode {
	public static final ChildDescriptor IMPORTNAME = new ChildDescriptor(QualifiedName.class);

	public ImportDeclaration(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}

	@Override
	public List<Node> processTreeNode(TreeNode treeNode) throws Exception {
		List<Node> offers = new ArrayList<Node>();
		if (treeNode.productionRule.getLefthand().equals("importdecls")) {
			QualifiedName qualifiedName = new QualifiedName(treeNode, this);
			addChild(IMPORTNAME, qualifiedName);
		} else {
			offers.addAll(treeNode.children);
		}
		return offers;
	}

	@Override
	public void processLeafNode(LeafNode leafNode) throws Exception {

	}
}
