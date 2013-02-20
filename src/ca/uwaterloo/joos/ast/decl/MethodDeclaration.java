package ca.uwaterloo.joos.ast.decl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import ca.uwaterloo.joos.ast.AST.ASTConstructException;
import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.body.Block;
import ca.uwaterloo.joos.ast.type.Modifiers;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;
import ca.uwaterloo.joos.parser.ParseTreeTraverse;
import ca.uwaterloo.joos.parser.ParseTreeTraverse.Traverser;


public class MethodDeclaration extends BodyDeclaration {

//	protected Parameters parameters;
	protected Block body;
	
	/**
	 * @return the body
	 */
	public Block getBody() {
		return body;
	}

	public MethodDeclaration(Node declNode, ASTNode parent) throws ASTConstructException {
		super(parent);
		assert declNode instanceof TreeNode : "MethodDeclaration is expecting a TreeNode";
				
		ParseTreeTraverse traverse = new ParseTreeTraverse(new Traverser(this) {

			public Set<Node> processTreeNode(TreeNode treeNode) throws ASTConstructException {
				Set<Node> offers = new HashSet<Node>();
				if(treeNode.productionRule.getLefthand().equals("classbodydecl")) {
//					returnType = new Type("VOID");
				}
				if(treeNode.productionRule.getLefthand().equals("modifiers")) {
					modifiers = new Modifiers(treeNode, parent);
				}
				else if(treeNode.productionRule.getLefthand().equals("type")) {
//					returnType = new Type();
				}
				else if(treeNode.productionRule.getLefthand().equals("params")) {
//					parameters = new Parameters(treeNode);
				}
				else if(treeNode.productionRule.getLefthand().equals("methodbody")) {
					if(Arrays.asList(treeNode.productionRule.getRighthand()).contains("block"))
						body = new Block(treeNode, parent);
				}
				else {
					for (Node n : treeNode.children)
						offers.add(n);
				}
				return offers;
			}

			public void processLeafNode(LeafNode leafNode) throws ASTConstructException {
				if(leafNode.token.getKind().equals("ID")) {
					setIdentifier(leafNode.token.getLexeme());
				}
				if(leafNode.token.getKind().equals("VOID")) {
//					returnType = new Type("VOID");
				}
			}
		    
		});

		traverse.traverse(declNode);
	}
}
