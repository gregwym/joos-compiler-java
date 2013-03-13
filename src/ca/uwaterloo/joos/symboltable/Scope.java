package ca.uwaterloo.joos.symboltable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.hamcrest.Matchers;

import ca.uwaterloo.joos.Main;
import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.expr.name.Name;
import ca.uwaterloo.joos.ast.expr.name.QualifiedName;
import ca.uwaterloo.joos.ast.expr.name.SimpleName;
import ca.uwaterloo.joos.ast.type.ReferenceType;
import ca.uwaterloo.joos.symboltable.SymbolTable.SymbolTableException;
import ch.lambdaj.Lambda;

public abstract class Scope {

	public static final Logger logger = Main.getLogger(Scope.class);

	protected String name; // Represents the name of the current scope
	protected Map<String, TableEntry> symbols;
	protected ASTNode referenceNode;

	public Scope(String name, ASTNode referenceNode) {
		this.name = name;
		this.symbols = new HashMap<String, TableEntry>();
		this.referenceNode = referenceNode;
	}

	public String getName() {
		return name;
	}

	public ASTNode getReferenceNode() {
		return this.referenceNode;
	}

	protected List<TableEntry> entriesWithSuffix(Collection<TableEntry> entries, String suffix) {
		return Lambda.select(entries, Lambda.having(Lambda.on(TableEntry.class).getName(), Matchers.endsWith(suffix)));
	}

	protected List<? extends Scope> scopesWithSuffix(Collection<? extends Scope> scopes, String suffix) {
		return Lambda.select(scopes, Lambda.having(Lambda.on(Scope.class).getName(), Matchers.endsWith(suffix)));
	}

	public String resolveReferenceType(ReferenceType type, SymbolTable table) throws Exception {
		Name name = type.getName();
		if (name instanceof QualifiedName) {
			TypeScope match = table.getType(name.getName());
			return match.getName();
		} else if (name instanceof SimpleName) {
			return this.resolveSimpleNameType((SimpleName) name);
		}
		return null;
	}

	public abstract String resolveSimpleNameType(SimpleName name) throws Exception;

	public TableEntry resolveVariableToDecl(Name name) throws Exception {
		if (name instanceof QualifiedName) {
			List<String> components = ((QualifiedName) name).getComponents();
			String fieldName = components.get(0);
			List<TableEntry> entries = this.entriesWithSuffix(this.symbols.values(), "." + fieldName);
			if (entries.size() > 1) {
				throw new SymbolTableException("Resolved variable " + name.getName() + " to multiple definition " + entries);
			} else if (entries.size() == 1) {
				return entries.get(0);
			}
		} else if (name instanceof SimpleName) {
			List<TableEntry> entries = this.entriesWithSuffix(this.symbols.values(), "." + name.getName());
			if (entries.size() > 1) {
				throw new SymbolTableException("Resolved variable " + name.getName() + " to multiple definition " + entries);
			} else if (entries.size() == 1) {
				return entries.get(0);
			}
		}
		return null;
	}

	public void listSymbols() {
		System.out.println("\tReferences to: " + this.referenceNode);
		System.out.println("\tSymbols:");
		List<String> keys = new ArrayList<String>(this.symbols.keySet());
		Collections.sort(keys);
		for (String key : keys) {
			System.out.println("\t\t" + key + "\t" + this.symbols.get(key).getNode());
		}
		System.out.println();
	}

	public String toString() {
		return "<" + this.getClass().getSimpleName() + "> " + this.name;
	}
}