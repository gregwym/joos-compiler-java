package ca.uwaterloo.joos.ast;

import ca.uwaterloo.joos.parser.LR1.ProductionRule;
import ca.uwaterloo.joos.parser.ParseTree;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

public class AST {
	
	@SuppressWarnings("serial")
	public static class ASTConstructException extends Exception {
		public ASTConstructException(String string) {
			super(string);
		}
	}

	private ASTNode root;

	public ASTNode getRoot() {
		return this.root;
	}

	public AST(ParseTree parseTree, String fileName) throws Exception {
		assert parseTree != null : "Null parse tree";

		ParseTree.TreeNode parseTreeRoot = (TreeNode) parseTree.root;
		assert parseTreeRoot instanceof ParseTree.TreeNode : "Parse tree root is not a TreeNode";

		ProductionRule productionRule = parseTreeRoot.productionRule;
		assert productionRule.getLefthand().equals("S") : "Parse tree does not start with symbol S";

		// Construct FileUnit with the `file` in `S -> BOF file EOF`
		this.root = new FileUnit(parseTreeRoot.children.get(1), fileName, null);
	}

	@Override
	public String toString() {
		return this.root.toString();
	}
}
