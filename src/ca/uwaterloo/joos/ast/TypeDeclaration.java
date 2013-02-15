package ca.uwaterloo.joos.ast;

import java.util.List;

import ca.uwaterloo.joos.ast.body.Body;
import ca.uwaterloo.joos.ast.type.Modifiers;


public abstract class TypeDeclaration extends ASTNode {
	protected Modifiers modifiers;
	protected Body body;
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
	public Body getBody() {
		return body;
	}
}
