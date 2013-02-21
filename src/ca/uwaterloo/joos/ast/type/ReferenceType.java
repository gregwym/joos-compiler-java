package ca.uwaterloo.joos.ast.type;

import java.util.HashSet;
import java.util.Set;

import ca.uwaterloo.joos.ast.AST.ASTConstructException;
import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.descriptor.ChildDescriptor;
import ca.uwaterloo.joos.name.Name;
import ca.uwaterloo.joos.name.QualifiedName;
import ca.uwaterloo.joos.name.SimpleName;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

public class ReferenceType extends Type {

	public static final ChildDescriptor NAME = new ChildDescriptor(Name.class);

	public ReferenceType(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}

	public Name getName() throws ChildTypeUnmatchException {
		return (Name) this.getChildByDescriptor(ReferenceType.NAME);
	}

	@Override
	public Set<Node> processTreeNode(TreeNode treeNode) throws Exception {
		Set<Node> offers = new HashSet<Node>();
		if (treeNode.productionRule.getLefthand().equals("qualifiedname")) {
			Name name = new QualifiedName(treeNode, this);
			this.addChild(NAME, name);
		}
		if (treeNode.productionRule.getLefthand().equals("simplename")) {
			Name name = new SimpleName(treeNode, this);
			this.addChild(NAME, name);
		} else if (treeNode.productionRule.getLefthand().equals("arraytype")) {
			throw new ASTConstructException("ArrayType should not appears in ReferenceType");
		} else {
			for (Node n : treeNode.children)
				offers.add(n);
		}
		return offers;
	}

	@Override
	public void processLeafNode(LeafNode leafNode) throws Exception {

	}

}
