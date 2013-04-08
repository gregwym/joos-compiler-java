package ca.uwaterloo.joos.codegen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.Modifiers.Modifier;
import ca.uwaterloo.joos.ast.decl.FieldDeclaration;
import ca.uwaterloo.joos.ast.decl.LocalVariableDeclaration;
import ca.uwaterloo.joos.ast.decl.MethodDeclaration;
import ca.uwaterloo.joos.ast.decl.ParameterDeclaration;
import ca.uwaterloo.joos.ast.decl.TypeDeclaration;
import ca.uwaterloo.joos.ast.statement.Block;
import ca.uwaterloo.joos.symboltable.Scope;
import ca.uwaterloo.joos.symboltable.SemanticsVisitor;
import ca.uwaterloo.joos.symboltable.SymbolTable;
import ca.uwaterloo.joos.symboltable.TypeScope;
import ca.uwaterloo.joos.checker.*;

public class IndexerVisitor extends SemanticsVisitor {
	int methods = 0;
	int fields = 1;
	int locals = 1;
	private int parameters = 2;
	public static ArrayList<FieldDeclaration> StaticFields = new ArrayList<FieldDeclaration>();
	HashMap<String, Integer> countList = new HashMap<String, Integer>();
	Map<TypeDeclaration, Stack<TypeScope>> HierarchyChain = new HashMap<TypeDeclaration, Stack<TypeScope>>();

	static ArrayList<String> checkedType = new ArrayList<String>();
	Map<Integer, Scope> methodList = new HashMap<Integer, Scope>();

	public IndexerVisitor(SymbolTable table) {
		super(table);
		
		
		// Map<TypeDeclaration, Stack<TypeScope>> tmpHierarchyChain = new
		// HashMap<TypeDeclaration,
		// Stack<TypeScope>>(HierarchyChecker.getClassHierachyChain());
		// HierarchyChain.putAll(tmpHierarchyChain);
		// HierarchyChain.putAll();
		for (Map.Entry<TypeDeclaration, Stack<TypeScope>> e : HierarchyChecker.getClassHierachyChain().entrySet()) {
			Stack<TypeScope> stack = new Stack<TypeScope>();
			stack.addAll(e.getValue());
			HierarchyChain.put(e.getKey(),stack );
		}
	}

	@Override
	public void willVisit(ASTNode node) throws Exception {
		super.willVisit(node);
		if (node instanceof TypeDeclaration) {
			Stack<TypeScope> chain = HierarchyChain.get(node);
			while (chain.size() > 1) {// When size is 1, we are looking at our
										// current type
				TypeScope ts = chain.pop();
				TypeDeclaration td = (TypeDeclaration) ts.getReferenceNode();
				if (!td.checked) {
					IndexerVisitor iv = new IndexerVisitor(this.table);
					iv.pushScope(ts.getWithinPackage());
					((TypeDeclaration) ts.getReferenceNode()).setSignatures(methodList);
					ts.getReferenceNode().accept(iv);
				}
				methodList.putAll(((TypeDeclaration) ts.getReferenceNode()).getSignatures());
				methods = ((TypeDeclaration) ts.getReferenceNode()).getSignatures().size();

			}
		}

		else if (node instanceof MethodDeclaration) {
			if (((MethodDeclaration) node).getModifiers().containModifier(Modifier.NATIVE) || ((MethodDeclaration) node).getModifiers().containModifier(Modifier.ABSTRACT)) {
				// Skip Virtual and Native methods for now
			} else if (((MethodDeclaration) node).getOverideMethod() != null) {
				// The method is overridden and should have already been
				// indexed.
				MethodDeclaration om = ((MethodDeclaration) node).getOverideMethod();
				((MethodDeclaration) node).setIndex(om.getIndex());
				// Add this method at the same index in the methods list
				methodList.put(om.getIndex(), this.getCurrentScope());
			} else {
				// Not overriden. Need to set an index...
				methodList.put(methods, this.getCurrentScope());
				((MethodDeclaration) node).setIndex(methods);
				methods++;
			}
		}
	}

	@Override
	public boolean visit(ASTNode node) throws Exception {
		if (node instanceof TypeDeclaration) {
			return (!((TypeDeclaration) node).checked);
		}

		if (node instanceof MethodDeclaration) {
			for (ParameterDeclaration param : ((MethodDeclaration) node).getParameters()) {
				param.setIndex(parameters);
				parameters++;
			}
			Block body = ((MethodDeclaration) node).getBody();
			if (body != null) {
				List<LocalVariableDeclaration> vars = ((MethodDeclaration) node).getBody().getLocalVariable();
				if (vars != null)
					((MethodDeclaration) node).totalLocalVariables = vars.size(); // TODO
																					// REMOVE
			}
		}

		if (node instanceof FieldDeclaration) {
			if (((FieldDeclaration) node).getModifiers().containModifier(Modifier.STATIC)) {
				StaticFields.add((FieldDeclaration) node);
			} else {
				((FieldDeclaration) node).setIndex(fields);
				fields++;
			}
		}

		if (node instanceof LocalVariableDeclaration) {
			((LocalVariableDeclaration) node).setIndex(locals);
			locals++;
		}
		return true;
	}

	@Override
	public void didVisit(ASTNode node) throws Exception {

		if (node instanceof MethodDeclaration) {
			((MethodDeclaration) node).totalLocalVariables = locals;
			locals = 1;
			parameters = 2;
		}
		if (node instanceof TypeDeclaration) {
			((TypeDeclaration) node).setSignatures(methodList);
			((TypeDeclaration) node).totalFieldDeclarations = ((TypeDeclaration) node).getBody().getFields().size();
			((TypeDeclaration) node).totalMethodDeclarations = methods;
			methodList = new HashMap<Integer, Scope>();
			methods = 0;
			((TypeDeclaration) node).checked = true;
			fields = 1;
		}
		super.didVisit(node);
	}
}
