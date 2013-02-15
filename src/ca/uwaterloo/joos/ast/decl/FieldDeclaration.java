package ca.uwaterloo.joos.ast.decl;

import java.util.HashSet;
import java.util.Set;

import ca.uwaterloo.joos.ast.AST.ASTConstructException;
import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.ASTVisitor;
import ca.uwaterloo.joos.ast.ParseTreeTraverse;
import ca.uwaterloo.joos.ast.ParseTreeTraverse.Traverser;
import ca.uwaterloo.joos.ast.type.Modifiers;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;


public class FieldDeclaration extends BodyDeclaration {
	
//	protected Expression initValue;
	
	public FieldDeclaration(Node declNode, ASTNode parent) throws ASTConstructException {
		super(parent);
		assert declNode instanceof TreeNode : "FieldDeclaration is expecting a TreeNode";
				
		ParseTreeTraverse traverse = new ParseTreeTraverse(new Traverser(this) {

			public Set<Node> processTreeNode(TreeNode treeNode) throws ASTConstructException {
				Set<Node> offers = new HashSet<Node>();
				if(treeNode.productionRule.getLefthand().equals("modifiers")) {
					modifiers = new Modifiers(treeNode, parent);
				}
				else if(treeNode.productionRule.getLefthand().equals("type")) {
//					returnType = new Type();
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

	/* (non-Javadoc)
	 * @see ca.uwaterloo.joos.ast.ASTNode#accept(ca.uwaterloo.joos.ast.ASTVisitor)
	 */
	@Override
	public void accept(ASTVisitor visitor) {
		visitor.willVisit(this);
		if(visitor.visit(this)) {
			super.accept(visitor);
//			this.initValue.accept(visitor);
		}
		visitor.didVisit(this);
	}
}
