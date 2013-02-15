package ca.uwaterloo.joos.ast.decl;

import java.util.List;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.body.TypeBody;
import ca.uwaterloo.joos.ast.type.Modifiers;


public abstract class TypeDeclaration extends ASTNode {
	protected Modifiers modifiers;
	protected TypeBody body;
	protected List<String> interfaces;
	
	/**
	 * @return the modifiers
	 */
	public Modifiers getModifiers() {
		return modifiers;
	}

	/**
	 * @return the interfaces
	 */
	public List<String> getInterfaces() {
		return interfaces;
	}

	/**
	 * @return the classBody
	 */
	public TypeBody getBody() {
		return body;
	}
}
