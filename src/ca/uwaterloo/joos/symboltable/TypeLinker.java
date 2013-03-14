package ca.uwaterloo.joos.symboltable;

import java.util.List;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.decl.BodyDeclaration;
import ca.uwaterloo.joos.ast.decl.ClassDeclaration;
import ca.uwaterloo.joos.ast.decl.FieldDeclaration;
import ca.uwaterloo.joos.ast.decl.MethodDeclaration;
import ca.uwaterloo.joos.ast.decl.TypeDeclaration;
import ca.uwaterloo.joos.ast.decl.VariableDeclaration;
import ca.uwaterloo.joos.ast.type.ArrayType;
import ca.uwaterloo.joos.ast.type.ReferenceType;
import ca.uwaterloo.joos.ast.type.Type;
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
		super.didVisit(node);
		
		if(node instanceof FieldDeclaration || node instanceof MethodDeclaration) {
			Type type = ((BodyDeclaration) node).getType();
			if(type == null) {
				return;
			}
			
			TypeScope enclosingScope = (TypeScope) this.getCurrentScope();
			TableEntry entry = enclosingScope.getTableEntry(node);
			entry.setType(type);
			
			if(type instanceof ArrayType) {
				type = ((ArrayType) type).getType();
			}
			TypeScope typeScope = this.table.getType(type.getFullyQualifiedName());
			entry.setTypeScope(typeScope);
		} else if(node instanceof VariableDeclaration) {
			Type type = ((BodyDeclaration) node).getType();
			if(type == null) {
				return;
			}
			
			BlockScope enclosingScope = (BlockScope) this.getCurrentScope();
			TableEntry entry = enclosingScope.getVariableDecl((VariableDeclaration) node);
			entry.setType(type);
			
			if(type instanceof ArrayType) {
				type = ((ArrayType) type).getType();
			}
			TypeScope typeScope = this.table.getType(type.getFullyQualifiedName());
			entry.setTypeScope(typeScope);
		}
	}

}
