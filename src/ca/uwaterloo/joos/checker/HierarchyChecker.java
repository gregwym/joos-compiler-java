package ca.uwaterloo.joos.checker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.Modifiers;
import ca.uwaterloo.joos.ast.body.InterfaceBody;
import ca.uwaterloo.joos.ast.decl.ClassDeclaration;
import ca.uwaterloo.joos.ast.decl.InterfaceDeclaration;
import ca.uwaterloo.joos.ast.decl.MethodDeclaration;
import ca.uwaterloo.joos.ast.decl.TypeDeclaration;
import ca.uwaterloo.joos.symboltable.SemanticsVisitor;
import ca.uwaterloo.joos.symboltable.SymbolTable;
import ca.uwaterloo.joos.symboltable.TableEntry;
import ca.uwaterloo.joos.symboltable.TypeScope;

public class HierarchyChecker extends SemanticsVisitor {
	TypeScope currentScope;
	TypeScope currentSuperScope;
	Map<String, TypeScope> interfaceScopes;
	String currentPrefix;
	static Set<String> checkedClass = new HashSet<String>();
	Stack<TypeScope> hierachyStack = new Stack<TypeScope>();

	public HierarchyChecker(SymbolTable table) {
		super(table);
	}

	public boolean visit(ASTNode node) throws Exception {

		if (node instanceof TypeDeclaration) {
			TypeDeclaration typeDeclNode = (TypeDeclaration) node;
			this.visitClassDecl(typeDeclNode);
		}
		return true;
	}

	protected void visitClassDecl(TypeDeclaration node) throws Exception {
		// System.out.println(":" + this.getCurrentScope());
		currentScope = (TypeScope) this.getCurrentScope();
		currentScope.getVisibleSymbols();
		if (currentScope instanceof TypeScope) {
			currentSuperScope = ((TypeScope) currentScope).getSuperScope();
			interfaceScopes = ((TypeScope) currentScope).getInterfaceScopes();
			Collection<TypeScope> interfaces = interfaceScopes.values();
			for (TypeScope INTERFACE : interfaces) {
				if (!(INTERFACE.getReferenceNode() instanceof InterfaceDeclaration)) {

					if (INTERFACE.getName().equals("java.lang.Object") && (currentScope.getReferenceNode() instanceof InterfaceDeclaration)) {

					} else {
						throw new Exception("can only implement interfaces");
					}
				}

			}
			if (currentSuperScope != null) {
				if (((TypeDeclaration) currentSuperScope.getReferenceNode()).getModifiers() != null) {
					if (((TypeDeclaration) currentSuperScope.getReferenceNode()).getModifiers().getModifiers().contains(Modifiers.Modifier.FINAL)) {
						throw new Exception("can not extend final");
					}
				}

				if (currentScope.getReferenceNode() instanceof InterfaceDeclaration) {
					if (currentSuperScope.getReferenceNode() instanceof ClassDeclaration) {
						throw new Exception("an interface can not extend a class");
					}
				}
				if (currentScope.getReferenceNode() instanceof ClassDeclaration) {
					if (currentSuperScope.getReferenceNode() instanceof InterfaceDeclaration) {
						throw new Exception("a class can not extend an interface");
					}
				}

			}
			checkSuperCycle();
			checkOverRide();
			checkAbstractClass();
		}

	}

	private void checkOverRide() throws Exception {

		System.out.println("current:" + ((TypeDeclaration) currentScope.getReferenceNode()).fullyQualifiedName);
		appendStack(currentScope);
		// currentScope.listSymbols();
		Stack<TypeScope> hierachyStack2 = hierachyStack;
		while (!hierachyStack2.empty()) {
			TypeScope currentTopScope = hierachyStack2.pop();

			System.out.println("Top" + ((TypeDeclaration) currentTopScope.getReferenceNode()).fullyQualifiedName);
			TypeScope currentSuperScope = currentTopScope.getSuperScope();

			Map<String, TypeScope> currentInterfaceScopes = currentTopScope.getInterfaceScopes();
			ArrayList<TypeScope> currentParentScopes = new ArrayList<TypeScope>(currentInterfaceScopes.values());
			if (currentSuperScope != null) {
				currentParentScopes.add(currentSuperScope);
			}
			addMethods(currentTopScope, currentParentScopes);
		}
	}

