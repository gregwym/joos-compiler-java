/**
 *
 */
package ca.uwaterloo.joos.ast.body;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	public static final ChildListDescriptor FIELDS = new ChildListDescriptor(MethodDeclaration.class);

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
	public Set<Node> processTreeNode(TreeNode treeNode) throws Exception {
		Set<Node> offers = new HashSet<Node>();
		if (treeNode.productionRule.getLefthand().equals("constructordecl")) {
			List<ConstructorDeclaration> decls = getConstructors();
			if (decls == null) {
				decls = new ArrayList<ConstructorDeclaration>();
				addChild(TypeBody.CONSTRUCTORS, decls);
			}
			ConstructorDeclaration decl = new ConstructorDeclaration(treeNode, this);
			decls.add(decl);
		} else if (treeNode.productionRule.getLefthand().equals("absmethoddecl") || treeNode.productionRule.getLefthand().equals("methoddecl")) {
			List<MethodDeclaration> decls = getMethods();
			if (decls == null) {
				decls = new ArrayList<MethodDeclaration>();
				addChild(TypeBody.METHODS, decls);
			}
			MethodDeclaration decl = new MethodDeclaration(treeNode, this);
			decls.add(decl);
		} else if (treeNode.productionRule.getLefthand().equals("fielddecl")) {
			List<FieldDeclaration> decls = getFields();
			if (decls == null) {
				decls = new ArrayList<FieldDeclaration>();
				addChild(TypeBody.FIELDS, decls);
			}
			FieldDeclaration decl = new FieldDeclaration(treeNode, this);
			decls.add(decl);
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
