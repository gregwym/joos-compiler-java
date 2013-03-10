//Scratch
//TODO 
//	-JAVADOC
//	-Define Table
package ca.uwaterloo.joos.symboltable;

//Proposal
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hamcrest.Matchers;

import ca.uwaterloo.joos.ast.expr.name.Name;
import ca.uwaterloo.joos.ast.expr.name.QualifiedName;
import ca.uwaterloo.joos.ast.expr.name.SimpleName;
import ca.uwaterloo.joos.ast.type.ReferenceType;
import ch.lambdaj.Lambda;

public abstract class Scope {
	/**
	 * Symbol Table
	 * 
	 * Scans an AST of a validated joos source file. The class maintains a
	 * static HashMap. After an AST scan is completed the HashMap is updated
	 * with the declarations held in the file's global namespace.
	 * 
	 */

	protected String name; // Represents the name of the current scope
	protected Map<String, TableEntry> symbols;

	public Scope(String name) {
		this.name = name;
		this.symbols = new HashMap<String, TableEntry>();
	}

	public String getName() {
		return name;
	}
	
	protected List<TableEntry> entriesWithSuffix(Collection<TableEntry> entries, String suffix) {
		return Lambda.select(entries, Lambda.having(Lambda.on(TableEntry.class).getName(), Matchers.endsWith(suffix)));
	}
	
	protected List<? extends Scope> scopesWithSuffix(Collection<? extends Scope> scopes, String suffix) {
		return Lambda.select(scopes, Lambda.having(Lambda.on(Scope.class).getName(), Matchers.endsWith(suffix)));
	}

	public boolean resolveReferenceType(ReferenceType type, SymbolTable table) throws Exception {
		Name name = type.getName();
		if(name instanceof QualifiedName) {
			TableEntry match = this.symbols.get(name.getName() + "{}");
			return match != null;
		} else if(name instanceof SimpleName) {
			this.resolveSimpleNameType((SimpleName) name);
		}
		return false;
	}
	
	public abstract boolean resolveSimpleNameType(SimpleName name) throws Exception;
	
	public String lookupReferenceType(ReferenceType type) throws Exception {
		Name name = type.getName();

		Map<String, Integer> nameWithPriority = new HashMap<String, Integer>();
		for (String key : this.symbols.keySet()) {
			if (key.matches("^(.+\\.)*" + name.getName() + "\\{\\}$")) {
				nameWithPriority.put(key, 0);
			}
		}

		Set<Integer> conflicting = new HashSet<Integer>();
		String result = null;
		int highest = 0;
		for (String key : nameWithPriority.keySet()) {
			int current = nameWithPriority.get(key);
			if (current > highest) {
				highest = current;
				result = key;
			} else if (current == highest) {
				conflicting.add(current);
			}
		}
		if (conflicting.contains(highest)) {
			throw new Exception("Unresovable ambiguous type " + type.getName());
		}

		return result;
	}

	public void listSymbols() {
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