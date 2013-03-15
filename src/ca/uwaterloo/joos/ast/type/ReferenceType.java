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
	
	protected String fullyQualifedName;

	public ReferenceType(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}
	
	public ReferenceType(String type) throws Exception {
		this(type, null);
	}
	
	public ReferenceType(String type, ASTNode parent) throws Exception {
		super(null, parent);
		Name name = null;
		if(type.contains(".")) {
			name = new QualifiedName(type);
		} else {
			name = new SimpleName(type);
		}
		this.addChild(NAME, name);
		this.fullyQualifedName = type;
	}

	public Name getName() throws ChildTypeUnmatchException {
		return (Name) this.getChildByDescriptor(ReferenceType.NAME);
	}
	
	@Override
	public String getFullyQualifiedName() throws Exception {
		if(this.fullyQualifedName == null) return this.getName().getName();
		return this.fullyQualifedName;
	}
	
	public void setFullyQualifiedName(String fullyQualifedTypeName) {
		this.fullyQualifedName = fullyQualifedTypeName;
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
