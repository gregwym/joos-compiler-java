package ca.uwaterloo.joos.ast.decl;

import java.util.HashSet;
import java.util.Set;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.descriptor.ChildDescriptor;
import ca.uwaterloo.joos.ast.type.ClassType;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

public class ClassDeclaration extends TypeDeclaration {

	protected static final ChildDescriptor SUPER = new ChildDescriptor(ClassType.class);

	public ClassDeclaration(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}

	/**
	 * @return the superClass
	 * @throws ChildTypeUnmatchException 
	 */
	public ClassType getSuperClass() throws ChildTypeUnmatchException {
		return (ClassType) this.getChildByDescriptor(ClassDeclaration.SUPER);
	}
	
	@Override
	public Set<Node> processTreeNode(TreeNode treeNode) throws Exception {
		if (treeNode.productionRule.getLefthand().equals("super")) {
//			ClassType superType = new ClassType(this);
//			addChild(SUPER, superType);
		}
		else {
			return super.processTreeNode(treeNode);
		}
		return new HashSet<Node>();
	}
}
