/**
 *
 */
package ca.uwaterloo.joos.ast.body;

import java.util.HashSet;
import java.util.Set;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.decl.BodyDeclaration;
import ca.uwaterloo.joos.ast.decl.ConstructorDeclaration;
import ca.uwaterloo.joos.ast.decl.FieldDeclaration;
import ca.uwaterloo.joos.ast.decl.MethodDeclaration;
import ca.uwaterloo.joos.ast.visitor.ASTVisitor;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;
import ca.uwaterloo.joos.parser.ParseTreeTraverse;

/**
 * @author Greg Wang
 *
 */
@SuppressWarnings("unchecked")
public abstract class TypeBody extends ASTNode {
	/**
	 * @throws Exception 
	 *
	 */
	public TypeBody(Node node, ASTNode parent) throws Exception {
		super(parent);
		
		ParseTreeTraverse traverse = new ParseTreeTraverse(this);

		traverse.traverse(node);
	}

	public Set<MethodDeclaration> getMethods() {
		return (Set<MethodDeclaration>) this.getMember(MethodDeclaration.class);
	}
	
	public Set<ConstructorDeclaration> getConstructors() {
		return (Set<ConstructorDeclaration>) this.getMember(MethodDeclaration.class);
	}
	
	public Set<FieldDeclaration> getFields() {
		return (Set<FieldDeclaration>) this.getMember(MethodDeclaration.class);
	}
	
	private Set<?> getMember(Class<?> type) {
		Set<BodyDeclaration> members = new HashSet<BodyDeclaration>();
		for(BodyDeclaration decl: this.members) {
			if(decl.getClass().equals(type)) members.add(decl);
		}
		return members;
	}
	
	@Override
	public Set<Node> processTreeNode(TreeNode treeNode) throws Exception {
		return null;
	}

	@Override
	public void processLeafNode(LeafNode leafNode) throws Exception {
		
	}
}
