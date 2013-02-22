package ca.uwaterloo.joos.ast;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.List;
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
	
	private static class ASTChild {
		public Descriptor descriptor;
		public Object value;

		public Descriptor getDescriptor() {
			return descriptor;
		}

		public Object getValue() {
			return value;
		}

		public ASTChild(Descriptor descriptor, Object value) {
			this.descriptor = descriptor;
			this.value = value;
		}
	}

	private ASTNode parent = null;
	private String identifier = new String();
	private List<ASTChild> childrenList = new ArrayList<ASTChild>();

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
	
	protected void addChild(Descriptor descriptor, Object child) {
		this.childrenList.add(new ASTChild(descriptor, child));
	}
	
	private List<?> getChildrenByRawDescriptor(Descriptor descriptor) throws ChildTypeUnmatchException {
		List<Object> children = extract(select(this.childrenList, 
				having(on(ASTChild.class).getDescriptor(), is(descriptor))), 
				on(ASTChild.class).getValue());
		return children;
	}

	public List<?> getChildByDescriptor(ChildListDescriptor listDescriptor) throws ChildTypeUnmatchException {
		return this.getChildrenByRawDescriptor(listDescriptor);
	}

	public ASTNode getChildByDescriptor(ChildDescriptor childDescriptor) throws ChildTypeUnmatchException {
		List<?> children = this.getChildrenByRawDescriptor(childDescriptor);
		if(children.size() == 0) {
			return null;
		} else if(children.size() > 1) {
			throw new ChildTypeUnmatchException(childDescriptor + " is mapping to more than one item");
		}
		ASTNode child = (ASTNode) children.get(0);
		if (childDescriptor.getElementClass().isAssignableFrom(child.getClass())) {
			return (ASTNode) child;
		}
		throw new ChildTypeUnmatchException(childDescriptor + " is not mapping to a " + childDescriptor.getElementClass().getSimpleName());
	}
	
	public List<?> getChildByDescriptor(SimpleListDescriptor listDescriptor) throws ChildTypeUnmatchException {
		return this.getChildrenByRawDescriptor(listDescriptor);
	}
	
	public Object getChildByDescriptor(SimpleDescriptor childDescriptor) throws ChildTypeUnmatchException {
		List<?> children = this.getChildrenByRawDescriptor(childDescriptor);
		if(children.size() == 0) {
			return null;
		} else if(children.size() > 1) {
			throw new ChildTypeUnmatchException(childDescriptor + " is mapping to more than one item");
		}
		Object child = children.get(0);
		if (childDescriptor.getElementClass().isAssignableFrom(child.getClass())) {
			return child;
		}
		throw new ChildTypeUnmatchException(childDescriptor + " is not mapping to a " + childDescriptor.getElementClass().getSimpleName());
	}

	public final void accept(ASTVisitor visitor) throws Exception {
		visitor.willVisit(this);
		logger.finest("Visiting <" + this.getClass().getSimpleName() + ">");
		if (visitor.visit(this)) {
			for (ASTChild child : this.childrenList) {
				Descriptor descriptor = child.getDescriptor();
				if (descriptor instanceof ChildListDescriptor || 
						descriptor instanceof ChildDescriptor) {
					ASTNode node = (ASTNode) child.getValue();
					node.accept(visitor);
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
		for (ASTChild child : this.childrenList) {
			Descriptor descriptor = child.getDescriptor();
			if( descriptor instanceof SimpleDescriptor || 
					descriptor instanceof SimpleListDescriptor ) {
				str += " " + descriptor.getElementClass().getSimpleName() + ": " + child.getValue() + " |";
			}
		}
		if (this.parent != null)
			str += " parent: " + this.parent.getClass().getSimpleName();
		return str;
	}
}
