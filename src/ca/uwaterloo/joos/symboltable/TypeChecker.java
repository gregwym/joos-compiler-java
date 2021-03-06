package ca.uwaterloo.joos.symboltable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.logging.Logger;

import ca.uwaterloo.joos.Main;
import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.Modifiers;
import ca.uwaterloo.joos.ast.Modifiers.Modifier;
import ca.uwaterloo.joos.ast.decl.BodyDeclaration;
import ca.uwaterloo.joos.ast.decl.ConstructorDeclaration;
import ca.uwaterloo.joos.ast.decl.FieldDeclaration;
import ca.uwaterloo.joos.ast.decl.MethodDeclaration;
import ca.uwaterloo.joos.ast.decl.TypeDeclaration;
import ca.uwaterloo.joos.ast.decl.VariableDeclaration;
import ca.uwaterloo.joos.ast.expr.AssignmentExpression;
import ca.uwaterloo.joos.ast.expr.CastExpression;
import ca.uwaterloo.joos.ast.expr.ClassCreateExpression;
import ca.uwaterloo.joos.ast.expr.Expression;
import ca.uwaterloo.joos.ast.expr.ForInit;
import ca.uwaterloo.joos.ast.expr.InfixExpression;
import ca.uwaterloo.joos.ast.expr.InfixExpression.InfixOperator;
import ca.uwaterloo.joos.ast.expr.MethodInvokeExpression;
import ca.uwaterloo.joos.ast.expr.UnaryExpression;
import ca.uwaterloo.joos.ast.expr.UnaryExpression.UnaryOperator;
import ca.uwaterloo.joos.ast.expr.name.Name;
import ca.uwaterloo.joos.ast.expr.name.QualifiedName;
import ca.uwaterloo.joos.ast.expr.name.SimpleName;
import ca.uwaterloo.joos.ast.expr.primary.ArrayAccess;
import ca.uwaterloo.joos.ast.expr.primary.ArrayCreate;
import ca.uwaterloo.joos.ast.expr.primary.FieldAccess;
import ca.uwaterloo.joos.ast.expr.primary.LiteralPrimary;
import ca.uwaterloo.joos.ast.expr.primary.Primary;
import ca.uwaterloo.joos.ast.expr.primary.ThisPrimary;
import ca.uwaterloo.joos.ast.statement.Block;
import ca.uwaterloo.joos.ast.statement.ExpressionStatement;
import ca.uwaterloo.joos.ast.statement.ForStatement;
import ca.uwaterloo.joos.ast.statement.IfStatement;
import ca.uwaterloo.joos.ast.statement.ReturnStatement;
import ca.uwaterloo.joos.ast.statement.Statement;
import ca.uwaterloo.joos.ast.statement.WhileStatement;
import ca.uwaterloo.joos.ast.type.ArrayType;
import ca.uwaterloo.joos.ast.type.PrimitiveType;
import ca.uwaterloo.joos.ast.type.PrimitiveType.Primitive;
import ca.uwaterloo.joos.ast.type.ReferenceType;
import ca.uwaterloo.joos.ast.type.Type;
import ch.lambdaj.Lambda;

public class TypeChecker extends SemanticsVisitor {

	private static final Logger logger = Main.getLogger(TypeChecker.class);
	private Stack<Stack<Type>> typeStacks;
	private int checkType = 0;
	private boolean inStatic = false;
	private Type methodReturnType = null;

	public TypeChecker(SymbolTable table) {
		super(table);
		this.typeStacks = new Stack<Stack<Type>>();
		this.checkType = 0;
		this.inStatic = false;
//		logger.setLevel(Level.FINER);
	}

	@Override
	public boolean visit(ASTNode node) throws Exception {
		if (node instanceof TypeDeclaration) {
//			if (this.getCurrentScope().getName().startsWith("java"))
//				return false;

			// Check the existence of super zero-arg constructor
			TypeScope superScope = this.getCurrentScope().getParentTypeScope().getSuperScope();
			if (superScope != null) {
				String constructorSignature = superScope.localSignatureOfMethod(superScope.getReferenceNode().getIdentifier(), true, new ArrayList<Type>());
				if (superScope.resolveMethodToDecl(constructorSignature) == null) {
					throw new Exception("Cannot find zero-argument constructor in super scope " + superScope.getName() + " for type " + this.getCurrentScope().getName());
				}
			}
		}

		if (node instanceof ConstructorDeclaration) {
			if (!this.getCurrentScope().getParentTypeScope().getName().endsWith("." + node.getIdentifier())) {
				throw new Exception("Constructor name " + node.getIdentifier() + " does not match Type name " + this.getCurrentScope().getParentTypeScope().getName());
			}
		}

		if (node instanceof Type) {
			return false;
		} else if (node instanceof MethodInvokeExpression) {
			Primary primary = ((MethodInvokeExpression) node).getPrimary();
			if (primary != null) {
				primary.accept(this);
			}
			for (Expression expr : ((MethodInvokeExpression) node).getArguments()) {
				expr.accept(this);
			}
			return false;
		} else if (node instanceof FieldAccess) {
			((FieldAccess) node).getPrimary().accept(this);
			return false;
		}
		return super.visit(node);
	}

