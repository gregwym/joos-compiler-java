package ca.uwaterloo.joos.symboltable;

import ca.uwaterloo.joos.ast.decl.TypeDeclaration;

public class PackageScope extends Scope {

	public PackageScope(String name) {
		super(name);
	}

	public void addType(TypeDeclaration type){
		TableEntry te = new TableEntry(type);
		symbols.put(this.name + "." + type.getIdentifier(), te);
	}
	
	public TableEntry getType(String key){
		return symbols.get(key);
	}
}
