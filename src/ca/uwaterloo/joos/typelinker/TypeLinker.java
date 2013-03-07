package ca.uwaterloo.joos.typelinker;

import java.util.Stack;
import java.util.logging.Logger;

import ca.uwaterloo.joos.Main;
import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.ASTNode.ChildTypeUnmatchException;
import ca.uwaterloo.joos.ast.decl.MethodDeclaration;
import ca.uwaterloo.joos.ast.decl.PackageDeclaration;
import ca.uwaterloo.joos.ast.decl.TypeDeclaration;
import ca.uwaterloo.joos.ast.statement.Block;
import ca.uwaterloo.joos.ast.type.ReferenceType;
import ca.uwaterloo.joos.ast.type.Type;
import ca.uwaterloo.joos.ast.visitor.TypeVisitor;
import ca.uwaterloo.joos.symboltable.Scope;
import ca.uwaterloo.joos.symboltable.SymbolTable;

public class TypeLinker extends TypeVisitor {
	
	protected static Logger logger = Main.getLogger(TypeLinker.class);
	protected Stack<Scope> viewStack;
	protected Stack<Integer> blocks;
	protected SymbolTable table;

	public TypeLinker(SymbolTable table) {
		this(table, new Stack<Scope>());
//		logger.setLevel(Level.FINER);
	}
	
	public TypeLinker(SymbolTable table, Stack<Scope> viewStack) {
		this.viewStack = viewStack;
		this.blocks = new Stack<Integer>();
		this.table = table;
	}
	
	protected Scope getCurrentScope() {
		return this.viewStack.peek();
	}
	
	protected void pushScope(Scope table) {
		logger.finer("Pushing scope " + table.toString());
		this.viewStack.push(table);
	}
	
	protected Scope popScope() {
		Scope scope = this.viewStack.pop();
		logger.finer("Popping scope " + scope);
		return scope;
	}

	@Override
	protected void visitType(Type type) throws Exception {
		if(type instanceof ReferenceType) {
			ReferenceType refType = (ReferenceType) type;
			refType.fullyQualifedTypeName = this.getCurrentScope().lookupReferenceType(refType);
			
			if(refType.fullyQualifedTypeName == null) {
				refType.fullyQualifedTypeName = this.table.lookupReferenceType(refType);
				if(refType.fullyQualifedTypeName == null) {
					throw new Exception("Unresolved type " + refType.getName().getName());
				}
			}
		}
	}

	@Override
	public void willVisit(ASTNode node) throws Exception {

		if (node instanceof PackageDeclaration) {
			PackageDeclaration PNode = (PackageDeclaration) node;
			String name = PNode.getPackageName();
			Scope table = this.table.getScope(name);
			
			// Push current scope into the view stack
			this.pushScope(table);
		} else if (node instanceof TypeDeclaration) {
			Scope currentScope = this.getCurrentScope();
			String name = ((TypeDeclaration) node).getIdentifier();
			name = currentScope.getName() + "." + name + "{}";
			Scope table = this.table.getScope(name);
			
			// Push current scope into the view stack
			this.pushScope(table);
		} else if (node instanceof MethodDeclaration) {
			String name = this.getCurrentScope().signatureOfMethod((MethodDeclaration) node);
			Scope scope = this.table.getScope(name);
						
			this.pushScope(scope);
			this.blocks.push(0);
		} else if (node instanceof Block && this.blocks.peek() == 0) {
			this.blocks.push(this.blocks.pop() + 1);
		} else if (node instanceof Block && this.blocks.peek() > 0) {
			int block = this.blocks.pop();
			String name = this.getCurrentScope().getName() + "." + block + "Block";
			Scope scope = this.table.getScope(name);
			
			this.pushScope(scope);
			this.blocks.push(block + 1);
			this.blocks.push(1);
		}
	}
	
	@Override
	public void didVisit(ASTNode node) throws ChildTypeUnmatchException {
//		logger.finer("Leaving " + node);
		
		if (node instanceof TypeDeclaration) {
			this.popScope();
		} else if (node instanceof MethodDeclaration) {
			this.popScope();
			this.blocks.pop();
		} else if (node instanceof Block && this.viewStack.peek().getName().matches("(.+\\.)+\\d+Block")) {
			this.popScope();
			this.blocks.pop();
		}
	}

}