	private void appendStack(TypeScope currentAppendScope) {
		TypeScope currentSuperScope = currentAppendScope.getSuperScope();
		if (!hierachyStack.contains(currentScope)) {
			hierachyStack.add(currentScope);
			System.out.println("hierachyStack.add" + currentScope);
		}

		Map<String, TypeScope> currentInterfaceScopes = currentAppendScope.getInterfaceScopes();
		ArrayList<TypeScope> currentParentScopes = new ArrayList<TypeScope>(currentInterfaceScopes.values());
		if (currentSuperScope != null) {
			currentParentScopes.add(currentSuperScope);
		}
		for (TypeScope currentParentScope : currentParentScopes) {

			if (!hierachyStack.contains(currentParentScope)) {

				hierachyStack.add(currentParentScope);
				System.out.println("hierachyStack.add" + currentParentScope);
			}
			appendStack(currentParentScope);
		}

	}

	private void checkAbstractClass() throws Exception {
		TypeDeclaration currentNode = (TypeDeclaration) currentScope.getReferenceNode();
		Map<String, TableEntry> curerentVisibleMethods = currentScope.getVisibleSymbols();
		ArrayList<TableEntry> visibleMethods = new ArrayList<TableEntry>(curerentVisibleMethods.values());
		if (!currentNode.getModifiers().getModifiers().contains(Modifiers.Modifier.ABSTRACT) && currentNode instanceof ClassDeclaration) {
			for (TableEntry visibleMethod : visibleMethods) {
				if (visibleMethod.getNode() instanceof MethodDeclaration) {
					MethodDeclaration currentVisibleNode = (MethodDeclaration) visibleMethod.getNode();
					if (currentVisibleNode.getModifiers().getModifiers().contains(Modifiers.Modifier.ABSTRACT)) {
						throw new Exception("should be abstract");
					}
					if (currentVisibleNode.getParent() instanceof InterfaceBody) {
						throw new Exception("should be abstract");
					}
				}
			}
		}

	}

	private void checkSuperCycle() throws Exception {
		Stack<TypeScope> classes = new Stack<TypeScope>();
		classes.add(currentScope);

		Stack<String> extendCycle = new Stack<String>();

		while (!classes.empty()) {
			TypeScope currentTopScope = classes.pop();
			extendCycle.add(((TypeDeclaration) currentScope.getReferenceNode()).fullyQualifiedName);

			TypeScope currentSuperScope = currentTopScope.getSuperScope();

			Map<String, TypeScope> currentInterfaceScopes = currentTopScope.getInterfaceScopes();
			if (currentSuperScope != null) {
				String superName = ((TypeDeclaration) currentSuperScope.getReferenceNode()).fullyQualifiedName;

				if (extendCycle.contains(superName)) {
					throw new Exception("there is a cycle in extend");
				} else {
					classes.add(currentSuperScope);
				}
				extendCycle.add(superName);

			} else if (currentInterfaceScopes.size() > 0) {
				Collection<TypeScope> interfaces = currentInterfaceScopes.values();
				for (TypeScope INTERFACE : interfaces) {

					String interfaceName = ((TypeDeclaration) INTERFACE.getReferenceNode()).fullyQualifiedName;
					if (extendCycle.contains(interfaceName) && (currentScope.getReferenceNode() instanceof InterfaceDeclaration)) {
						System.out.println("duplicate:" + interfaceName);

						throw new Exception("there is a cycle in extend");

					} else {
						classes.add(INTERFACE);
					}

				}

			}
		}

	}