	@Override
	public void willVisit(ASTNode node) throws Exception {
		if (node instanceof TypeDeclaration || node instanceof Block) {
			this.pushTypeStack();
		}
		
		if (node instanceof FieldDeclaration || node instanceof Block) {
			this.checkType++;
		}
		if (node instanceof MethodDeclaration || node instanceof FieldDeclaration) {
			// Change the inStatic flag according to the Method and Filed
			// declaration scopes method
			Modifiers modifiers = ((BodyDeclaration) node).getModifiers();
			inStatic = modifiers.containModifier(Modifiers.Modifier.STATIC);
		}

		if (node instanceof MethodDeclaration) {
			this.methodReturnType = ((MethodDeclaration) node).getType();
		}
		super.willVisit(node);
	}
	
	protected Stack<Type> getCurrentTypeStack() {
		if (!this.typeStacks.isEmpty())
			return this.typeStacks.peek();
		return null;
	}
	
	protected void pushTypeStack() {
		logger.finer("Pushing new Type Stack");
		this.typeStacks.push(new Stack<Type>());
	}
	
	protected void popTypeStack() {
		Stack<Type> popping = this.typeStacks.pop();
		logger.finer("Popping Type Stack " + popping);
	}

	protected void pushType(Type type) throws Exception {
		logger.finer("Pushing " + type.getFullyQualifiedName());
		this.getCurrentTypeStack().push(type);
	}

	protected Type popType() throws Exception {
		Type type = this.getCurrentTypeStack().pop();
		logger.finer("Popping " + type.getFullyQualifiedName());
		return type;
	}

	protected Type getCurrentType() {
		Stack<Type> current = this.getCurrentTypeStack();
		if (current != null && !current.isEmpty())
			return current.peek();
		return null;
	}

	private boolean isAssignable(Type varType, Type assignType, boolean castop) throws Exception {
		// TODO MATT: Clean This
		// TODO ensure that the correct type is passed when assigning to an
		// index of an array
		if (assignType instanceof ArrayType && varType instanceof ArrayType) {
			assignType = ((ArrayType) assignType).getType();
			varType = ((ArrayType) varType).getType();
			if (varType instanceof PrimitiveType && !assignType.equals(varType)) {
				return false;
			}
		}

		if (varType.equals(assignType))
			return true;// Can assign between two same types

		if (assignType.getClass().equals(varType.getClass())) {
			// Both either Primitive or Reference types
			if (assignType instanceof PrimitiveType) {
				// Given our supported types, casts between two primitives are
				// always allowed

				if (castop)
					return true;
				// From - to primitive
				// TODO: don't allow narrowing conversions
				PrimitiveType aType = (PrimitiveType) assignType;
				PrimitiveType vType = (PrimitiveType) varType;

				if (aType.getPrimitive().equals(Primitive.INT)) {
					if (vType.getPrimitive().equals(Primitive.INT))
						return true;
					return false; // Only assignable to INT
				}

				if (aType.getPrimitive().equals(Primitive.BYTE)) {
					// Short Int long float double
					if (vType.getPrimitive().equals(Primitive.BYTE))
						return true;
					if (vType.getPrimitive().equals(Primitive.INT))
						return true;
					if (vType.getPrimitive().equals(Primitive.SHORT))
						return true;
					return false;
				}

				if (aType.getPrimitive().equals(Primitive.BOOLEAN)) {
					// Boolean
					if (vType.getPrimitive().equals(Primitive.BOOLEAN))
						return true;
					return false;
				}

				if (aType.getPrimitive().equals(Primitive.CHAR)) {
					// int long float double
					if (vType.getPrimitive().equals(Primitive.INT))
						return true;
					return false;
				}

				if (aType.getPrimitive().equals(Primitive.SHORT)) {
					// int long float double
					if (vType.getPrimitive().equals(Primitive.INT))
						return true;
					if (vType.getPrimitive().equals(Primitive.SHORT))
						return true;
					return false;
				}

			} else if (assignType instanceof ReferenceType && !assignType.getFullyQualifiedName().equals("__NULL__")) {
				String fullName = assignType.getFullyQualifiedName();
				TypeScope typeScope = this.table.getType(fullName);
				boolean result = typeScope.isSubclassOf(varType.getFullyQualifiedName());
				if (result == false) {
					return false;
				}
			}
		} else if (varType instanceof ArrayType && assignType.getFullyQualifiedName().equals("__NULL__")) {

		} else if (assignType instanceof ArrayType && varType.getFullyQualifiedName().matches("^java.(lang.Object|lang.Cloneable|io.Serializable)$")) {

		} else {
			return false;
		}
		return true;
	}

