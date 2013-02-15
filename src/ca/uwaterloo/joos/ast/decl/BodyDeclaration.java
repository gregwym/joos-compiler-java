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
import ca.uwaterloo.joos.ast.type.Modifiers;
import ca.uwaterloo.joos.ast.type.Type;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

/**
 * @author Greg Wang
 *
 */
public abstract class BodyDeclaration extends ASTNode {
	
	protected Modifiers modifiers;
	protected Type type;

	/**
	 * 
	 */
	public BodyDeclaration() {}
	
	private static BodyDeclaration newBodyDecl = null;
	public static BodyDeclaration newBodyDeclaration(Node declNode) throws ASTConstructException {
		assert declNode instanceof TreeNode : "BodyDecl is expecting a TreeNode";
	
		ParseTreeTraverse traverse = new ParseTreeTraverse(new Traverser() {
	
			public Set<Node> processTreeNode(TreeNode treeNode) throws ASTConstructException {
				Set<Node> offers = new HashSet<Node>();
				if(treeNode.productionRule.getLefthand().equals("constructordecl")){
					newBodyDecl = new ConstructorDeclaration(treeNode);
				}
				else if(treeNode.productionRule.getLefthand().equals("absmethoddecl")){
					newBodyDecl = new MethodDeclaration(treeNode);
				}
				else if(treeNode.productionRule.getLefthand().equals("methoddecl")){
					newBodyDecl = new MethodDeclaration(treeNode);
				}
				else if(treeNode.productionRule.getLefthand().equals("fielddecl")){
					newBodyDecl = new FieldDeclaration(treeNode);
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
		str += "type: " + this.type + "\n";
		str += this.modifiers.toString(level + 1);
		return str;
	}
}
