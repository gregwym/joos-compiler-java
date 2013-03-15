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
import ca.uwaterloo.joos.ast.decl.ClassDeclaration;
import ca.uwaterloo.joos.ast.decl.InterfaceDeclaration;
import ca.uwaterloo.joos.ast.decl.MethodDeclaration;
import ca.uwaterloo.joos.ast.decl.TypeDeclaration;
import ca.uwaterloo.joos.ast.type.Type;
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
		currentScope.getSymbols();
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
		}

		// if (!node.fullyQualifiedName.equals("java.lang.Object")) {
		// hierarchyMap.put(node.fullyQualifiedName, "java.lang.Object");
		// }
		//
		// methodMap.put(node.fullyQualifiedName,
		// this.table.getType(node.fullyQualifiedName));
		// if
		// (node.getModifiers().getModifiers().contains(Modifiers.Modifier.FINAL))
		// {
		//
		// finals.add(node.fullyQualifiedName);
		//
		// }
		// if (node instanceof ClassDeclaration) {
		//
		// classes.add(node.fullyQualifiedName);
		// if (((ClassDeclaration) node).getSuperClass() != null) {
		//
		// ReferenceType EXTEND = (ReferenceType) ((ClassDeclaration)
		// node).getSuperClass();
		// String extendClasses = EXTEND.getFullyQualifiedName();
		//
		// hierarchyMap.put(node.fullyQualifiedName, extendClasses);
		//
		// }
		// if (node.getInterfaces().size() != 0) {
		// List<ReferenceType> IMPLENTS = node.getInterfaces();
		// List<String> implentClasses = new ArrayList<String>();
		// for (ReferenceType implent : IMPLENTS) {
		// if (implentClasses.contains(implent.getFullyQualifiedName())) {
		// throw new
		// Exception("An interface must not be mentioned more than once in the same implements clause of a class");
		// }
		// implentClasses.add(implent.getFullyQualifiedName());
		// }
		// implementMap.put(node.fullyQualifiedName, implentClasses);
		//
		// }
		// } else {
		// interfaces.add(node.fullyQualifiedName);
		// if (((InterfaceDeclaration) node).getInterfaces() != null) {
		// List<ReferenceType> EXTEND = ((InterfaceDeclaration)
		// node).getInterfaces();
		// for (ReferenceType EXTENDNAME : EXTEND) {
		// String extendClasses = EXTENDNAME.getFullyQualifiedName();
		// hierarchyMap.put(node.fullyQualifiedName, extendClasses);
		//
		// }
		//
		// }
		//
		// }

	}

	private void checkSuperCycle() throws Exception {
		Stack<TypeScope> classes = new Stack<TypeScope>();
		classes.add(currentScope);

		Stack<String> extendCycle = new Stack<String>();

		System.out.println("currentName" + ((TypeDeclaration) currentScope.getReferenceNode()).fullyQualifiedName);
		while (!classes.empty()) {
			TypeScope currentTopScope = classes.pop();
			extendCycle.add(((TypeDeclaration) currentScope.getReferenceNode()).fullyQualifiedName);

			TypeScope currentSuperScope = currentTopScope.getSuperScope();
			System.out.println("currentTopName" + ((TypeDeclaration) currentTopScope.getReferenceNode()).fullyQualifiedName);
			Map<String, TypeScope> currentInterfaceScopes = currentTopScope.getInterfaceScopes();
			ArrayList<TypeScope> currentParentScopes = new ArrayList<TypeScope>(currentInterfaceScopes.values());
			currentParentScopes.add(currentSuperScope);
			if (currentParentScopes != null) {
				addMethods(currentTopScope, currentParentScopes);
			}
			if (currentSuperScope != null) {
				String superName = ((TypeDeclaration) currentSuperScope.getReferenceNode()).fullyQualifiedName;
				System.out.println("superName" + superName);
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
					System.out.println("interfaceName:" + interfaceName);
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
		Set<String> currentMethods = currentScope.getSymbols().keySet();
		System.out.println("####" + currentScope.getName());
		// currentPrefix =
		// currentMethods.toArray().substring(0,MethodSig.lastIndexOf("."));
		Set<String> simpleCurrentMethods = new HashSet<String>();
		for (String currentMethod : currentMethods) {
			simpleCurrentMethods.add(getSimpleSignature(currentMethod));
			System.out.println("listMethod:" + currentMethod);
		}
		Map<String, ASTNode> parentMethods = new HashMap<String, ASTNode>();
		for (TypeScope parentScope : parentScopes) {
			if (parentScope != null) {
				Iterator<Entry<String, TableEntry>> parentMethodIterator = parentScope.getSymbols().entrySet().iterator();
				while (parentMethodIterator.hasNext()) {
					Map.Entry<String, TableEntry> currentParentMethod = parentMethodIterator.next();
					// System.out.println("current key" +
					// currentParentMethod.getKey());
					if (currentParentMethod.getValue().getNode() instanceof MethodDeclaration) {
						String currentParentMethodSig = currentScope.signatureOfMethod((MethodDeclaration) currentParentMethod.getValue().getNode());
						String currentSimpleParentMethodSig = getSimpleSignature(currentParentMethodSig);
						System.out.println("currentParentMethodSig"+parentScope.signatureOfMethod((MethodDeclaration) currentParentMethod.getValue().getNode()));
						if (parentMethods.keySet().contains(currentSimpleParentMethodSig)) {
							//System.out.println("!!!!!MethodSig" + currentSimpleParentMethodSig);
							MethodDeclaration existMethodNode = (MethodDeclaration) parentMethods.get(currentSimpleParentMethodSig);
							MethodDeclaration newMethodNode = (MethodDeclaration) currentParentMethod.getValue().getNode();
							// if(existMethodNode)
							if ((existMethodNode.getType() != null) && (newMethodNode.getType() != null)) {
								if (!existMethodNode.getType().getIdentifier().equals(newMethodNode.getType().getIdentifier())) {
									throw new Exception("same method different return type");
								}
							} else {
								if ((existMethodNode.getType() == null) != (newMethodNode.getType() == null)) {
									throw new Exception("same method different return type");
								}

							}
							boolean containProtected = existMethodNode.getModifiers().getModifiers().contains(Modifiers.Modifier.PROTECTED);
							boolean containPublic = newMethodNode.getModifiers().getModifiers().contains(Modifiers.Modifier.PUBLIC);
							System.out.println(existMethodNode.getModifiers().getModifiers() + ":" + newMethodNode.getModifiers().getModifiers());
							if (containProtected == containPublic) {
								throw new Exception("protected mothed can not override public");
							}
						} else {

							parentMethods.put(currentSimpleParentMethodSig, currentParentMethod.getValue().getNode());
						}
						//System.out.println("currentParentMethodSig" + currentParentMethodSig + simpleCurrentMethods.toString());
						if (simpleCurrentMethods.contains(currentSimpleParentMethodSig)) {
							System.out.println("!!!!!" +currentScope.getName()+ currentParentMethodSig);
							MethodDeclaration currentMethodNode = (MethodDeclaration) currentScope.getSymbols().get(currentScope.getName() + currentSimpleParentMethodSig).getNode();
							MethodDeclaration currentParentMethodNode = (MethodDeclaration) currentParentMethod.getValue().getNode();
							System.out.println("!!!!!222" + currentMethodNode.getModifiers().getModifiers() + currentParentMethodNode.getModifiers().getModifiers());
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
						}
					}

				}
			}

		}
	}

	private String getSimpleSignature(String MethodSig) {

		//
		return MethodSig.substring(MethodSig.lastIndexOf("."));
	}
}
