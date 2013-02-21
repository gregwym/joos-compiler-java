package ca.uwaterloo.joos.ast.decl;

import java.util.ArrayList;
import java.util.List;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.body.Block;
import ca.uwaterloo.joos.ast.descriptor.ChildDescriptor;
import ca.uwaterloo.joos.ast.descriptor.ChildListDescriptor;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

public class MethodDeclaration extends BodyDeclaration {

	protected static final ChildListDescriptor PARAMETERS = new ChildListDescriptor(ParameterDeclaration.class);
	protected static final ChildDescriptor BODY = new ChildDescriptor(Block.class);

	public MethodDeclaration(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}

	@SuppressWarnings("unchecked")
	public List<ParameterDeclaration> getParameters() throws ChildTypeUnmatchException {
		return (List<ParameterDeclaration>) this.getChildByDescriptor(MethodDeclaration.PARAMETERS);
	}

	public Block getBody() throws ChildTypeUnmatchException {
		return (Block) this.getChildByDescriptor(MethodDeclaration.BODY);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ca.uwaterloo.joos.parser.ParseTreeTraverse.Traverser#processTreeNode(
	 * ca.uwaterloo.joos.parser.ParseTree.TreeNode)
	 */
	@Override
	public List<Node> processTreeNode(TreeNode treeNode) throws Exception {
		if (treeNode.productionRule.getLefthand().equals("param")) {
			List<ParameterDeclaration> parameters = this.getParameters();
			if (parameters == null) {
				parameters = new ArrayList<ParameterDeclaration>();
				this.addChild(MethodDeclaration.PARAMETERS, parameters);
			}
			ParameterDeclaration parameter = new ParameterDeclaration(treeNode, this);
			parameters.add(parameter);
		} else if (treeNode.productionRule.getLefthand().equals("block")) {
			Block body = new Block(treeNode, this);
			addChild(BODY, body);
		} else {
			return super.processTreeNode(treeNode);
		}
		return null;
	}
}
