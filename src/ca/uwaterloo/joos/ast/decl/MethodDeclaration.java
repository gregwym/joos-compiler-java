package ca.uwaterloo.joos.ast.decl;

import java.util.List;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.body.Block;
import ca.uwaterloo.joos.ast.descriptor.ChildDescriptor;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;


public class MethodDeclaration extends BodyDeclaration {
	
//	protected static final ChildListDescriptor PARAMETERS = new ChildDescriptor(Parameters.class);
	protected static final ChildDescriptor BODY = new ChildDescriptor(Block.class);

	public MethodDeclaration(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}
	
//	public List<Parameters> getParameters() throws ChildTypeUnmatchException {
//		return (List<Parameters>) this.getChildByDescriptor(MethodDeclaration.PARAMETERS);
//	}
	
	public Block getBody() throws ChildTypeUnmatchException {
		return (Block) this.getChildByDescriptor(MethodDeclaration.BODY);
	}

	/* (non-Javadoc)
	 * @see ca.uwaterloo.joos.parser.ParseTreeTraverse.Traverser#processTreeNode(ca.uwaterloo.joos.parser.ParseTree.TreeNode)
	 */
	@Override
	public List<Node> processTreeNode(TreeNode treeNode) throws Exception {
        if(treeNode.productionRule.getLefthand().equals("param")) {
//              returnType = new Type();
        }
        else if(treeNode.productionRule.getLefthand().equals("block")) {
                Block body = new Block(treeNode, this);
                addChild(BODY, body);
        }
        else {
             return super.processTreeNode(treeNode);
        }
        return null;
	}
}
