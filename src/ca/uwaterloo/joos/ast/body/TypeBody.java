/**
 *
 */
package ca.uwaterloo.joos.ast.body;

import java.util.ArrayList;
import java.util.List;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.decl.ConstructorDeclaration;
import ca.uwaterloo.joos.ast.decl.FieldDeclaration;
import ca.uwaterloo.joos.ast.decl.MethodDeclaration;
import ca.uwaterloo.joos.ast.descriptor.ChildListDescriptor;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

/**
 * @author Greg Wang
 *
 */
@SuppressWarnings("unchecked")
public abstract class TypeBody extends ASTNode {

	public static final ChildListDescriptor METHODS = new ChildListDescriptor(MethodDeclaration.class);
	public static final ChildListDescriptor CONSTRUCTORS = new ChildListDescriptor(ConstructorDeclaration.class);
	public static final ChildListDescriptor FIELDS = new ChildListDescriptor(FieldDeclaration.class);

	/**
	 * @throws Exception
	 *
	 */
	public TypeBody(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}

	public List<MethodDeclaration> getMethods() throws ChildTypeUnmatchException {
		return (List<MethodDeclaration>) this.getChildByDescriptor(TypeBody.METHODS);
	}

	public List<ConstructorDeclaration> getConstructors() throws ChildTypeUnmatchException {
		return (List<ConstructorDeclaration>) this.getChildByDescriptor(TypeBody.CONSTRUCTORS);
	}

	public List<FieldDeclaration> getFields() throws ChildTypeUnmatchException {
		return (List<FieldDeclaration>) this.getChildByDescriptor(TypeBody.FIELDS);
	}

	@Override
	public List<Node> processTreeNode(TreeNode treeNode) throws Exception {
		List<Node> offers = new ArrayList<Node>();
		if (treeNode.productionRule.getLefthand().equals("constructordecl")) {
			ConstructorDeclaration decl = new ConstructorDeclaration(treeNode, this);
			addChild(TypeBody.CONSTRUCTORS, decl);
		} else if (treeNode.productionRule.getLefthand().equals("absmethoddecl") ||
		           treeNode.productionRule.getLefthand().equals("methoddecl")) {
			MethodDeclaration decl = new MethodDeclaration(treeNode, this);
			addChild(TypeBody.METHODS, decl);
		} else if (treeNode.productionRule.getLefthand().equals("fielddecl")) {
			FieldDeclaration decl = new FieldDeclaration(treeNode, this);
			addChild(TypeBody.FIELDS, decl);
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
