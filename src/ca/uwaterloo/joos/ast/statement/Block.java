/**
 *
 */
package ca.uwaterloo.joos.ast.statement;

import java.util.List;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.decl.LocalVariableDeclaration;
import ca.uwaterloo.joos.ast.descriptor.ChildListDescriptor;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

/**
 * @author Greg Wang
 *
 */
public class Block extends Statement {

	public static final ChildListDescriptor LOCAL_VAR = new ChildListDescriptor(LocalVariableDeclaration.class);
	public static final ChildListDescriptor STATEMENTS = new ChildListDescriptor(Statement.class);

	/**
	 * @param parent
	 * @throws Exception
	 */
	public Block(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}

	@SuppressWarnings("unchecked")
	public List<LocalVariableDeclaration> getLocalVariable() throws ChildTypeUnmatchException {
		return (List<LocalVariableDeclaration>) this.getChildByDescriptor(LOCAL_VAR);
	}

	@SuppressWarnings("unchecked")
	public List<Statement> getStatements() throws ChildTypeUnmatchException {
		return (List<Statement>) this.getChildByDescriptor(STATEMENTS);
	}

	@Override
	public List<Node> processTreeNode(TreeNode treeNode) throws Exception {
		if (treeNode.getKind().equals("localvardecl")) {
			LocalVariableDeclaration decl = new LocalVariableDeclaration(treeNode, this);
			this.addChild(Block.LOCAL_VAR, decl);
		} else if (treeNode.getKind().equals("stmnt")) {
			Statement statement = Statement.newStatement(treeNode, this);
			this.addChild(Block.STATEMENTS, statement);
		} else {
			return super.processTreeNode(treeNode);
		}
		return null;
	}

}