	private void addMethods(TypeScope currentScope, ArrayList<TypeScope> parentScopes) throws Exception {

		Map<String, ASTNode> parentMethods = new HashMap<String, ASTNode>();
		for (TypeScope parentScope : parentScopes) {
			Set<String> currentMethods = currentScope.getVisibleSymbols().keySet();
			System.out.println("####" + currentScope.getName() + currentMethods.toString());
			Set<String> simpleCurrentMethods = new HashSet<String>();
			for (String currentMethod : currentMethods) {
				simpleCurrentMethods.add(getSimpleSignature(currentMethod));
			}
			if (parentScope != null) {
				Iterator<Entry<String, TableEntry>> parentMethodIterator = parentScope.getVisibleSymbols().entrySet().iterator();
				while (parentMethodIterator.hasNext()) {
					Map.Entry<String, TableEntry> currentParentMethod = parentMethodIterator.next();
					if (currentParentMethod.getValue().getNode() instanceof MethodDeclaration) {
						String currentParentMethodSig = currentParentMethod.getKey();
						String currentSimpleParentMethodSig = getSimpleSignature(currentParentMethodSig);
						if (parentMethods.keySet().contains(currentSimpleParentMethodSig)) {
							System.out.println("parentDuplicate" + currentSimpleParentMethodSig);

							MethodDeclaration existMethodNode = (MethodDeclaration) parentMethods.get(currentSimpleParentMethodSig);
							MethodDeclaration newMethodNode = (MethodDeclaration) currentParentMethod.getValue().getNode();

							if ((existMethodNode.getType() != null) && (newMethodNode.getType() != null)) {
								if (!existMethodNode.getType().getIdentifier().equals(newMethodNode.getType().getIdentifier())) {
									throw new Exception("same method different return type");
								}
							} else {
								if ((existMethodNode.getType() == null) != (newMethodNode.getType() == null)) {
									throw new Exception("same method different return type");
								}

							}
							System.out.println("ISPUER!!" + (existMethodNode.getParent() instanceof InterfaceBody) + (newMethodNode.getParent() instanceof InterfaceBody));

							boolean containProtected = existMethodNode.getModifiers().getModifiers().contains(Modifiers.Modifier.PROTECTED);
							boolean containPublic = newMethodNode.getModifiers().getModifiers().contains(Modifiers.Modifier.PUBLIC);

							if (containProtected == containPublic) {
								throw new Exception("protected mothed can not override public");
							}
						} else {

							parentMethods.put(currentSimpleParentMethodSig, currentParentMethod.getValue().getNode());
						}

						if (!currentMethods.contains(currentParentMethodSig)) {
							System.out.println("already contain:" + simpleCurrentMethods + currentMethods);

							if (simpleCurrentMethods.contains(currentSimpleParentMethodSig)) {
								System.out.println("override" + currentParentMethodSig);
								MethodDeclaration currentMethodNode = null;
								String CurrentMethodkey = null;
								for (String key : currentScope.getSymbols().keySet()) {
									if (key.contains(currentSimpleParentMethodSig)) {
										CurrentMethodkey = key;
										currentMethodNode = (MethodDeclaration) currentScope.getSymbols().get(key).getNode();
									}
								}

								MethodDeclaration currentParentMethodNode = (MethodDeclaration) currentParentMethod.getValue().getNode();

								if (currentMethodNode.getModifiers().getModifiers().contains(Modifiers.Modifier.PROTECTED)) {
									if (currentParentMethodNode.getModifiers().getModifiers().contains(Modifiers.Modifier.PUBLIC)) {
										throw new Exception("protected mothed can not override public");
									}
								}

								if (currentParentMethodNode.getModifiers().getModifiers().contains(Modifiers.Modifier.FINAL)) {
									throw new Exception("can not override final method");
								}
								if (currentParentMethodNode.getModifiers().getModifiers().contains(Modifiers.Modifier.STATIC)) {
									throw new Exception("can not override static method");
								}
								if (currentMethodNode.getModifiers().getModifiers().contains(Modifiers.Modifier.STATIC)) {
									throw new Exception("static can not override instance method");
								}
								if ((currentMethodNode.getType() != null) && (currentParentMethodNode.getType() != null)) {
									if (!currentMethodNode.getType().getIdentifier().equals(currentParentMethodNode.getType().getIdentifier())) {
										throw new Exception("same method different return type");
									}

								} else {
									if ((currentMethodNode.getType() == null) != (currentParentMethodNode.getType() == null)) {
										throw new Exception("same method different return type");
									}

								}
								if (currentMethodNode.getParent() instanceof InterfaceBody) {
									System.out.println("replace:");
									currentMethods.remove(CurrentMethodkey);
									currentScope.addVisibleSymbols(currentParentMethod.getKey(), currentParentMethod.getValue());

								}
							} else {
								currentScope.addVisibleSymbols(currentParentMethod.getKey(), currentParentMethod.getValue());
							}
						}
					}
				}
			}

		}
		currentScope.listVisibleSymbols();
	}

	private String getSimpleSignature(String MethodSig) {
		return MethodSig.substring(MethodSig.lastIndexOf("."));
	}
}