	private boolean isAccessible(boolean staticAccess, TableEntry entry, TypeScope accessingTypeScope, TypeScope currentTypeScope) throws Exception {
		TypeScope declaredTypeScope = entry.getWithinScope().getParentTypeScope();
		logger.fine("Checking accessibility static: " + staticAccess + " entry " + entry.getName() + " within type " + declaredTypeScope.getName() + " access type " + accessingTypeScope.getName() + " from type " + currentTypeScope.getName());
		BodyDeclaration fieldNode = (BodyDeclaration) entry.getNode();
		Modifiers modifiers = fieldNode.getModifiers();
		boolean isStatic = modifiers != null && fieldNode.getModifiers().containModifier(Modifier.STATIC);
		boolean isProtected = modifiers != null && fieldNode.getModifiers().containModifier(Modifier.PROTECTED);

		// Check whether is accessing static in non-static way
		if (isStatic != staticAccess) {
			throw new Exception("Cannot access " + entry.getName() + " in not matching way. staticAccess: " + staticAccess);
		}
		// Check whether has permission to access
		if (isProtected) {
			if (isStatic && !currentTypeScope.isSubclassOf(declaredTypeScope.getName())) {
				throw new Exception("Cannot access static PROTECTED " + entry.getName() + " from " + currentTypeScope.getName());
			} else if (!isStatic && !accessingTypeScope.isSubclassOf(currentTypeScope.getName())) { // ||
																									// currentTypeScope.isSubclassOf(declaredTypeScope.getName())
				throw new Exception("Cannot access PROTECTED " + entry.getName() + " from " + currentTypeScope.getName());
			} else if (!isStatic && accessingTypeScope.isSubclassOf(currentTypeScope.getName()) && !currentTypeScope.isSubclassOf(declaredTypeScope.getName())) {
				throw new Exception("Cannot access subclass PROTECTED " + entry.getName() + " from " + currentTypeScope.getName());
			}
		}
		return true;
	}

