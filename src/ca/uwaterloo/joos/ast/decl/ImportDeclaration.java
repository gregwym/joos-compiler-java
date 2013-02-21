package ca.uwaterloo.joos.ast.decl;

import java.util.ArrayList;
import java.util.List;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.descriptor.ChildDescriptor;
import ca.uwaterloo.joos.ast.name.QualifiedName;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

public abstract class ImportDeclaration extends ASTNode {
	public static final ChildDescriptor IMPORTNAME = new ChildDescriptor(QualifiedName.class);

	public ImportDeclaration(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}

	public static ImportDeclaration newImportDeclaration(Node node, ASTNode parent) throws Exception {
		if (node instanceof TreeNode) {
			TreeNode childNode = (TreeNode) ((TreeNode) node).children.get(0);
			if (childNode.productionRule.getLefthand().equals("ondemandimport")) {
				return new OnDemandImport(childNode,parent);
			}
			if (childNode.productionRule.getLefthand().equals("singletypeimport")) {
				return new SingleImport(childNode,parent);
			}
		}
		return null;
	}

	@Override
	public List<Node> processTreeNode(TreeNode treeNode) throws Exception {
		List<Node> offers = new ArrayList<Node>();
		if (treeNode.productionRule.getLefthand().equals("qualifiedname")) {
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

	public QualifiedName getImportName() throws ChildTypeUnmatchException {
		return (QualifiedName) this.getChildByDescriptor(ImportDeclaration.IMPORTNAME);
	}
}
