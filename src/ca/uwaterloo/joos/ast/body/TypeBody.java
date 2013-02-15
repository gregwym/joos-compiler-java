/**
 *
 */
package ca.uwaterloo.joos.ast.body;

import java.util.HashSet;
import java.util.Set;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.ASTVisitor;
import ca.uwaterloo.joos.ast.decl.BodyDeclaration;
import ca.uwaterloo.joos.ast.decl.ConstructorDeclaration;
import ca.uwaterloo.joos.ast.decl.FieldDeclaration;
import ca.uwaterloo.joos.ast.decl.MethodDeclaration;

/**
 * @author Greg Wang
 *
 */
@SuppressWarnings("unchecked")
public abstract class TypeBody extends ASTNode {
	protected Set<BodyDeclaration> members;
	/**
	 *
	 */
	public TypeBody() {
		this.members = new HashSet<BodyDeclaration>();
	}

	public Set<MethodDeclaration> getMethods() {
		return (Set<MethodDeclaration>) this.getMember(MethodDeclaration.class);
	}
	
	public Set<ConstructorDeclaration> getConstructors() {
		return (Set<ConstructorDeclaration>) this.getMember(MethodDeclaration.class);
	}
	
	public Set<FieldDeclaration> getFields() {
		return (Set<FieldDeclaration>) this.getMember(MethodDeclaration.class);
	}
	
	private Set<?> getMember(Class<?> type) {
		Set<BodyDeclaration> members = new HashSet<BodyDeclaration>();
		for(BodyDeclaration decl: this.members) {
			if(decl.getClass().equals(type)) members.add(decl);
		}
		return members;
	}
	
	@Override
	public String toString(int level) {
		String str = super.toString(level);
		str += "\n";
		for(BodyDeclaration member: this.members) 
			str += member.toString(level + 1);
		return str;
	}
	
	/* (non-Javadoc)
	 * @see ca.uwaterloo.joos.ast.ASTNode#accept(ca.uwaterloo.joos.ast.ASTVisitor)
	 */
	@Override
	public void accept(ASTVisitor visitor) {
		visitor.willVisit(this);
		if(visitor.visit(this)) {
			for(BodyDeclaration member: this.members)
				member.accept(visitor);
		}
		visitor.didVisit(this);
	}
}