	@Override
	public void didVisit(ASTNode node) throws Exception {
		int i = 0;

		if (node instanceof TypeDeclaration || node instanceof Block) {
			this.popTypeStack();
		}
		
		if (this.checkType == 0) {
			super.didVisit(node);
			return;
		}

		logger.fine("Did visit " + node);

		/* Type Providers */
		if (node instanceof LiteralPrimary) {
			((LiteralPrimary) node).exprType = ((LiteralPrimary) node).getType();
			this.pushType(((LiteralPrimary) node).getType());
		} else if (node instanceof ThisPrimary) {
			String typeName = this.getCurrentScope().getParentTypeScope().getName();
			if (this.inStatic) {
				throw new Exception("Trying to access THIS within static scope " + this.getCurrentScope());
			}
			((ThisPrimary) node).exprType = new ReferenceType(typeName, node); 
			this.pushType(((ThisPrimary) node).exprType);
		} else if (node instanceof SimpleName) {
			Type nameType = ((SimpleName) node).getOriginalDeclaration().getType();
			((SimpleName) node).exprType = nameType;
			this.pushType(nameType);
		} else if (node instanceof QualifiedName) {
			QualifiedName qualifiedName = (QualifiedName) node;
			List<String> components = qualifiedName.getComponents();

			TableEntry entry = qualifiedName.getOriginalDeclaration();
			ASTNode entryNode = entry.getNode();
			boolean staticAccess = entryNode instanceof TypeDeclaration;

			Type type = entry.getType();
			TypeScope typeScope = entry.getTypeScope();
			TypeScope currentTypeScope = this.getCurrentScope().getParentTypeScope();
			for (String component : components.subList(1, components.size())) {
				if (type instanceof ArrayType) {
					if (component.equals("length")) {
						type = new PrimitiveType(Primitive.INT, node);
						break;
					} else {
						throw new Exception("QN: Unkown ArrayType field " + component + " in " + qualifiedName.getName());
					}
				}
				entry = typeScope.resolveVariableToDecl(new SimpleName(component, node));
				if (entry != null) {
					this.isAccessible(staticAccess, entry, typeScope, currentTypeScope);
					qualifiedName.originalDeclarations.add(entry);

					staticAccess = false;
					type = entry.getType();
					typeScope = entry.getTypeScope();
				}
			}

			entry = qualifiedName.getOriginalDeclaration();
			if (entry.getType() == type) {
				throw new Exception("Cannot resolve " + qualifiedName.getName() + " within scope " + entry.getTypeScope());
			}

			((QualifiedName) node).exprType = type;
			this.pushType(type);
		}

		/* Type Consumers and Providers */
		else if (node instanceof ClassCreateExpression) {
			ReferenceType type = ((ClassCreateExpression) node).getType();
			String typeName = type.getFullyQualifiedName();
			TypeScope typeScope = this.table.getType(typeName);
			TypeDeclaration typeDecl = (TypeDeclaration) typeScope.getReferenceNode();
			if (typeDecl.getModifiers().containModifier(Modifier.ABSTRACT)) {
				throw new Exception("Cannot instantiate ABSTRACT type " + typeName);
			}

			List<Expression> arguments = ((ClassCreateExpression) node).getArguments();
			List<Type> argTypes = new LinkedList<Type>();
			for (i = 0; i < arguments.size(); i++) {
				argTypes.add(0, this.popType());
			}

			String signature = typeScope.signatureOfMethod(type.getName().getSimpleName(), true, argTypes);
			TableEntry entry = typeScope.getSymbols().get(signature);
			if (entry == null) {
				throw new Exception("Unknown constructor " + signature);
			}

			ConstructorDeclaration constructorNode = (ConstructorDeclaration) entry.getNode();
			if (constructorNode.getModifiers().containModifier(Modifier.PROTECTED) && typeScope.getWithinPackage() != this.getCurrentScope().getParentTypeScope().getWithinPackage()) {
				throw new Exception("Cannot access constructor " + entry.getName() + " from scope " + this.getCurrentScope().getName());
			}
			
			// Save constructor fully qualified name to the ASTNode
			((ClassCreateExpression) node).fullyQualifiedName = entry.getName();

			((ClassCreateExpression) node).exprType = type;
			this.pushType(type);
		} else if (node instanceof ArrayAccess) {
			Type indexType = this.popType();
			Type arrayType = this.popType();
			if (!(arrayType instanceof ArrayType) && !indexType.getFullyQualifiedName().equals("INT")) {
				throw new Exception("Accessing array, but got type " + arrayType.getFullyQualifiedName());
			}
			((ArrayAccess) node).exprType = (ArrayType) arrayType;
			this.pushType(((ArrayType) arrayType).getType());
		} else if (node instanceof ArrayCreate) {
			Type dimType = this.popType();

			if (dimType == null) {
				throw new Exception("Dimension of array expecting INT but got NULL");
			} else if (!dimType.getFullyQualifiedName().matches("^(INT|BYTE|SHORT|CHAR)$")) {
				throw new Exception("Dimension of array expecting numeric but got " + dimType.getFullyQualifiedName());
			}
			((ArrayCreate) node).exprType = ((ArrayCreate) node).getType();
			this.pushType(((ArrayCreate) node).getType());
		} else if (node instanceof UnaryExpression) {
			Type exprType = this.popType();
			UnaryOperator operator = ((UnaryExpression) node).getOperator();
			if (operator.equals(UnaryOperator.NOT)) {
				if (!exprType.getFullyQualifiedName().equals("BOOLEAN")) {
					throw new Exception("Unary Expression NOT expecting BOOLEAN but got " + exprType.getFullyQualifiedName());
				}
				((UnaryExpression) node).exprType = exprType;
				this.pushType(exprType);
			} else if (operator.equals(UnaryOperator.MINUS)) {
				if (!(exprType instanceof PrimitiveType) || exprType.getFullyQualifiedName().equals("BOOLEAN")) {
					throw new Exception("Unary Expression MINUS expecting BYTE, CHAR, INT or SHORT but got " + exprType.getFullyQualifiedName());
				}
				((UnaryExpression) node).exprType = new PrimitiveType(Primitive.INT, node); 
				this.pushType(((UnaryExpression) node).exprType);
			}
		} else if (node instanceof CastExpression) {
			Type exprType = this.popType();
			Type castType = ((CastExpression) node).getType();

			if (this.isAssignable(castType, exprType, true) == false && this.isAssignable(exprType, castType, true) == false) {
				throw new Exception("Cannot cast " + exprType.getFullyQualifiedName() + " to " + castType.getFullyQualifiedName());
			}
			((CastExpression) node).exprType = castType;
			this.pushType(castType);
		} else if (node instanceof InfixExpression) {
			InfixExpression infix = (InfixExpression) node;

			if (infix.getOperator().equals(InfixExpression.InfixOperator.INSTANCEOF)) {
				Type operandType = this.popType();
				Type rhsType = infix.getRHS();
				if (rhsType instanceof PrimitiveType) {
					throw new Exception("Cannot instanceof a PrimitiveType");
				} else if (this.isAssignable(operandType, rhsType, false) == false && this.isAssignable(rhsType, operandType, false) == false) {
					throw new Exception("Cannot instancof " + operandType.getFullyQualifiedName() + " with " + rhsType.getFullyQualifiedName());
				}
				((InfixExpression) node).exprType = new PrimitiveType(PrimitiveType.Primitive.BOOLEAN, infix); 
				this.pushType(((InfixExpression) node).exprType);
			} else {
				Type op2Type = this.popType();
				Type op1Type = this.popType();
				Type resultType = expressionType(op1Type, op2Type, infix.getOperator());
				((InfixExpression) node).exprType = resultType;
				this.pushType(resultType);
			}
		}

		else if (node instanceof MethodInvokeExpression) {

			// Fetch all argument types
			List<Expression> arguments = ((MethodInvokeExpression) node).getArguments();
			List<Type> argTypes = new LinkedList<Type>();
			for (i = 0; i < arguments.size(); i++) {
				argTypes.add(0, this.popType());
			}

			TypeScope currentTypeScope = this.getCurrentScope().getParentTypeScope();
			Scope currentScope = this.getCurrentScope();
			TypeScope typeScope = null;
			String localSignature = null;
			boolean staticAccess = false;

			// Prepare typeScope and signature
			Primary invokingPrimary = ((MethodInvokeExpression) node).getPrimary();
			Name invokingName = ((MethodInvokeExpression) node).getName();
			if (invokingPrimary != null) {
				Type primaryType = this.popType();
				typeScope = this.table.getType(primaryType.getFullyQualifiedName());
				if (typeScope == null) {
					throw new Exception("Cannot invoke method from primary type " + primaryType.getFullyQualifiedName());
				}
				localSignature = typeScope.localSignatureOfMethod(invokingName.getSimpleName(), false, argTypes);
			} else if (invokingName instanceof QualifiedName) {
				List<String> components = ((QualifiedName) invokingName).getComponents();
				String methodName = components.get(components.size() - 1);
				String qualifiedName = ((QualifiedName) invokingName).getQualifiedName();

				for (i = 0; i < components.size() - 1; i++) {
					// Resolve as field access into currentScope
					TableEntry entry = currentScope.resolveVariableToDecl(new SimpleName(components.get(i), node));
					if (entry == null) {
						currentScope = null;
						break;
					}
					// Check whether has permission to access the field
					this.isAccessible(staticAccess, entry, currentScope.getParentTypeScope(), currentTypeScope);

					if (!(entry.getType() instanceof ReferenceType)) {
						throw new Exception("Cannot invoke method from non-reference type");
					}
					
					((QualifiedName) invokingName).originalDeclarations.add(entry);
					currentScope = entry.getTypeScope();
				}

				// Try to resolve as a static method
				if (currentScope == null) {
					staticAccess = true;
					String typeName = qualifiedName;
					// Find the TypeScope portion, start from the longest name
					for (i = components.size() - 1; i > 0; i--) {
						typeName = Lambda.join(components.subList(0, i), ".");
						typeName = this.getCurrentScope().resolveReferenceType(new ReferenceType(typeName, node), this.table);
						if (typeName != null) {
							currentScope = this.table.getType(typeName);
							break;
						}
					}
					// Resolve as static field access
					if (currentScope != null) {
						String fieldName = null;
						for (; i < components.size() - 1; i++) {
							fieldName = components.get(i);
							TableEntry entry = currentScope.resolveVariableToDecl(new SimpleName(fieldName, node));
							if (entry == null) {
								currentScope = null;
								break;
							}
							this.isAccessible(staticAccess, entry, currentScope.getParentTypeScope(), currentTypeScope);
							
							((QualifiedName) invokingName).originalDeclarations.add(entry);
							staticAccess = false;
							currentScope = entry.getTypeScope();
						}
					}
				}

				if (currentScope == null) {
					throw new Exception("Unable to resolve method invoke prefix " + qualifiedName);
				}

				typeScope = (TypeScope) currentScope;
				localSignature = typeScope.localSignatureOfMethod(methodName, false, argTypes);
				logger.finer("Method " + qualifiedName + " resolved to signature " + localSignature);
			} else if (invokingName instanceof SimpleName) {
				typeScope = this.getCurrentScope().getParentTypeScope();
				localSignature = typeScope.localSignatureOfMethod(invokingName.getSimpleName(), false, argTypes);
				if (this.inStatic) {
					throw new Exception("Cannot access to non-static method " + localSignature + " in static scope");
				}
			}

			TableEntry entry = typeScope.resolveMethodToDecl(localSignature);
			if (entry == null) {
				throw new Exception("Unknown method " + localSignature + " in scope " + typeScope.getName());
			}

			// Check method permission
			this.isAccessible(staticAccess, entry, typeScope, currentTypeScope);
			
			// Save method fully qualified name to the ASTNode
			((MethodInvokeExpression) node).fullyQualifiedName = entry.getName();
			invokingName.setOriginalDeclaration(entry);

			Type type = entry.getType();
			if (type == null) {
				((MethodInvokeExpression) node).exprType = new ReferenceType("__VOID__", node); 
				this.pushType(((MethodInvokeExpression) node).exprType);
			} else {
				((MethodInvokeExpression) node).exprType = type;
				this.pushType(type);
			}
		} else if (node instanceof FieldAccess) {
			Type primaryType = this.popType();
			Name fieldName = ((FieldAccess) node).getName();
			Type type = null;
			if (primaryType instanceof ArrayType) {
				if (fieldName.getName().equals("length")) {
					type = new PrimitiveType(Primitive.INT, node);
				} else {
					throw new Exception("Unkown ArrayType field " + fieldName.getName() + " in " + primaryType.getFullyQualifiedName());
				}
			} else {
				TypeScope typeScope = this.table.getType(primaryType.getFullyQualifiedName());
				TypeScope currentTypeScope = this.getCurrentScope().getParentTypeScope();
				TableEntry entry = typeScope.resolveVariableToDecl(fieldName);
				if (entry == null) {
					throw new Exception("Unknown field " + fieldName.getName() + " in primary type " + primaryType.getFullyQualifiedName());
				}

				this.isAccessible(false, entry, typeScope, currentTypeScope);
				fieldName.setOriginalDeclaration(entry);
				
				type = entry.getType();
			}
			((FieldAccess) node).exprType = type;
			this.pushType(type);
		} else if (node instanceof AssignmentExpression) {
			Type exprType = this.popType();
			Type varType = this.popType();

			if (this.isAssignable(varType, exprType, false) == false) {
				throw new Exception("Cannot assign " + varType.getFullyQualifiedName() + " with " + exprType.getFullyQualifiedName());
			}
			((AssignmentExpression) node).exprType = varType;
			this.pushType(varType);
		}

		/* Type Consumers */
		else if (node instanceof VariableDeclaration) {
			if (((VariableDeclaration) node).getInitial() == null) {
				this.popType();
			} else {
				Type initType = this.popType();
				Type varType = this.popType();

				if (this.isAssignable(varType, initType, false) == false) {
					throw new Exception("Cannot initialize " + varType.getFullyQualifiedName() + " with " + initType.getFullyQualifiedName());
				}
			}
			this.pushType(new ReferenceType("__VOID__", node));
		} else if (node instanceof ReturnStatement) {
			if (this.methodReturnType == null) {
				if (((ReturnStatement) node).getExpression() != null) {
					throw new Exception("Returning with value in void method");
				}
			} else if (this.isAssignable(this.methodReturnType, this.popType(), false) == false) {
				throw new Exception("Invalid Return Type");
			}
			if (this.methodReturnType != null) {
				this.pushType(this.methodReturnType);
			} else {
				this.pushType(new ReferenceType("__VOID__", node));
			}
		} else if (node instanceof IfStatement) {
			Statement statement = ((IfStatement) node).getIfStatement();
			if(statement != null && !(statement instanceof Block)) {
				if(statement.getClass().equals(ExpressionStatement.class) && ((ExpressionStatement) statement).getExpression() == null) {
					
				} else {
					this.popType();
				}
			}
			statement = ((IfStatement) node).getElseStatement();
			if(statement != null && !(statement instanceof Block)) {
				if(statement.getClass().equals(ExpressionStatement.class) && ((ExpressionStatement) statement).getExpression() == null) {
					
				} else {
					this.popType();
				}
			}
			
			Type condType = this.popType();
			if (!condType.getFullyQualifiedName().equals("BOOLEAN")) {
				throw new Exception("If statement's condition expecting BOOLEAN but got " + condType.getFullyQualifiedName());
			}
			this.pushType(new ReferenceType("__VOID__", node));
		} else if (node instanceof WhileStatement) {
			Statement statement = ((WhileStatement) node).getWhileStatement();
			if(statement != null && !(statement instanceof Block)) {
				if(statement.getClass().equals(ExpressionStatement.class) && ((ExpressionStatement) statement).getExpression() == null) {
					
				} else {
					this.popType();
				}
			}
			
			Type condType = this.popType();
			if (!condType.getFullyQualifiedName().equals("BOOLEAN")) {
				throw new Exception("While statement's condition expecting BOOLEAN but got " + condType.getFullyQualifiedName());
			}
			this.pushType(new ReferenceType("__VOID__", node));
		} else if (node instanceof ForStatement) {
			Statement statement = ((ForStatement) node).getForStatement();
			if(statement != null && !(statement instanceof Block)) {
				if(statement.getClass().equals(ExpressionStatement.class) && ((ExpressionStatement) statement).getExpression() == null) {
					
				} else {
					this.popType();
				}
			}
			
			Expression update = ((ForStatement) node).getForUpdate();
			if (update != null) {
				this.popType();
			}
			
			Expression condition = ((ForStatement) node).getForCondition();
			if (condition != null) {
				Type condType = this.popType();
				if (!condType.getFullyQualifiedName().equals("BOOLEAN")) {
					throw new Exception("For statement's condition expecting BOOLEAN but got " + condType.getFullyQualifiedName());
				}
			}
			
			ForInit init = ((ForStatement) node).getForInit();
			if (init != null) {
				this.popType();
			}
			
			this.pushType(new ReferenceType("__VOID__", node));
		} else if (node instanceof Expression) {
			Type exprType = this.popType();
			((Expression) node).exprType = exprType;
			this.pushType(exprType);
		}

		if (node instanceof FieldDeclaration || node instanceof Block) {
			this.checkType--;
		}
		if (node instanceof MethodDeclaration || node instanceof FieldDeclaration) {
			methodReturnType = null;
			inStatic = false;
		}

		super.didVisit(node);
	}

