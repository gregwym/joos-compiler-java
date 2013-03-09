package ca.uwaterloo.joos.symboltable;

import java.util.List;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.ASTNode.ChildTypeUnmatchException;
import ca.uwaterloo.joos.ast.FileUnit;
import ca.uwaterloo.joos.ast.decl.ImportDeclaration;
import ca.uwaterloo.joos.ast.decl.OnDemandImport;
import ca.uwaterloo.joos.ast.decl.PackageDeclaration;
import ca.uwaterloo.joos.ast.decl.SingleImport;
import ca.uwaterloo.joos.ast.decl.TypeDeclaration;
import ca.uwaterloo.joos.symboltable.SymbolTable.SymbolTableException;

public class ImportVisitor extends SemanticsVisitor {

	public ImportVisitor(SymbolTable table) {
		super(table);
	}

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

	public boolean visit(ASTNode node) throws ChildTypeUnmatchException, Exception {
		if (node instanceof TypeDeclaration) {
			TypeScope scope = (TypeScope) this.getCurrentScope();
			
			// Add java.lang implicitly
			if(this.table.containPackage("java.lang")) {
				scope.addOnDemandImport(this.table.getPackage("java.lang"));
			}
			else {
//				throw new Exception("Missing java.lang");
			}
			
			List<ImportDeclaration> imports = ((FileUnit) node.getParent()).getImportDeclarations();
			for(ImportDeclaration anImport: imports) {
				if(anImport instanceof SingleImport) {
					String name = anImport.getImportName().getName();
					if(this.table.containType(name)) {
						scope.addSingleImport(this.table.getType(name));
					}
					else {
						throw new SymbolTableException("Unknown Single Import " + anImport.getIdentifier());
					}
				} else if(anImport instanceof OnDemandImport) {
					String name = anImport.getImportName().getName();
					if(this.table.containPackage(name)) {
						scope.addOnDemandImport(this.table.getPackage(name));
					}
					else {
						throw new SymbolTableException("Unknown On Demand Import " + anImport.getIdentifier());
					}
				}
			}
			
			return false;
		}

		return node instanceof FileUnit;
	}

}
