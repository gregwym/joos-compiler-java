/**
 * 
 */
package ca.uwaterloo.joos.ast.body;

import java.util.ArrayList;
import java.util.List;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.decl.LocalVariableDeclaration;
import ca.uwaterloo.joos.ast.descriptor.ChildDescriptor;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

/**
 * @author Greg Wang
 *
 */
public class Block extends ASTNode {
	
	public static final ChildDescriptor LOCAL_VAR = new ChildDescriptor(LocalVariableDeclaration.class);
//	public static final ChildListDescriptor STATEMENTS = new ChildListDescriptor(Statement.class);

	/**
	 * @param parent
	 * @throws Exception 
	 */
	public Block(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}
	
	public LocalVariableDeclaration getLocalVariable() throws ChildTypeUnmatchException {
		return (LocalVariableDeclaration) this.getChildByDescriptor(LOCAL_VAR);
	}
	
//	@SuppressWarnings("unchecked")
//	public List<Statement> getStatements() throws ChildTypeUnmatchException {
//		return (List<Statement>) this.getChildByDescriptor(STATEMENTS);
//	}

	@Override
	public List<Node> processTreeNode(TreeNode treeNode) throws Exception {
		List<Node> offers = new ArrayList<Node>();
		if (treeNode.productionRule.getLefthand().equals("localvardecl")) {
			LocalVariableDeclaration decl = new LocalVariableDeclaration(treeNode, this);
			this.addChild(Block.LOCAL_VAR, decl);
		} else if (treeNode.productionRule.getLefthand().equals("stmnt")) {
			// TODO Implement Statement
//			List<Statement> statements = getStatements();
//			if (statements == null) {
//				statements = new ArrayList<Statement>();
//				addChild(Block.STATEMENTS, statements);
//			}
//			Statement statement = new Statement(treeNode, this);
//			statements.add(statement);
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
