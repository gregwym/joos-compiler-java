package ca.uwaterloo.joos.symboltable;

import ca.uwaterloo.joos.ast.decl.VariableDeclaration;
import ca.uwaterloo.joos.ast.expr.name.SimpleName;


public class BlockScope extends Scope {
	
	protected Scope parent;

	public BlockScope(String name, Scope parent) {
		super(name);
		this.parent = parent;
	}
	
	public String nameForDecl(VariableDeclaration var) throws Exception {
		String name = this.getName() + "." + var.getName().getName();
		return name;
	}

	public void addVariableDecl(VariableDeclaration var) throws Exception {
		String name = this.nameForDecl(var);
		TableEntry entry = new TableEntry(name, var);

		this.symbols.put(name, entry);
	}

	public TableEntry getVariableDecl(VariableDeclaration var) throws Exception {
		return this.symbols.get(this.nameForDecl(var));
	}
	
	@Override
	public boolean resolveSimpleNameType(SimpleName name) throws Exception {
//		List<TableEntry> matches = new ArrayList<TableEntry>();
//		matches = this.entriesWithSuffix(this.symbols.values(), name.getName());
//		
//		if(matches.size() > 1) {
//			throw new SymbolTableException("More than one match was found in Block Scope for type " + name);
//		} else if(matches.size() == 1) {
//			return true;
//		}
		
		return this.parent.resolveSimpleNameType(name);
	}

	@Override
	public void listSymbols(){
		System.out.println("\tParent:");
		System.out.println("\t\t" + this.parent);
		
		super.listSymbols();
	}
}
