/**
 * 
 */
package ca.uwaterloo.joos.ast.decl;

import java.util.HashSet;
import java.util.Set;

import ca.uwaterloo.joos.ast.AST.ASTConstructException;
import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.ParseTreeTraverse;
import ca.uwaterloo.joos.ast.ParseTreeTraverse.Traverser;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

/**
 * @author Greg Wang
 *
 */
public abstract class BodyDeclaration extends ASTNode {

	/**
	 * 
	 */
	public BodyDeclaration() {
		// TODO Auto-generated constructor stub
	}
	
	private static BodyDeclaration newBodyDecl = null;
	public static BodyDeclaration newBodyDeclaration(Node declNode) throws ASTConstructException {
		assert declNode instanceof TreeNode : "BodyDecl is expecting a TreeNode";
	
		ParseTreeTraverse traverse = new ParseTreeTraverse(new Traverser() {
	
			public Set<Node> processTreeNode(TreeNode treeNode) {
				Set<Node> offers = new HashSet<Node>();
				if(treeNode.productionRule.getLefthand().equals("constructordecl")){
					newBodyDecl = new ConstructorDeclaration();
				}
				else if(treeNode.productionRule.getLefthand().equals("methoddecl")){
					newBodyDecl = new MethodDeclaration();
				}
				else if(treeNode.productionRule.getLefthand().equals("fielddecl")){
					newBodyDecl = new FieldDeclaration();
				}
				else {
					for (Node n : treeNode.children) {
						offers.add(n);
					}
				}
				return offers;
			}
	
			public void processLeafNode(LeafNode leafNode) throws ASTConstructException {}
		    
		});
	
		traverse.traverse(declNode);
		
		BodyDeclaration rtn = newBodyDecl;
		newBodyDecl = null;
		
		return rtn;
	}

	@Override
	public String toString(int level) {
		String str = super.toString(level);
		str += "<" + this.getClass().getSimpleName() + "> \n";
		return str;
	}
}
