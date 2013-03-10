package ca.uwaterloo.joos.symboltable;

import java.util.ArrayList;
import java.util.List;

import ca.uwaterloo.joos.ast.decl.TypeDeclaration;
import ca.uwaterloo.joos.ast.expr.name.SimpleName;
import ca.uwaterloo.joos.symboltable.SymbolTable.SymbolTableException;

public class PackageScope extends Scope {

	public PackageScope(String name) {
		super(name);
	}

	public void addType(TypeDeclaration type){
		String name = this.name + "." + type.getIdentifier();
		TableEntry entry = new TableEntry(name, type);
		symbols.put(name, entry);
	}
	
	public TableEntry getType(String key){
		return symbols.get(key);
	}

	@Override
	public String resolveSimpleNameType(SimpleName name) throws Exception {
		List<TableEntry> matches = new ArrayList<TableEntry>();
		matches = this.entriesWithSuffix(this.symbols.values(), "." + name.getName());
		
		if(matches.size() > 1) {
			throw new SymbolTableException("More than one match was found in Package Scope for type " + name);
		} else if(matches.size() == 1) {
			return matches.get(0).getName();
		}
		
		return null;
	}
}
