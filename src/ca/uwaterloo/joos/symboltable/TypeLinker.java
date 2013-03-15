package ca.uwaterloo.joos.symboltable;

import java.util.ArrayList;
import java.util.List;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.decl.ClassDeclaration;
import ca.uwaterloo.joos.ast.decl.MethodDeclaration;
import ca.uwaterloo.joos.ast.decl.PackageDeclaration;
import ca.uwaterloo.joos.ast.decl.TypeDeclaration;
import ca.uwaterloo.joos.ast.type.ReferenceType;
import ca.uwaterloo.joos.symboltable.SymbolTable.SymbolTableException;

public class TypeLinker extends SemanticsVisitor {
	
	public TypeLinker(SymbolTable table) {
		super(table);
	}

	@Override
	public boolean visit(ASTNode node) throws Exception {
		if(node instanceof ReferenceType) {
			ReferenceType refType = (ReferenceType) node;
			
			Scope currentScope = this.getCurrentScope();
			String resolved = currentScope.resolveReferenceType(refType, this.table);
			if(resolved == null) {
				throw new SymbolTableException("Cannot resolve type " + refType.getName().getName());
			}
			
			refType.setFullyQualifiedName(resolved);
		}
		return true;
	}
	
	@Override 
	public void willVisit(ASTNode node) throws Exception {
		if (node instanceof PackageDeclaration) {
			PackageScope scope = this.table.getPackageByDecl((PackageDeclaration) node);
			this.pushScope(scope);
		} else if (node instanceof TypeDeclaration) {
			PackageScope currentScope = (PackageScope) this.getCurrentScope();
			String name = ((TypeDeclaration) node).getIdentifier();
			name = currentScope.getName() + "." + name;
			
			TypeScope scope = this.table.getType(name);
			this.pushScope(scope);
		}
	}
	
	@Override
	public void didVisit(ASTNode node) throws Exception {
		
		// Add super classes and implemented interface to current TypeScope
		if(node instanceof ClassDeclaration) {
			TypeScope scope = (TypeScope) this.getCurrentScope();
			ReferenceType superClass = ((ClassDeclaration) node).getSuperClass();
			if(superClass != null) {
				TypeScope superScope = this.table.getType(superClass.getFullyQualifiedName());
				if(superScope == null) {
					throw new SymbolTableException("Extending unknown super class " + node.getIdentifier());
				}
				logger.finer("Adding " + superScope.getName() + " as super to " + node);
				scope.setSuperScope(superScope);
			} else if (!scope.getName().equals("java.lang.Object")) {
				TypeScope superScope = this.table.getType("java.lang.Object");
				if(superScope == null) {
					throw new SymbolTableException("Extending unknown super class " + node.getIdentifier());
				}
				logger.finer("Adding java.lang.Object as super to " + node);
				scope.setSuperScope(superScope);
			}
		}
		if(node instanceof TypeDeclaration) {
			TypeScope scope = (TypeScope) this.getCurrentScope();
			List<ReferenceType> interfaces = ((TypeDeclaration) node).getInterfaces();
			for(ReferenceType type: interfaces) {
				TypeScope typeScope = this.table.getType(type.getFullyQualifiedName());
				if(typeScope == null) {
					throw new SymbolTableException("Extending unknown interface " + node.getIdentifier());
				}
				logger.finer("Adding " + typeScope.getName() + " as interface to " + node);
				scope.addInterfaceScope(typeScope);
			}
		}
		
		if(node instanceof TypeDeclaration) {
			TypeScope scope = (TypeScope) this.getCurrentScope();
			List<TableEntry> entries = new ArrayList<TableEntry>(scope.symbols.values());
			
			for(TableEntry entry : entries) {
				if(entry.getNode() instanceof MethodDeclaration) {
					scope.symbols.remove(entry.getName());
					scope.addMethod((MethodDeclaration) entry.getNode());
				}
			}
		}
		
		// Add Type related scope to TableEntry
		if(node instanceof TypeDeclaration) {
			TypeScope typeScope = (TypeScope) this.popScope();
			PackageScope packageScope = (PackageScope) this.getCurrentScope();
			String typeName = ((TypeDeclaration) node).fullyQualifiedName;
			TableEntry entry = packageScope.getType(typeName);
			entry.setTypeScope(typeScope);
		}
		
		// Is anything other than TypeDeclaration, 
		// let super manipulate the scope stack first. 
//		super.didVisit(node);
	}

}
