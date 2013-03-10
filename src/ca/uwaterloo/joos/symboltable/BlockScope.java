package ca.uwaterloo.joos.symboltable;

import ca.uwaterloo.joos.ast.decl.VariableDeclaration;


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
		TableEntry entry = new TableEntry(var);
		String name = this.nameForDecl(var);

		this.symbols.put(name, entry);
	}

	public TableEntry getVariableDecl(VariableDeclaration var) throws Exception {
		return this.symbols.get(this.nameForDecl(var));
	}

	@Override
	public void listSymbols(){
		System.out.println("\tParent:");
		System.out.println("\t\t" + this.parent);
		
		super.listSymbols();
	}
}
