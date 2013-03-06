package ca.uwaterloo.joos.ast.type;

import java.util.List;

import ca.uwaterloo.joos.ast.AST.ASTConstructException;
import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.descriptor.ChildDescriptor;
import ca.uwaterloo.joos.ast.expr.name.Name;
import ca.uwaterloo.joos.ast.expr.name.QualifiedName;
import ca.uwaterloo.joos.ast.expr.name.SimpleName;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

public class ReferenceType extends Type {

	public static final ChildDescriptor NAME = new ChildDescriptor(Name.class);
	
	public String fullyQualifedTypeName;

	public ReferenceType(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}

	public Name getName() throws ChildTypeUnmatchException {
		return (Name) this.getChildByDescriptor(ReferenceType.NAME);
	}

	@Override
	public List<Node> processTreeNode(TreeNode treeNode) throws Exception {
		if (treeNode.productionRule.getLefthand().equals("qualifiedname")) {
			Name name = new QualifiedName(treeNode, this);
			this.addChild(NAME, name);
		} else if (treeNode.productionRule.getLefthand().equals("simplename")) {
			Name name = new SimpleName(treeNode, this);
			this.addChild(NAME, name);
		} else if (treeNode.productionRule.getLefthand().equals("arraytype")) {
			throw new ASTConstructException("ArrayType should not appears in ReferenceType");
		} else {
			return super.processTreeNode(treeNode);
		}
		return null;
	}

	@Override
	public String getIdentifier() {
		String name = null;
		try {
			name = this.getName().getName();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return name;
	}

}
