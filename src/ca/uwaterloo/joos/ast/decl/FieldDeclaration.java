package ca.uwaterloo.joos.ast.decl;

import java.util.HashSet;
import java.util.Set;

import ca.uwaterloo.joos.ast.ParseTreeTraverse;
import ca.uwaterloo.joos.ast.AST.ASTConstructException;
import ca.uwaterloo.joos.ast.ParseTreeTraverse.Traverser;
import ca.uwaterloo.joos.ast.type.Modifiers;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;


public class FieldDeclaration extends BodyDeclaration {
	
//	protected Expression initValue;
	
	public FieldDeclaration(Node declNode) throws ASTConstructException {
		assert declNode instanceof TreeNode : "FieldDeclaration is expecting a TreeNode";
				
		ParseTreeTraverse traverse = new ParseTreeTraverse(new Traverser() {

			public Set<Node> processTreeNode(TreeNode treeNode) throws ASTConstructException {
				Set<Node> offers = new HashSet<Node>();
				if(treeNode.productionRule.getLefthand().equals("modifiers")) {
					modifiers = new Modifiers(treeNode);
				}
				else if(treeNode.productionRule.getLefthand().equals("type")) {
//					returnType = new Type();
				}
				else if(treeNode.productionRule.getLefthand().equals("params")) {
//					parameters = new Parameters(treeNode);
				}
				else if(treeNode.productionRule.getLefthand().equals("expr")) {
//					initValue = new Expression(treeNode);
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
			}
		});

		traverse.traverse(declNode);
	}
}
