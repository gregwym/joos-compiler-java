package ca.uwaterloo.joos.ast.decl;

import java.util.List;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.descriptor.ChildDescriptor;
import ca.uwaterloo.joos.ast.descriptor.ChildListDescriptor;
import ca.uwaterloo.joos.ast.type.ReferenceType;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

public class ClassDeclaration extends TypeDeclaration {

	protected static final ChildDescriptor SUPER = new ChildDescriptor(ReferenceType.class);
	

	public ClassDeclaration(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}

	/**
	 * @return the superClass
	 * @throws ChildTypeUnmatchException
	 */
	public ReferenceType getSuperClass() throws ChildTypeUnmatchException {
		return (ReferenceType) this.getChildByDescriptor(ClassDeclaration.SUPER);
	}

	@Override
	public List<Node> processTreeNode(TreeNode treeNode) throws Exception {
		if (treeNode.productionRule.getLefthand().equals("super")&&(treeNode.children.size()!=0)) {
			ReferenceType superType = new ReferenceType(treeNode, this);
			addChild(SUPER, superType);
		}
		else if(treeNode.productionRule.getLefthand().equals("interfaces")&&(treeNode.children.size()!=0)){
			ReferenceType implementType = new ReferenceType(treeNode, this);
			addChild(IMPLEMNTS, implementType);
		}
		else {
			return super.processTreeNode(treeNode);
		}
		return null;
	}
}
