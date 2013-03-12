package ca.uwaterloo.joos.symboltable;

import java.util.List;

import org.hamcrest.Matchers;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.decl.VariableDeclaration;
import ca.uwaterloo.joos.ast.expr.name.SimpleName;
import ca.uwaterloo.joos.symboltable.SymbolTable.SymbolTableException;
import ch.lambdaj.Lambda;


public class BlockScope extends Scope {
	
	protected Scope parent;

	public BlockScope(String name, Scope parent, ASTNode referenceNode) {
		super(name, referenceNode);
		this.parent = parent;
	}
	
	public String nameForDecl(VariableDeclaration var) throws Exception {
		String name = this.getName() + "." + var.getName().getName();
		return name;
	}

	public void addVariableDecl(VariableDeclaration var) throws Exception {
		String name = this.nameForDecl(var);
		if(this.getLocalVariable(var.getName().getName()) != null) {
			throw new SymbolTableException("Duplicate Variable Declaration of " + name);
		}
		
		TableEntry entry = new TableEntry(name, var);

		this.symbols.put(name, entry);
	}

	public TableEntry getVariableDecl(VariableDeclaration var) throws Exception {
		return this.symbols.get(this.nameForDecl(var));
	}
	
	public TableEntry getLocalVariable(String name) {
		List<TableEntry> vars = Lambda.select(this.symbols.values(), Lambda.having(Lambda.on(TableEntry.class).getName(), Matchers.endsWith("." + name)));
		if(vars.size() == 1) {
			return vars.get(0);
		} else if(this.parent instanceof BlockScope) {
			return ((BlockScope) this.parent).getLocalVariable(name);
		}
		
		return null;
	}
	
	@Override
	public String resolveSimpleNameType(SimpleName name) throws Exception {
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
