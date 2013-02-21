package ca.uwaterloo.joos.ast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import ca.uwaterloo.joos.Main;
import ca.uwaterloo.joos.ast.AST.ASTConstructException;
import ca.uwaterloo.joos.ast.descriptor.ChildDescriptor;
import ca.uwaterloo.joos.ast.descriptor.ChildListDescriptor;
import ca.uwaterloo.joos.ast.descriptor.Descriptor;
import ca.uwaterloo.joos.ast.descriptor.SimpleDescriptor;
import ca.uwaterloo.joos.ast.descriptor.SimpleListDescriptor;
import ca.uwaterloo.joos.ast.visitor.ASTVisitor;
import ca.uwaterloo.joos.parser.ParseTree.Node;
import ca.uwaterloo.joos.parser.ParseTreeTraverse;
import ca.uwaterloo.joos.parser.ParseTreeTraverse.Traverser;

public abstract class ASTNode implements Traverser {
	protected static final Logger logger = Main.getLogger(ASTNode.class);

	@SuppressWarnings("serial")
	public static class ChildTypeUnmatchException extends ASTConstructException {
		public ChildTypeUnmatchException(String msg) {
			super(msg);
		}
	}

	private ASTNode parent = null;
	private String identifier = new String();
	private Map<Descriptor, Object> childrenList = new HashMap<Descriptor, Object>();

	public ASTNode(Node node, ASTNode parent) throws Exception {
		this.parent = parent;
		
		ParseTreeTraverse traverse = new ParseTreeTraverse(this);
		traverse.traverse(node);
	}

	/**
	 * @return the parent
	 */
	public ASTNode getParent() {
		return parent;
	}

	/**
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}
	
	protected void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	protected void addChild(Descriptor descriptor, Object value) {
		this.childrenList.put(descriptor, value);
	}

	public List<?> getChildByDescriptor(ChildListDescriptor listDescriptor) throws ChildTypeUnmatchException {
		Object child = childrenList.get(listDescriptor);
		if (child == null || child instanceof List) {
			return (List<?>) child;
		}
		throw new ChildTypeUnmatchException(listDescriptor + " is not mapping to a List");
	}

	public ASTNode getChildByDescriptor(ChildDescriptor childDescriptor) throws ChildTypeUnmatchException {
		Object child = childrenList.get(childDescriptor);
		if (childDescriptor.getElementClass().isAssignableFrom(child.getClass())) {
			return (ASTNode) child;
		}
		throw new ChildTypeUnmatchException(childDescriptor + " is not mapping to a " + childDescriptor.getElementClass().getSimpleName());
	}
	
	public List<?> getChildByDescriptor(SimpleListDescriptor listDescriptor) throws ChildTypeUnmatchException {
		Object child = childrenList.get(listDescriptor);
		if (child == null || child instanceof List) {
			return (List<?>) child;
		}
		throw new ChildTypeUnmatchException(listDescriptor + " is not mapping to a List");
	}
	
	public Object getChildByDescriptor(SimpleDescriptor childDescriptor) throws ChildTypeUnmatchException {
		Object child = childrenList.get(childDescriptor);
		if (childDescriptor.getElementClass().isAssignableFrom(child.getClass())) {
			return (Object) child;
		}
		throw new ChildTypeUnmatchException(childDescriptor + " is not mapping to a " + childDescriptor.getElementClass().getSimpleName());
	}

	@SuppressWarnings("unchecked")
	public final void accept(ASTVisitor visitor) throws Exception {
		visitor.willVisit(this);
		logger.finest("Visiting <" + this.getClass().getSimpleName() + ">");
		if (visitor.visit(this)) {
			for (Descriptor key : this.childrenList.keySet()) {
				if (key instanceof ChildListDescriptor) {
					List<ASTNode> children = (List<ASTNode>) this.getChildByDescriptor((ChildListDescriptor) key);
					for (ASTNode child : children) {
						child.accept(visitor);
					}
				}
				else if(key instanceof ChildDescriptor) {
					this.getChildByDescriptor((ChildDescriptor) key).accept(visitor);
				}
			}
		}
		visitor.didVisit(this);
	}

	@Override
	public final String toString() {
		String str = "";
		str += "<" + this.getClass().getSimpleName() + ">";
		if(this.identifier.length() > 0) str += " " + this.identifier + " |";
		for (Descriptor key : this.childrenList.keySet()) {
			if( key instanceof SimpleDescriptor || key instanceof SimpleListDescriptor ) {
				Object child = this.childrenList.get(key);
				str += " " + key.getElementClass().getSimpleName() + ": " + child + " |";
			}
		}
		if (this.parent != null)
			str += " parent: " + this.parent.getClass().getSimpleName();
		return str;
	}
}
