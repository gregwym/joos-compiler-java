package ca.uwaterloo.joos.symboltable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.decl.ConstructorDeclaration;
import ca.uwaterloo.joos.ast.decl.FieldDeclaration;
import ca.uwaterloo.joos.ast.decl.MethodDeclaration;
import ca.uwaterloo.joos.ast.decl.ParameterDeclaration;
import ca.uwaterloo.joos.ast.expr.name.Name;
import ca.uwaterloo.joos.ast.expr.name.SimpleName;
import ca.uwaterloo.joos.ast.type.Type;
import ca.uwaterloo.joos.symboltable.SymbolTable.SymbolTableException;
import ch.lambdaj.Lambda;

public class TypeScope extends Scope {

	protected PackageScope withinPackage;
	protected Map<String, TypeScope> singleImport;
	protected Map<String, PackageScope> onDemandImport;
	protected TypeScope superScope;
	protected Map<String, TypeScope> interfaceScopes;

	public TypeScope(String name, PackageScope withinPackage, ASTNode referenceNode) {
		super(name, referenceNode);

		this.withinPackage = withinPackage;
		this.singleImport = new HashMap<String, TypeScope>();
		this.onDemandImport = new HashMap<String, PackageScope>();
		this.superScope = null;
		this.interfaceScopes = new HashMap<String, TypeScope>();
	}

	public PackageScope getWithinPackage() {
		return withinPackage;
	}

	public void addSingleImport(String simpleName, TypeScope scope) throws SymbolTableException {
		// Check name clash with type
		if ((!this.getName().equals(scope.getName())) && this.getName().endsWith("." + simpleName)) {
			throw new SymbolTableException("Single Type Import " + scope.getName() + " clashes with type declaration " + this.getName());
		}

		// Check name clash with other import
		List<? extends Scope> clashImports = this.scopesWithSuffix(this.singleImport.values(), "." + simpleName);
		if (clashImports.size() > 0) {
			for (Scope clashImport : clashImports) {
				if (!clashImport.getName().equals(scope.getName())) {
					throw new SymbolTableException("Single Type Import " + scope.getName() + " clashes with type declaration[s] " + clashImports);
				}
			}
		}

		this.singleImport.put(scope.getName(), scope);
	}

	public void addOnDemandImport(PackageScope scope) {
		this.onDemandImport.put(scope.getName(), scope);
	}

	public String nameForDecl(FieldDeclaration field) throws Exception {
		String name = this.getName() + "." + field.getName().getName();
		return name;
	}

	public void addFieldDecl(FieldDeclaration field) throws Exception {
		String name = this.nameForDecl(field);
		TableEntry entry = new TableEntry(name, field);

		if (this.symbols.containsKey(name)) {
			throw new SymbolTableException("Duplicate Field Declarations: " + name);
		}

		this.symbols.put(name, entry);
	}

	public TableEntry getFieldDecl(FieldDeclaration field) throws Exception {
		return this.symbols.get(this.nameForDecl(field));
	}

	public String signatureOfMethod(MethodDeclaration method) throws Exception {
		return this.signatureOfMethod(method.getName().getName(), 
				method instanceof ConstructorDeclaration, 
				Lambda.extract(method.getParameters(), Lambda.on(ParameterDeclaration.class).getType()));
	}
	
	public String signatureOfMethod(String methodName, boolean isConstructor, List<Type> parameterTypes) throws Exception {
		String name = this.name + "." + methodName + "(";
		if (isConstructor) {
			name += "THIS,";
		}
		for (Type parameterType : parameterTypes) {
			name += parameterType.getFullyQualifiedName() + ",";
		}
		name += ")";
		return name;
	}

	public TableEntry addMethod(MethodDeclaration node) throws Exception {
		String name = this.signatureOfMethod(node);
		if (this.symbols.containsKey(name)) {
			throw new SymbolTableException("Duplicate Method Declaraion " + name);
		}
		TableEntry entry = new TableEntry(name, node);
		this.symbols.put(name, entry);
		return entry;
	}

