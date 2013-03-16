package ca.uwaterloo.joos.symboltable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.hamcrest.Matchers;

import ca.uwaterloo.joos.Main;
import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.decl.PackageDeclaration;
import ch.lambdaj.Lambda;

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

	public PackageScope getPackageByDecl(PackageDeclaration packDecl) throws Exception {
		// Get Package Name
		String name = null;
		if (packDecl.getPackageName() == null) {
			name = "__default__";
		} else {
			name = packDecl.getPackageName().getName();
		}

		// Find the scope
		Scope scope = this.scopes.get(name);
		if (scope == null) {
			scope = this.addPackage(name, packDecl);
		} else if (!(scope instanceof PackageScope)) {
			throw new SymbolTableException("Expecting PackageScope but get " + scope.getClass().getName());
		}
		return (PackageScope) scope;
	}

	public PackageScope getPackage(String name) throws Exception {
		// Get Package Name
		if (name == null) {
			name = "__default__";
		}

		// Find the scope
		Scope scope = this.scopes.get(name);
		if (scope != null && !(scope instanceof PackageScope)) {
			throw new SymbolTableException("Expecting PackageScope but get " + scope.getClass().getName());
		}
		return (PackageScope) scope;
	}

	public List<? extends Scope> getScopesByPrefix(String prefix, Class<?> scopeClass) {
		return Lambda.select(Lambda.select(this.scopes.values(), Matchers.instanceOf(scopeClass)), Lambda.having(Lambda.on(Scope.class).getName(), Matchers.startsWith(prefix)));
	}

	public TypeScope getType(String name) throws Exception {
		Scope scope = this.scopes.get(name);
		if (scope != null && !(scope instanceof TypeScope)) {
			throw new SymbolTableException("Expecting TypeScope but get " + scope);
		}
		return (TypeScope) scope;
	}

	public BlockScope getBlock(String name) throws Exception {
		Scope scope = this.scopes.get(name);
		if (scope != null && !(scope instanceof BlockScope)) {
			throw new SymbolTableException("Expecting BlockScope but get " + scope);
		}
		return (BlockScope) scope;
	}

	public boolean containPackage(String name) {
		Scope scope = this.scopes.get(name);
		return scope != null && scope instanceof PackageScope;
	}

	public boolean containType(String name) {
		Scope scope = this.scopes.get(name);
		return scope != null && scope instanceof TypeScope;
	}

	public boolean containBlock(String name) {
		Scope scope = this.scopes.get(name);
		return scope != null && scope instanceof BlockScope;
	}

	public PackageScope addPackage(String packageName, ASTNode referenceNode) throws Exception {
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

		PackageScope scope = new PackageScope(packageName, referenceNode);
		this.scopes.put(packageName, scope);
		return scope;
	}

	public TypeScope addType(String typeName, PackageScope packageScope, ASTNode referenceNode) throws Exception {
		// Check for prefix conflict
		List<? extends Scope> packages = this.getScopesByPrefix(typeName + ".", PackageScope.class);
		if (this.containPackage(typeName) || !packages.isEmpty()) {
			throw new SymbolTableException("Type declaration conflict with package prefix " + typeName);
		}

		TypeScope scope = new TypeScope(typeName, packageScope, referenceNode);
		this.scopes.put(typeName, scope);
		return scope;
	}

	public BlockScope addBlock(String blockName, Scope parent, ASTNode referenceNode) throws Exception {
		// Check for prefix conflict
		if (this.scopes.containsKey(blockName)) {
			throw new SymbolTableException("Duplicate Block Declaration: " + blockName);
		}

		BlockScope scope = new BlockScope(blockName, parent, referenceNode);
		this.scopes.put(blockName, scope);
		return scope;
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
