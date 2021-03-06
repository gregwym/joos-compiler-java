package ca.uwaterloo.joos.symboltable;

import java.util.List;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.ASTNode.ChildTypeUnmatchException;
import ca.uwaterloo.joos.ast.FileUnit;
import ca.uwaterloo.joos.ast.decl.ImportDeclaration;
import ca.uwaterloo.joos.ast.decl.OnDemandImport;
import ca.uwaterloo.joos.ast.decl.SingleImport;
import ca.uwaterloo.joos.ast.decl.TypeDeclaration;
import ca.uwaterloo.joos.ast.expr.name.Name;
import ca.uwaterloo.joos.symboltable.SymbolTable.SymbolTableException;

public class ImportVisitor extends SemanticsVisitor {

	public ImportVisitor(SymbolTable table) {
		super(table);
	}

	public boolean visit(ASTNode node) throws ChildTypeUnmatchException, Exception {
		if (node instanceof TypeDeclaration) {
			TypeScope scope = (TypeScope) this.getCurrentScope();

			// Add java.lang implicitly
			if (this.table.containPackage("java.lang")) {
				scope.addOnDemandImport(this.table.getPackage("java.lang"));
			} else {
				// throw new Exception("Missing java.lang");
			}

			List<ImportDeclaration> imports = ((FileUnit) node.getParent()).getImportDeclarations();
			for (ImportDeclaration anImport : imports) {
				if (anImport instanceof SingleImport) {
					Name name = anImport.getImportName();
					String typeName = name.getName();
					
					if (this.table.containType(typeName)) {
						scope.addSingleImport(name.getSimpleName(), this.table.getType(typeName));
					} else {
						throw new SymbolTableException("Unknown Single Import " + anImport.getIdentifier());
					}
				} else if (anImport instanceof OnDemandImport) {
					String name = anImport.getImportName().getName();

					if (this.table.containPackage(name)) {
						scope.addOnDemandImport(this.table.getPackage(name));
						continue;
					} 
					
					List<? extends Scope> scopes = this.table.getScopesByPrefix(name + ".", PackageScope.class);
					if (scopes.size() > 0) {
						for (Scope packScope : scopes) {
							scope.addOnDemandImport((PackageScope) packScope);
						}
					} else {
						throw new SymbolTableException("Unknown On Demand Import " + anImport.getIdentifier());
					}
				}
			}
		}

		return node instanceof FileUnit;
	}

}
