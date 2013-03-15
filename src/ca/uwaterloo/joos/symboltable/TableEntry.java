package ca.uwaterloo.joos.symboltable;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.type.Type;

public class TableEntry {
	// An entry in the symbols hash map

	private String name;
	private ASTNode node;
	private Scope withinScope;
	
	private Type type;
	private TypeScope typeScope;

	public TableEntry(String name, ASTNode inode, Scope withinScope) {
		this.name = name;
		this.node = inode;
		this.withinScope = withinScope;
	}

	public ASTNode getNode() {
		return node;
	}

	public String getName() {
		return name;
	}
	
	public Scope getWithinScope() {
		return withinScope;
	}
	
	public Type getType() {
		return type;
	}

	public TypeScope getTypeScope() {
		return typeScope;
	}
	
	public void setType(Type type) {
		this.type = type;
	}

	public void setTypeScope(TypeScope typeScope) {
		this.typeScope = typeScope;
	}

	@Override
	public String toString() {
		String str = "<" + this.getClass().getSimpleName() + "> ";
		if(this.type != null) {
			try {
				str += "[" + this.type.getFullyQualifiedName() + "]";
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else str += "[VOID]"; 
		str += name;
		if(this.typeScope != null) str += " | " + typeScope;
		return str;
	}
}
