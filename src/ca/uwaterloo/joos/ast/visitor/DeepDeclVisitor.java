package ca.uwaterloo.joos.ast.visitor;

import java.util.Stack;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.ASTNode.ChildTypeUnmatchException;
import ca.uwaterloo.joos.ast.decl.ImportDeclaration;
import ca.uwaterloo.joos.ast.decl.MethodDeclaration;
import ca.uwaterloo.joos.ast.decl.VariableDeclaration;
import ca.uwaterloo.joos.ast.decl.ClassDeclaration;
import ca.uwaterloo.joos.ast.decl.PackageDeclaration;
import ca.uwaterloo.joos.symbolTable.SymbolTable;

public class DeepDeclVisitor extends SemanticsVisitor {

	private String name = null;
	public DeepDeclVisitor(SymbolTable ist) {
		super(ist);
		String lookup = null;
		// TODO Auto-generated constructor stub
	}
	
	public void willVisit(ASTNode node){
		if (node instanceof MethodDeclaration){
			MethodDeclaration CNode = (MethodDeclaration) node;
			name = name + "." + CNode.getIdentifier();
			System.out.println(name);
			st.openScope(name);
//			st.
		}
	}
	
	public void didVisit(ASTNode node){
		if (node instanceof MethodDeclaration){
			MethodDeclaration CNode = (MethodDeclaration) node;
			st.closeScope();
			name = name.substring(0, name.lastIndexOf("."));
			System.out.println("DONE BODY: " + name);
			System.out.println(name);
			
		}
	}
	
	public boolean visit(ASTNode node) throws ChildTypeUnmatchException, Exception {
		if (node instanceof PackageDeclaration){
			PackageDeclaration PNode = (PackageDeclaration) node;
//			System.out.println("Packagename: " + PNode.getPackageName());
			name = PNode.getPackageName();
		}
		
		if (node instanceof ClassDeclaration){
			
//			System.out.println("SCOPESIZE: " + st.getView().size());
			
			
		}
		
		if (node instanceof MethodDeclaration){
			//open a new symboltable at Package.classname.Methodname
			MethodDeclaration Mnode = (MethodDeclaration) node;
			SymbolTable nst = new SymbolTable();
			nst.setName(name);
			nst.addScope();
//			nst.constructScope(node);
//			return false;
		}
		
		if (node instanceof VariableDeclaration){
			//check upper scope for previous definitions
			//How to get decl name...
			
			VariableDeclaration VNode = (VariableDeclaration)node;
			
//			System.out.println("DeepDeclVisitor.visit(): declarationname: " + VNode.getName().getName());
			Stack<SymbolTable> scview = st.getView();
			for (int i = scview.size() - 1; i >= 0; i--){
//				if (scview.get(i).isEmpty(VNode.getName().getName())) System.exit(9);
			}
		}
		
		return true;
	}

}
