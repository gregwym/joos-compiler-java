package ca.uwaterloo.joos.ast.decl;

import java.util.List;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.descriptor.ChildDescriptor;
import ca.uwaterloo.joos.ast.expr.name.QualifiedName;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

public abstract class ImportDeclaration extends ASTNode {
	public static final ChildDescriptor IMPORTNAME = new ChildDescriptor(QualifiedName.class);

	public ImportDeclaration(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}

	public static ImportDeclaration newImportDeclaration(TreeNode node, ASTNode parent) throws Exception {
		if (node.children.size() == 1) {
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
		if (treeNode.productionRule.getLefthand().equals("qualifiedname")) {
			QualifiedName qualifiedName = new QualifiedName(treeNode, this);
			addChild(IMPORTNAME, qualifiedName);
		} else {
			return super.processTreeNode(treeNode);
		}
		return null;
	}

	public QualifiedName getImportName() throws ChildTypeUnmatchException {
		return (QualifiedName) this.getChildByDescriptor(ImportDeclaration.IMPORTNAME);
	}
}
