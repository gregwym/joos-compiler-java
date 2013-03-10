package ca.uwaterloo.joos.symboltable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.uwaterloo.joos.ast.decl.ConstructorDeclaration;
import ca.uwaterloo.joos.ast.decl.FieldDeclaration;
import ca.uwaterloo.joos.ast.decl.MethodDeclaration;
import ca.uwaterloo.joos.ast.decl.ParameterDeclaration;
import ca.uwaterloo.joos.ast.expr.name.SimpleName;
import ca.uwaterloo.joos.symboltable.SymbolTable.SymbolTableException;

public class TypeScope extends Scope {
	
	protected PackageScope withinPackage;
	protected Map<String, TypeScope> singleImport;
	protected Map<String, PackageScope> onDemandImport;

	public TypeScope(String name, PackageScope withinPackage) {
		super(name);
		
		this.withinPackage = withinPackage;
		this.singleImport = new HashMap<String, TypeScope>();
		this.onDemandImport = new HashMap<String, PackageScope>();
	}

	public PackageScope getWithinPackage() {
		return withinPackage;
	}
	
	public void addSingleImport(TypeScope scope) {
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
		
		if(this.symbols.containsKey(name)) {
			throw new SymbolTableException("Duplicate Field Declarations: " + name);
		}

		this.symbols.put(name, entry);
	}

	public TableEntry getFieldDecl(FieldDeclaration field) throws Exception {
		return this.symbols.get(this.nameForDecl(field));
	}

	public String signatureOfMethod(MethodDeclaration method) throws Exception {
		String name = this.name + "." + method.getName().getName() + "(";
		if(method instanceof ConstructorDeclaration) {
			name += "THIS,";
		}
		for(ParameterDeclaration parameter: method.getParameters()) {
			name += parameter.getType().getIdentifier() + ",";
		}
		name += ")";
		return name;
	}

	public void addMethod(MethodDeclaration node) throws Exception{
		String name = this.signatureOfMethod(node);
		if(this.symbols.containsKey(name)) {
			throw new SymbolTableException("Duplicate Method Declaraion " + name);
		}
		this.symbols.put(name, new TableEntry(name, node));
	}
	
	public TableEntry getMethod(MethodDeclaration node) throws Exception{
		//If false, no Method exists and we can add it
		String name = this.signatureOfMethod(node);
		return this.symbols.get(name);
	}
	
	@Override
	public boolean resolveSimpleNameType(SimpleName name) throws Exception {
		// Check enclosing type
		if(this.getName().matches(".+\\." + name.getName() + "\\{\\}")) {
			return true;
		}
		
		// Check Single Type Imports
		List<Scope> matches = new ArrayList<Scope>();
		matches.addAll(this.scopesWithSuffix(this.singleImport.values(), "." + name.getName() + "{}"));
		
		if(matches.size() > 1) {
			throw new SymbolTableException("More than one match was found in Single Type Imports for type " + name);
		} else if(matches.size() == 1) {
			return true;
		}
		
		// Check Same Package
		if(this.withinPackage.resolveSimpleNameType(name)) {
			return true;
		}
		
		// Check On Demand Imports
		for(PackageScope scope: this.onDemandImport.values()) {
			if(scope.resolveSimpleNameType(name)) {
				matches.add(scope);
			}
		}
		
		if(matches.size() > 1) {
			throw new SymbolTableException("More than one match was found in On Demand Imports for type " + name);
		} else if(matches.size() == 1) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public void listSymbols(){
		System.out.println("\tPackage:");
		System.out.println("\t\t" + this.withinPackage);
		
		System.out.println("\tSingle Type Imports:");
		for(Scope scope: this.singleImport.values()) {
			System.out.println("\t\t" + scope);
		}
		
		System.out.println("\tOnDemand Imports:");
		for(Scope scope: this.onDemandImport.values()) {
			System.out.println("\t\t" + scope);
		}
		
		super.listSymbols();
	}
}
