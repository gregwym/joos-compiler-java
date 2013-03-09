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

public class SymbolTable {

	@SuppressWarnings("serial")
	public static class SymbolTableException extends Exception {
		public SymbolTableException(String string) {
			super(string);
		}
	}

	public static Logger logger = Main.getLogger(SymbolTable.class);

	private Map<String, Scope> scopes = new HashMap<String, Scope>();

	public SymbolTable() {
		this.scopes = new HashMap<String, Scope>();
	}

	public PackageScope getPackage(String name) throws Exception {
		Scope scope = this.scopes.get(name);
		if (scope == null) {
			scope = this.addPackage(name);
		} else if (!(scope instanceof PackageScope)) {
			throw new SymbolTableException("Expecting PackageScope but get " + scope.getClass().getName());
		}
		return (PackageScope) scope;
	}

	public TypeScope getType(String name) throws Exception {
		Scope scope = this.scopes.get(name + "{}");
		if (scope == null) {
			scope = this.addType(name);
		} else if (!(scope instanceof TypeScope)) {
			throw new SymbolTableException("Expecting TypeScope but get " + scope.getClass().getName());
		}
		return (TypeScope) scope;
	}

	public boolean containPackage(String name) {
		Scope scope = this.scopes.get(name);
		return scope != null && scope instanceof PackageScope;
	}

	public boolean containType(String name) {
		Scope scope = this.scopes.get(name);
		return scope != null && scope instanceof TypeScope;
	}

//	public String lookupReferenceType(ReferenceType type) throws Exception {
		// Name name = type.getName();
		// if(name instanceof QualifiedName && this.containScope(name.getName()
		// + "{}")) {
		// return name.getName();
		// }
//		return null;
//	}

	public PackageScope addPackage(String packageName) throws Exception {
		int i = 0;
		String[] components = packageName.split("\\.");
		String name;

		// Check for prefix conflict
		for (i = 0, name = ""; i < components.length; i++) {
			if (i != 0)
				name += ".";
			name += components[i];
			if (this.containType(name)) {
				throw new SymbolTableException("Package declaration conflict with type prefix " + name);
			}
		}

		PackageScope scope = new PackageScope(packageName);
		this.scopes.put(packageName, scope);
		return scope;
	}

	public TypeScope addType(String typeName) throws Exception {
		// Check for prefix conflict
		if (this.containPackage(typeName)) {
			throw new SymbolTableException("Type declaration conflict with package prefix " + typeName);
		}
		typeName += "{}";

		TypeScope scope = new TypeScope(typeName);
		this.scopes.put(typeName, scope);
		return scope;
	}

	public void build(List<AST> asts) throws Exception {
		logger.setLevel(Level.FINE);

		for (AST ast : asts) {
			ast.getRoot().accept(new TopDeclVisitor(this));
		}
		logger.info("Building Symbol Table Pass 1 Finished");

		// for (AST ast: asts){
		// ast.getRoot().accept(new ImportVisitor(this));
		// }
		// logger.info("Building Symbol Table Pass 2 Finished");
		//
		// for (AST ast: asts){
		// ast.getRoot().accept(new DeepDeclVisitor(this));
		// }
		// logger.info("Building Symbol Table Pass 3 Finished");
	}

	public void listScopes() {
		System.out.println("Listing Scopes");
		List<String> keys = new ArrayList<String>(this.scopes.keySet());
		Collections.sort(keys);
		for (String key : keys) {
			System.out.println(this.scopes.get(key).getName());
			this.scopes.get(key).listSymbols();
		}
	}
}