	private Type expressionType(Type op1Type, Type op2Type, InfixOperator operator) throws Exception {
		if (operator.equals(InfixOperator.MINUS) && ((op1Type.getFullyQualifiedName().equals("java.lang.String")) && (op2Type.getFullyQualifiedName().equals("java.lang.String")))) {
			throw new Exception("can not minus string two strings");

		}
		if (operator.equals(InfixOperator.PLUS) && ((op1Type.getFullyQualifiedName().equals("java.lang.String")) || (op2Type.getFullyQualifiedName().equals("java.lang.String")))) {
			if (op1Type.getFullyQualifiedName().equals("__VOID__") || op2Type.getFullyQualifiedName().equals("__VOID__")) {
				throw new Exception("can not concat string and void");
			} else {
				return new ReferenceType("java.lang.String");
			}

		}

		if (operator.equals(InfixOperator.AND) || operator.equals(InfixOperator.OR) || 
				operator.equals(InfixOperator.BAND) || operator.equals(InfixOperator.BOR)) {
			if (op1Type instanceof ReferenceType || op2Type instanceof ReferenceType) {
				throw new Exception("Invalid referrence comparasion");
			}
			
			PrimitiveType type1 = (PrimitiveType) op1Type;
			PrimitiveType type2 = (PrimitiveType) op2Type;
			if (!(type1.getPrimitive().equals(Primitive.BOOLEAN) && type2.getPrimitive().equals(Primitive.BOOLEAN))) {
				throw new Exception("Invalid operands for & | && ||");
			}
			return new PrimitiveType(Primitive.BOOLEAN);
		}
		if (operator.equals(InfixOperator.EQ) || operator.equals(InfixOperator.NEQ)) {
			if (op1Type instanceof ReferenceType && op2Type instanceof ReferenceType) {
				if (op1Type.getFullyQualifiedName().equals("__VOID__") || op2Type.getFullyQualifiedName().equals("__VOID__")) {
					throw new Exception("Equation is not allowed with VOID");
				} else if (op1Type.getFullyQualifiedName().equals("__NULL__") || op2Type.getFullyQualifiedName().equals("__NULL__")) {
					// Let it through
				} else if (!(this.isAssignable(op1Type, op2Type, false) || this.isAssignable(op2Type, op1Type, false))) {
					throw new Exception("Equation is not allowed between " + op1Type.getFullyQualifiedName() + " and " + op2Type.getFullyQualifiedName());
				}
			} else if (op1Type instanceof PrimitiveType && op2Type instanceof PrimitiveType) {
				PrimitiveType type1 = (PrimitiveType) op1Type;
				PrimitiveType type2 = (PrimitiveType) op2Type;
				if ((type1.getPrimitive().equals(Primitive.BOOLEAN) || 
						type1.getPrimitive().equals(Primitive.BOOLEAN)) && 
						!type1.getPrimitive().equals(type2.getPrimitive())) {
					throw new Exception("Equation is not allowed between " + op1Type.getFullyQualifiedName() + " and " + op2Type.getFullyQualifiedName());
				}
			} else {
				throw new Exception("Equation is not allowed between " + op1Type.getFullyQualifiedName() + " and " + op2Type.getFullyQualifiedName());
			}
			
			return new PrimitiveType(Primitive.BOOLEAN);
		}
		if (operator.equals(InfixOperator.GT) || operator.equals(InfixOperator.GEQ) || operator.equals(InfixOperator.LEQ) || operator.equals(InfixOperator.LT)) {
			if (op1Type instanceof ReferenceType || op2Type instanceof ReferenceType) {
				throw new Exception("Invalid referrence comparasion1");
			} else if (op1Type instanceof PrimitiveType && op2Type instanceof PrimitiveType) {
				PrimitiveType type1 = (PrimitiveType) op1Type;
				PrimitiveType type2 = (PrimitiveType) op2Type;
				if ((type1.getPrimitive().equals(Primitive.BOOLEAN) || 
						type1.getPrimitive().equals(Primitive.BOOLEAN)) && 
						!type1.getPrimitive().equals(type2.getPrimitive())) {
					throw new Exception("Comparison is not allowed between " + op1Type.getFullyQualifiedName() + " and " + op2Type.getFullyQualifiedName());
				}
			}
			
			return new PrimitiveType(Primitive.BOOLEAN);
		}
		if (op1Type.equals(op2Type)) {
			if (op1Type instanceof PrimitiveType && ((PrimitiveType) op1Type).getPrimitive().equals(Primitive.CHAR)) {
				return new PrimitiveType(Primitive.INT);
			}
			return op1Type;
		}

		// For now ignore ALL operations performed with differing Reference
		// types.

		if ((op1Type instanceof ReferenceType || op2Type instanceof ReferenceType) && (!operator.equals(InfixOperator.EQ))) {
			throw new Exception("Invalid non-String reference type operation");
		}

		// If the types are the same, return that type

		// Otherwise, operation between two different Primitive types
		// Always convert to type with
		if (op1Type instanceof PrimitiveType) {
			// Any operation between two primitive types allowed
			PrimitiveType type1 = (PrimitiveType) op1Type;
			PrimitiveType type2 = (PrimitiveType) op2Type;
			List<PrimitiveType> types = new ArrayList<PrimitiveType>();
			types.add(type1);
			types.add(type2);
			if (types.get(0).getPrimitive().equals(Primitive.BYTE)) {
				if (types.get(1).getPrimitive().equals(Primitive.INT))
					return types.get(1);
				if (types.get(1).getPrimitive().equals(Primitive.SHORT))
					return types.get(1);
			}

			if (types.get(0).getPrimitive().equals(Primitive.CHAR)) {
				if (types.get(1).getPrimitive().equals(Primitive.INT))
					return types.get(1);
			}
			if (types.get(0).getPrimitive().equals(Primitive.INT)) {
				if (types.get(1).getPrimitive().equals(Primitive.BYTE))
					return types.get(0);
				if (types.get(1).getPrimitive().equals(Primitive.CHAR))
					return types.get(0);
				if (types.get(1).getPrimitive().equals(Primitive.SHORT))
					return types.get(0);
			}
			if (types.get(0).getPrimitive().equals(Primitive.SHORT)) {
				if (types.get(1).getPrimitive().equals(Primitive.INT))
					return types.get(1);
				if (types.get(1).getPrimitive().equals(Primitive.BYTE))
					return types.get(0);
				if (types.get(1).getPrimitive().equals(Primitive.CHAR))
					return types.get(0);
			}

			throw new Exception("Invalid type operation between " + type1.getPrimitive() + " and " + type2.getPrimitive());
		}

		return null;
	}
}
