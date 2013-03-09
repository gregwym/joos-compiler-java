//Scratch
//TODO 
//	-JAVADOC
//	-Define Table
package ca.uwaterloo.joos.symboltable;

//Proposal
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.decl.BodyDeclaration;
import ca.uwaterloo.joos.ast.decl.LocalVariableDeclaration;
import ca.uwaterloo.joos.ast.decl.ParameterDeclaration;
import ca.uwaterloo.joos.ast.decl.VariableDeclaration;
import ca.uwaterloo.joos.ast.expr.name.Name;
import ca.uwaterloo.joos.ast.type.ReferenceType;

public abstract class Scope {
	/**
	 * Symbol Table
	 * 
	 * Scans an AST of a validated joos source file. The class maintains a
	 * static HashMap. After an AST scan is completed the HashMap is updated
	 * with the declarations held in the file's global namespace.
	 * 
	 */

	protected String name = null; // Represents the name of the current scope
	protected Map<String, TableEntry> symbols = null; // A map mapping
														// identifiers to their
														// related ASTNode

	public Scope(String name) {
		this.name = name;
		this.symbols = new HashMap<String, TableEntry>();
	}

	public String getName() {
		return name;
	}

	public void addDeclaration(String key, ASTNode node) {
		TableEntry te = new TableEntry(node);
		symbols.put(key, te);

	}

	public String nameForDecl(VariableDeclaration field) throws Exception {
		String name = this.getName() + "." + field.getName().getName();
		return name;
	}

	public boolean containVariableName(VariableDeclaration varDecl) throws Exception {
		String simpleName = varDecl.getName().getSimpleName();
		for (String key : this.symbols.keySet()) {
			TableEntry entry = this.symbols.get(key);
			ASTNode node = entry.getNode();

			if ((node instanceof LocalVariableDeclaration || node instanceof ParameterDeclaration) && 
					((BodyDeclaration) node).getName().getSimpleName().equals(simpleName)) {
				return true;
			}
		}
		return false;
	}

	public void addVariableDecl(VariableDeclaration node) throws Exception {
		// Add a field
		TableEntry entry = new TableEntry(node);
		String name = this.nameForDecl(node);

		symbols.put(name, entry);
	}

	public TableEntry getVariableDecl(VariableDeclaration node) throws Exception {
		return this.symbols.get(this.nameForDecl(node));
	}

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