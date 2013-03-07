package ca.uwaterloo.joos.symboltable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import ca.uwaterloo.joos.Main;
import ca.uwaterloo.joos.ast.AST;
import ca.uwaterloo.joos.ast.expr.name.Name;
import ca.uwaterloo.joos.ast.expr.name.QualifiedName;
import ca.uwaterloo.joos.ast.type.ReferenceType;

public class SymbolTable {
	
	public static Logger logger = Main.getLogger(SymbolTable.class);
	
	private Map<String, Scope> scopes = new HashMap<String, Scope>();	// Links each Scope together

	public SymbolTable() {
		// TODO Auto-generated constructor stub
	}

	public Scope getScope(String name) {
		Scope scope = this.scopes.get(name);
		if(scope == null) {
			scope = new Scope(name);
		}
		this.scopes.put(name, scope);
		return scope;
	}
	
	public boolean containScope(String name) {
		return this.scopes.containsKey(name);
	}
	
	public String lookupReferenceType(ReferenceType type) throws Exception {
		Name name = type.getName();
		if(name instanceof QualifiedName && this.containScope(name.getName() + "{}")) {
			return name.getName();
		}
		return null;
	}
	
	public void build(List<AST> asts) throws Exception{
		logger.setLevel(Level.FINE);
		logger.fine("Building SymbolTable");
		//Called from main
		for (AST ast: asts){
			ast.getRoot().accept(new TopDeclVisitor(this));
		}
		
		logger.info("Pass 1 Finished");
		
		for (AST iast: asts){
			iast.getRoot().accept(new DeepDeclVisitor(this));
		}
	}

	public void listScopes() {
		System.out.println("Listing Scopes");
		List<String> keys = new ArrayList<String>(this.scopes.keySet());
		Collections.sort(keys);
		for (String key : keys){
			System.out.println(this.scopes.get(key).getName());
			this.scopes.get(key).listSymbols();
		}
	}
}
