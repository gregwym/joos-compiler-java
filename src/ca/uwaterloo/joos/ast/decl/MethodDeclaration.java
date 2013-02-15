package ca.uwaterloo.joos.ast.decl;

import java.util.HashSet;
import java.util.Set;

import ca.uwaterloo.joos.ast.AST.ASTConstructException;
import ca.uwaterloo.joos.ast.ParseTreeTraverse;
import ca.uwaterloo.joos.ast.ParseTreeTraverse.Traverser;
import ca.uwaterloo.joos.ast.type.Modifiers;
import ca.uwaterloo.joos.ast.type.Type;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;


public class MethodDeclaration extends BodyDeclaration {
	
	protected Modifiers modifiers;
	protected Type returnType;
//	protected Parameters parameters;
//	protected Block body;
	
	public MethodDeclaration(Node declNode) throws ASTConstructException {
		assert declNode instanceof TreeNode : "Modifiers is expecting a TreeNode";
				
		ParseTreeTraverse traverse = new ParseTreeTraverse(new Traverser() {

			public Set<Node> processTreeNode(TreeNode treeNode) throws ASTConstructException {
				Set<Node> offers = new HashSet<Node>();
				if(treeNode.productionRule.getLefthand().equals("classbodydecl")) {
//					returnType = new Type("VOID");
				}
				if(treeNode.productionRule.getLefthand().equals("modifiers")) {
					modifiers = new Modifiers(treeNode);
				}
				else if(treeNode.productionRule.getLefthand().equals("type")) {
//					returnType = new Type();
				}
				else if(treeNode.productionRule.getLefthand().equals("params")) {
//					parameters = new Parameters(treeNode);
				}
				else if(treeNode.productionRule.getLefthand().equals("methodbody")) {
//					body = new Block(treeNode);
				}
				else {
					for (Node n : treeNode.children)
						offers.add(n);
				}
				return offers;
			}

			public void processLeafNode(LeafNode leafNode) throws ASTConstructException {
				if(leafNode.token.getKind().equals("ID")) {
					identifier = leafNode.token.getLexeme();
				}
				if(leafNode.token.getKind().equals("VOID")) {
//					returnType = new Type("VOID");
				}
			}
		    
		});

		traverse.traverse(declNode);
	}

	@Override
	public String toString(int level) {
		String str = super.toString(level);
		str += this.modifiers.toString(level + 1);
		return str;
	}
}