	public TableEntry getMethod(MethodDeclaration node) throws Exception {
		// If false, no Method exists and we can add it
		String name = this.signatureOfMethod(node);
		return this.symbols.get(name);
	}
	
	public TableEntry getTableEntry(ASTNode node) throws Exception {
		if(node instanceof FieldDeclaration) {
			return this.getFieldDecl((FieldDeclaration) node);
		} else if(node instanceof MethodDeclaration) {
			return this.getMethod((MethodDeclaration) node);
		}
		return null;
	}
	
	public void setSuperScope(TypeScope superScope) {
		this.superScope = superScope;
	}
	
	public TypeScope getSuperScope() {
		return this.superScope;
	}
	
	public void addInterfaceScope(TypeScope interfaceScope) {
		this.interfaceScopes.put(interfaceScope.getName(), interfaceScope);
	}
	
	public Map<String, TypeScope> getInterfaceScopes() {
		return this.interfaceScopes;
	}
	
	public boolean isSubclassOf(String fullyQualifiedName) {
		if(this.name.equals(fullyQualifiedName)) {
			return true;
		}
		for(TypeScope typeScope: this.interfaceScopes.values()) {
			if(typeScope.isSubclassOf(fullyQualifiedName)) {
				return true;
			}
		}
		if(this.superScope != null) {
			return this.superScope.isSubclassOf(fullyQualifiedName);
		}
		return false;
	}

	@Override
	public String resolveSimpleNameType(SimpleName name) throws Exception {
		// Check enclosing type
		if (this.getName().endsWith("." + name.getName())) {
			return this.getName();
		}

		// Check Single Type Imports
		String type = null;
		List<String> types = new ArrayList<String>();
		List<Scope> scopes = new ArrayList<Scope>();
		scopes.addAll(this.scopesWithSuffix(this.singleImport.values(), "." + name.getName()));

		if (scopes.size() > 1) {
			throw new SymbolTableException("More than one match was found in Single Type Imports for type " + name);
		} else if (scopes.size() == 1) {
			return scopes.get(0).getName();
		}

		// Check Same Package
		type = this.withinPackage.resolveSimpleNameType(name);
		if (type != null) {
			return type;
		}

		// Check On Demand Imports
		for (PackageScope scope : this.onDemandImport.values()) {
			type = scope.resolveSimpleNameType(name);
			if (type != null) {
				types.add(type);
			}
		}

		if (types.size() > 1) {
			throw new SymbolTableException("More than one match was found in On Demand Imports for type " + name);
		} else if (types.size() == 1) {
			return types.get(0);
		}

		return null;
	}
	
	@Override
	public TableEntry resolveVariableToDecl(Name name) throws Exception {
		TableEntry result = super.resolveVariableToDecl(name);
		if(result == null && this.superScope != null) {
			result = this.superScope.resolveVariableToDecl(name);
		}
		return result;
	}

	@Override
	public void listSymbols() {
		System.out.println("\tPackage:");
		System.out.println("\t\t" + this.withinPackage);

		if(this.singleImport.size() > 0) {
			System.out.println("\tSingle Type Imports:");
			for (Scope scope : this.singleImport.values()) {
				System.out.println("\t\t" + scope);
			}
		}
		
		if(this.onDemandImport.size() > 0) {
			System.out.println("\tOnDemand Imports:");
			for (Scope scope : this.onDemandImport.values()) {
				System.out.println("\t\t" + scope);
			}
		}
		
		if(this.superScope != null) {
			System.out.println("\tSuper Scope:");
			System.out.println("\t\t" + this.superScope);
		}

		if(this.interfaceScopes.size() > 0) {
			System.out.println("\tInterface Scopes:");
			for (Scope scope : this.interfaceScopes.values()) {
				System.out.println("\t\t" + scope);
			}
		}
		
		super.listSymbols();
	}
}
