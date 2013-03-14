package ca.uwaterloo.joos.checker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import ca.uwaterloo.joos.ast.Modifiers;
import ca.uwaterloo.joos.ast.decl.InterfaceDeclaration;
import ca.uwaterloo.joos.ast.decl.MethodDeclaration;
import ca.uwaterloo.joos.symboltable.Scope;
import ca.uwaterloo.joos.symboltable.TableEntry;
import ca.uwaterloo.joos.symboltable.TypeScope;

public class HierarchyChecker {
	public HierarchyBuilder hierarchyBuilder;

	public HierarchyChecker(HierarchyBuilder hierarchyBuilder) throws Exception {
		this.hierarchyBuilder = hierarchyBuilder;

	}

	public void CheckHierarchy() throws Exception {
		checkImplements();
		checkExtends();
		updateScope();
		checkOverride();
	}

	private void updateScope() {
	}

	private void checkOverride() throws Exception {

		Iterator<Entry<String, Scope>> iteratorMethodMap = hierarchyBuilder.getMethodMap().entrySet().iterator();
		while (iteratorMethodMap.hasNext()) {
			Map.Entry<String, Scope> classToMethods = (Map.Entry<String, Scope>) iteratorMethodMap.next();
			// check if the class is subclass of any class
			if (hierarchyBuilder.getHierarchyMap().containsKey(classToMethods.getKey())) {
				Map<String, List<Scope>> superClassToMethods = extractSuperClass(classToMethods.getKey());
				Set<String> superClassNames = superClassToMethods.keySet();
				// List<TableEntry> allSuperClassMethod = superClassToMethods.;
				// String currentClass = classToMethods.getKey();

				for (String superClassName : superClassNames) {
					Scope superClassMethods = hierarchyBuilder.getMethodMap().get(superClassName);
					Iterator<Entry<String, TableEntry>> methodIterator = classToMethods.getValue().getSymbols().entrySet().iterator();
					while (methodIterator.hasNext()) {
						Map.Entry<String, TableEntry> methodToAST = methodIterator.next();
						if (superClassMethods instanceof TypeScope) {
							if (methodToAST.getValue().getNode() instanceof MethodDeclaration) {
								MethodDeclaration methodDecl = (MethodDeclaration) methodToAST.getValue().getNode();
								TableEntry overrideInSuper = ((TypeScope) superClassMethods).getMethod(methodDecl);
								if ((overrideInSuper != null) && (overrideInSuper.getNode() instanceof MethodDeclaration)) {

									if (((MethodDeclaration) overrideInSuper.getNode()).getModifiers().getModifiers().contains(Modifiers.Modifier.FINAL)) {
										throw new Exception("can override final method" + overrideInSuper.getName());
									}

									if (methodDecl.getModifiers().getModifiers().contains(Modifiers.Modifier.STATIC)) {
										throw new Exception("a static method can not override an instance ");
									}
									if (((MethodDeclaration) overrideInSuper.getNode()).getModifiers().getModifiers().contains(Modifiers.Modifier.STATIC)) {
										throw new Exception("can override a static method");
									}
									System.out.println(((MethodDeclaration) overrideInSuper.getNode()).getModifiers().getModifiers() + ":/n" + methodDecl.getModifiers().getModifiers());
									// if ((((MethodDeclaration)
									// overrideInSuper.getNode()).getModifiers().getModifiers().contains(Modifier.PUBLIC)
									// |(overrideInSuper.getNode().getParent()
									// instanceof InterfaceDeclaration))&&
									// methodDecl.getModifiers().getModifiers().contains(Modifier.PROTECTED))
									// {
									if (methodDecl.getModifiers().getModifiers().contains(Modifiers.Modifier.PROTECTED)) {
										System.out.println(methodDecl.getModifiers().getModifiers().get(0).getClass());
										if (((MethodDeclaration) overrideInSuper.getNode()).getModifiers().getModifiers().contains(Modifiers.Modifier.PUBLIC) | (overrideInSuper.getNode().getParent() instanceof InterfaceDeclaration)) {
											throw new Exception("A protected method can not override a public method" + overrideInSuper.getNode() + ":" + methodDecl.getIdentifier());
										}
										// throw new
										// Exception("A protected method can not override a public method"
										// + overrideInSuper.getName());
									}
									if (methodDecl.getType() != null) {
										if (!((MethodDeclaration) overrideInSuper.getNode()).getType().getIdentifier().equals(methodDecl.getType().getIdentifier())) {

											throw new Exception("A method" + methodDecl.getIdentifier() + " must not replace a method with a different return type" + ((MethodDeclaration) overrideInSuper.getNode()).getType().getIdentifier() + methodDecl.getType().getIdentifier());
										}
									} else if (((MethodDeclaration) overrideInSuper.getNode()).getType() != null) {
										throw new Exception("A method with the same signature and different return type");
									}
									// overrideInSuper.getNode()).getType())
									break;
								}

							}
						}
					}
				}
			}
		}
	}

