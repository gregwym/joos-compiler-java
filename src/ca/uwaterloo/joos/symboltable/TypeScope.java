package ca.uwaterloo.joos.symboltable;

import java.util.HashMap;
import java.util.Map;

import ca.uwaterloo.joos.ast.decl.ConstructorDeclaration;
import ca.uwaterloo.joos.ast.decl.MethodDeclaration;
import ca.uwaterloo.joos.ast.decl.ParameterDeclaration;
import ca.uwaterloo.joos.symboltable.SymbolTable.SymbolTableException;

public class TypeScope extends Scope {
	
	protected PackageScope withinPackage;
	protected Map<String, TypeScope> singleImport;
	protected Map<String, PackageScope> onDemandImport;

	public PackageScope getWithinPackage() {
		return withinPackage;
	}

	public void setWithinPackage(PackageScope withinPackage) {
		this.withinPackage = withinPackage;
	}
	
	public void addSingleImport(TypeScope scope) {
		this.singleImport.put(scope.getName(), scope);
	}
	
	public void addOnDemandImport(PackageScope scope) {
		this.onDemandImport.put(scope.getName(), scope);
	}

	public TypeScope(String name) {
		super(name);
		
		this.withinPackage = null;
		this.singleImport = new HashMap<String, TypeScope>();
		this.onDemandImport = new HashMap<String, PackageScope>();
	}

	public String signatureOfMethod(MethodDeclaration method) throws Exception {
		String name = this.name + "." + method.getName().getName() + "(";
		if(method instanceof ConstructorDeclaration) {
			name += "C";
		}
		for(ParameterDeclaration parameter: method.getParameters()) {
			name += parameter.getType().getIdentifier();
		}
		name += ")";
		return name;
	}

	public void addMethod(MethodDeclaration node) throws Exception{
		String name = this.signatureOfMethod(node);
		if(this.symbols.containsKey(name)) {
			throw new SymbolTableException("Duplicate method declaraion " + name);
		}
		this.symbols.put(name, new TableEntry(node));
	}
	
	public TableEntry getMethod(MethodDeclaration node) throws Exception{
		//If false, no Method exists and we can add it
		String name = this.signatureOfMethod(node);
		return this.symbols.get(name);
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
