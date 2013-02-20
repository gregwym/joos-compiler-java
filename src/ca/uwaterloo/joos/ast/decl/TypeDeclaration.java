package ca.uwaterloo.joos.ast.decl;

import java.util.ArrayList;
import java.util.List;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.ChildDescriptor;
import ca.uwaterloo.joos.ast.Descriptor;
import ca.uwaterloo.joos.ast.body.TypeBody;
import ca.uwaterloo.joos.ast.type.Modifiers;
import ca.uwaterloo.joos.ast.visitor.ASTVisitor;


public abstract class TypeDeclaration extends ASTNode {
	/*protected Modifiers modifiers;
	protected TypeBody body;
	protected List<String> interfaces;*/
	
	public TypeDeclaration(ASTNode parent) {
		super(parent);
	
	}

	

	public Object getChildrenByDescriptor(Descriptor childDescriptor) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	/* (non-Javadoc)
	 * @see ca.uwaterloo.joos.ast.ASTNode#accept(ca.uwaterloo.joos.ast.ASTVisitor)
	 */
	//@Override
	/*public void accept(ASTVisitor visitor) throws Exception{
		this.modifiers.accept(visitor);
//		for(InterfaceType interfaceType: this.interfaces) {
//			interfaceType.accept(visitor);
//		}
		if(this.body != null) this.body.accept(visitor);
	}*/
}