	private List<Scope> extractSuperClass(String currentClass) {
		Map<String, List<Scope>> superClassToMethods = null;
		if (hierarchyBuilder.getHierarchyMap().get(currentClass) != null) {
			String superName = hierarchyBuilder.getHierarchyMap().get(currentClass);
			superClassToMethods.put(superName, (List<Scope>) hierarchyBuilder.getMethodMap().values());
		}

		// System.out.println("currentClass:" + currentClass);
		if (hierarchyBuilder.getImplementMap().get(currentClass) != null) {
			System.out.println(currentClass + "implement" + hierarchyBuilder.getImplementMap().get(currentClass));
			for (String implementClass : hierarchyBuilder.getImplementMap().get(currentClass)) {
				superClassToMethods.put(implementClass, (List<Scope>) hierarchyBuilder.getMethodMap().values());
				System.out.println(currentClass + "implement" + implementClass);
			}
		}
		return superClassToMethods;
	}

	private void checkImplements() throws Exception {
		Iterator<Entry<String, List<String>>> iteratorImplement = hierarchyBuilder.getImplementMap().entrySet().iterator();
		while (iteratorImplement.hasNext()) {
			Map.Entry<String, List<String>> implementPair = (Map.Entry<String, List<String>>) iteratorImplement.next();
			List<String> interfaeces = (List<String>) implementPair.getValue();
			for (String interfaceName : interfaeces) {
				if (!hierarchyBuilder.getInterfaces().contains(interfaceName)) {
					throw new Exception(implementPair.getKey() + "implement" + implementPair.getValue() + " must be a interface");
				}
			}
			// it.remove(); // avoids a ConcurrentModificationException
		}
	}

	private void checkExtends() throws Exception {

		Iterator<Entry<String, String>> it = hierarchyBuilder.getHierarchyMap().entrySet().iterator();
		while (it.hasNext()) {
			Set<String> superClain = new HashSet<String>();
			@SuppressWarnings("rawtypes")
			Map.Entry pairs = (Map.Entry) it.next();
			superClain.add((String) pairs.getKey());

			if (superClain.contains((String) pairs.getValue())) {
				throw new Exception("extend cycle");
			} else {
				superClain.add((String) pairs.getValue());
			}
			String chain = (String) pairs.getValue();

			while (this.hierarchyBuilder.getHierarchyMap().containsKey(chain)) {
				if (superClain.contains((String) this.hierarchyBuilder.getHierarchyMap().get(chain))) {
					throw new Exception("extend cycle");
				} else {
					superClain.add(this.hierarchyBuilder.getHierarchyMap().get(chain));
				}
				chain = this.hierarchyBuilder.getHierarchyMap().get(chain);

			}
			String className = (String) pairs.getKey();
			String SUPER = (String) pairs.getValue();

			if (hierarchyBuilder.getInterfaces().contains(SUPER) && (!hierarchyBuilder.getInterfaces().contains(className))) {
				// System.out.println(hierarchyBuilder.getInterfaces());
				throw new Exception("a class" + className + " can not extend an interface" + SUPER);
			}
			if (hierarchyBuilder.getInterfaces().contains(SUPER) && (hierarchyBuilder.getClasses().contains(SUPER))) {
				throw new Exception("an interface" + className + " can not extend a class" + SUPER);
			}
			if (hierarchyBuilder.getFinals().contains(SUPER)) {
				throw new Exception("can not extend final" + SUPER);
			}

			// it.remove();
		}
	}

}
