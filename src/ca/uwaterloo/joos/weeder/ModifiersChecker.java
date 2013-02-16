/**
 *
 */
package ca.uwaterloo.joos.weeder;

import java.util.HashSet;
import java.util.Set;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.body.InterfaceBody;
import ca.uwaterloo.joos.ast.decl.ClassDeclaration;
import ca.uwaterloo.joos.ast.decl.FieldDeclaration;
import ca.uwaterloo.joos.ast.decl.InterfaceDeclaration;
import ca.uwaterloo.joos.ast.decl.MethodDeclaration;
import ca.uwaterloo.joos.ast.type.Modifiers;
import ca.uwaterloo.joos.ast.type.Modifiers.Modifier;
import ca.uwaterloo.joos.ast.visitor.ModifiersVisitor;
import ca.uwaterloo.joos.weeder.Weeder.WeedException;

/**
 * @author Greg Wang
 *
 */
public class ModifiersChecker extends ModifiersVisitor {

	/* (non-Javadoc)
	 * @see ca.uwaterloo.joos.ast.visitor.ModifiersVisitor#visitModifiers(ca.uwaterloo.joos.ast.type.Modifiers)
	 */
	@Override
	protected void visitModifiers(Modifiers modifiers) throws Exception {
		Set<Modifier> modifierSet = new HashSet<Modifier>();

		for(Modifier modifier: modifiers.getModifiers()) {
			if(modifierSet.contains(modifier)) throw new WeedException("Duplicated modifer" + modifier.name());
			modifierSet.add(modifier);
		}

		if(modifierSet.contains(Modifier.PUBLIC) && modifierSet.contains(Modifier.PROTECTED))
			throw new WeedException("Cannot be public and protected at the same time");
		if(!modifierSet.contains(Modifier.PUBLIC) && !modifierSet.contains(Modifier.PROTECTED))
			throw new WeedException("Must have one access modifier");

		ASTNode parent = modifiers.getParent();
		if(parent instanceof ClassDeclaration) {

			if(modifierSet.contains(Modifier.ABSTRACT) && modifierSet.contains(Modifier.FINAL))
				throw new WeedException("Class cannot be both abstract and final");

		}
		else if(parent instanceof InterfaceDeclaration) {
			if(modifierSet.contains(Modifier.ABSTRACT) && modifierSet.contains(Modifier.FINAL))
				throw new WeedException("Class cannot be both abstract and final");
		}
		else if(parent instanceof MethodDeclaration) {

			if(modifierSet.contains(Modifier.ABSTRACT))
				if(modifierSet.contains(Modifier.STATIC) || modifierSet.contains(Modifier.FINAL))
					throw new WeedException("Abstract method cannot be static or final");

			if(modifierSet.contains(Modifier.STATIC) && modifierSet.contains(Modifier.FINAL))
				throw new WeedException("Method cannot be both static and final");

			if(modifierSet.contains(Modifier.NATIVE) && !modifierSet.contains(Modifier.STATIC))
				throw new WeedException("Method must be native and static at the same time");

			if(modifierSet.contains(Modifier.ABSTRACT) || modifierSet.contains(Modifier.NATIVE)) {
				MethodDeclaration methodDecl = (MethodDeclaration) modifiers.getParent();
				if(methodDecl.getBody() != null) {
					throw new WeedException("Abstract or Native method cannot have body");
				}
			}

			if(parent.getParent() instanceof InterfaceBody) {

				if(modifierSet.contains(Modifier.STATIC) || modifierSet.contains(Modifier.FINAL) || modifierSet.contains(Modifier.NATIVE))
					throw new WeedException("Interface method cannot be static, final, or native");

			}
		}
		else if(parent instanceof FieldDeclaration) {
			if(modifierSet.contains(Modifier.FINAL))
				throw new WeedException("Field cannot be final");
		}


	}

	@Override
	public void willVisit(ASTNode node) {

	}

	@Override
	public void didVisit(ASTNode node) {

	}

}
