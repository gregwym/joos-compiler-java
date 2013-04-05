package ca.uwaterloo.joos.codegen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.Modifiers.Modifier;
import ca.uwaterloo.joos.ast.decl.BodyDeclaration;
import ca.uwaterloo.joos.ast.decl.ClassDeclaration;
import ca.uwaterloo.joos.ast.decl.FieldDeclaration;
import ca.uwaterloo.joos.ast.decl.LocalVariableDeclaration;
import ca.uwaterloo.joos.ast.decl.MethodDeclaration;
import ca.uwaterloo.joos.ast.decl.TypeDeclaration;
import ca.uwaterloo.joos.ast.statement.Block;
import ca.uwaterloo.joos.checker.HierarchyChecker;
import ca.uwaterloo.joos.symboltable.Scope;
import ca.uwaterloo.joos.symboltable.SemanticsVisitor;
import ca.uwaterloo.joos.symboltable.SymbolTable;
import ca.uwaterloo.joos.symboltable.TypeScope;

public class IndexerVisitor extends SemanticsVisitor{
	int methodIdx = 0;
	int fieldIdx = 1;
	public static ArrayList<FieldDeclaration> StaticFields = new ArrayList<FieldDeclaration>();
	HashMap<String, Integer> countList = new HashMap<String, Integer>();
	Map<TypeDeclaration, Stack<TypeScope>> HierarchyChain = HierarchyChecker.getClassHierachyChain();
	static ArrayList<String> checkedType = new ArrayList<String>();
	Map<Integer, Scope> methodList = new HashMap<Integer, Scope>(); 
	
	public IndexerVisitor(SymbolTable table) {
		super(table);
	}

	@Override
	public void willVisit(ASTNode node) throws Exception{
		super.willVisit(node);
		if (node instanceof TypeDeclaration){
			Stack<TypeScope> chain = HierarchyChain.get(node);
			while (chain.size() > 1){//When size is 1, we are looking at our current type
				TypeScope ts = chain.pop();
				TypeDeclaration td = (TypeDeclaration) ts.getReferenceNode();
				if (!td.checked){
					IndexerVisitor iv = new IndexerVisitor(this.table);
					iv.pushScope(ts.getWithinPackage());
					((TypeDeclaration) ts.getReferenceNode()).setSignatures(methodList);
					ts.getReferenceNode().accept(iv);
				}
				methodList.putAll(((TypeDeclaration) ts.getReferenceNode()).getSignatures());
				methodIdx = ((TypeDeclaration)ts.getReferenceNode()).getSignatures().size();
				
			}
		}
		
		else if (node instanceof MethodDeclaration){
			if (((MethodDeclaration)node).getModifiers().containModifier(Modifier.NATIVE)||
					((MethodDeclaration)node).getModifiers().containModifier(Modifier.ABSTRACT)){
				//Skip Virtual and Native methods for now
			}
			else if (((MethodDeclaration)node).getOverideMethod() != null){
				//The method is overridden and should have already been indexed.
				MethodDeclaration om = ((MethodDeclaration)node).getOverideMethod();
				((MethodDeclaration)node).setIndex(om.getIndex());
				//Add this method at the same index in the methods list
				methodList.put(om.getIndex(), this.getCurrentScope());
			}
			else {
				//Not overriden. Need to set an index...
				methodList.put(methodIdx, this.getCurrentScope());
				((MethodDeclaration) node).setIndex(methodIdx);
				methodIdx++;
			}
		}
	}
	
	@Override
	public boolean visit(ASTNode node) throws Exception{
		if (node instanceof TypeDeclaration){
			return (!((TypeDeclaration)node).checked);
		}
		
		if (node instanceof MethodDeclaration) {
			Block body = ((MethodDeclaration)node).getBody();
			if (body != null){
				List<LocalVariableDeclaration> vars = ((MethodDeclaration) node).getBody().getLocalVariable();
				if (vars != null)((MethodDeclaration)node).totalLocalVariables = vars.size();
			}
		}
		
		if (node instanceof FieldDeclaration){
			if (((FieldDeclaration)node).getModifiers().containModifier(Modifier.STATIC)){
				StaticFields.add((FieldDeclaration)node);
			}
			else{
				((FieldDeclaration)node).setIndex(fieldIdx);
				fieldIdx++;
			}
		}
		
		return true;
	}
	
	@Override
	public void didVisit(ASTNode node)throws Exception{
		
		if(node instanceof MethodDeclaration){
		}
		if (node instanceof TypeDeclaration){
			((TypeDeclaration)node).setSignatures(methodList);
			((TypeDeclaration)node).totalFieldDeclarations = ((TypeDeclaration) node).getBody().getFields().size();
			((TypeDeclaration) node).totalMethodDeclarations = methodIdx;
			methodList = new HashMap<Integer,Scope>();
			methodIdx = 0;
			((TypeDeclaration)node).checked = true;
			fieldIdx = 1;
		}
		super.didVisit(node);
	}
}
