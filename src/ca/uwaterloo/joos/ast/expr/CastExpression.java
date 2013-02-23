package ca.uwaterloo.joos.ast.expr;

import java.util.List;

import ca.uwaterloo.joos.ast.AST.ASTConstructException;
import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.descriptor.ChildDescriptor;
import ca.uwaterloo.joos.ast.descriptor.SimpleDescriptor;
import ca.uwaterloo.joos.ast.expr.name.Name;
import ca.uwaterloo.joos.ast.type.PrimitiveType;
import ca.uwaterloo.joos.parser.ParseTree.LeafNode;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTree.TreeNode;

public class CastExpression extends Expression {

	public static final ChildDescriptor PRIMITIVE = new ChildDescriptor(PrimitiveType.class);
	public static final ChildDescriptor NAME = new ChildDescriptor(Name.class);
	public static final SimpleDescriptor IS_ARRAY = new SimpleDescriptor(Boolean.class);
	public static final ChildDescriptor VALUE = new ChildDescriptor(Expression.class);

	public CastExpression(Node node, ASTNode parent) throws Exception {
		super(node, parent);
	}

	public PrimitiveType getPrimitive() throws ChildTypeUnmatchException {
		return (PrimitiveType) this.getChildByDescriptor(PRIMITIVE);
	}

	public Name getName() throws ChildTypeUnmatchException {
		return (Name) this.getChildByDescriptor(NAME);
	}

	public Boolean getIsArray() throws ChildTypeUnmatchException {
		return (Boolean) this.getChildByDescriptor(IS_ARRAY);
	}

	public Expression getValue() throws ChildTypeUnmatchException {
		return (Expression) this.getChildByDescriptor(VALUE);
	}

	@Override
	public List<Node> processTreeNode(TreeNode treeNode) throws Exception {
		String kind = treeNode.getKind();

		if (kind.equals("expr")) {
			Expression expr = Expression.newExpression(treeNode, this);
			if (expr instanceof Name) {
				Name name = (Name) expr;
				this.addChild(NAME, name);
			} else {
				throw new ASTConstructException("Can not cast to Expression other than a Name, but got " + expr);
			}
		} else if (kind.equals("name")) {
			Name name = Name.newName(treeNode, this);
			this.addChild(NAME, name);
		} else if (kind.equals("primitivetype")) {
			PrimitiveType type = new PrimitiveType(treeNode, this);
			this.addChild(PRIMITIVE, type);
		} else if (kind.contains("unaryexpr")) {
			Expression value = Expression.newExpression(treeNode, this);
			this.addChild(VALUE, value);
		} else {
			return super.processTreeNode(treeNode);
		}
		return null;
	}

	@Override
	public void processLeafNode(LeafNode leafNode) throws Exception {
		if (leafNode.getKind().equals("RBRACK")) {
			this.addChild(IS_ARRAY, true);
		}
	}
}
