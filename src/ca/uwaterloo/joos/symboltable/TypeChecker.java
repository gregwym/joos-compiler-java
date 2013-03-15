package ca.uwaterloo.joos.symboltable;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

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
import ca.uwaterloo.joos.ast.expr.InfixExpression;
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
import ca.uwaterloo.joos.ast.type.ArrayType;
import ca.uwaterloo.joos.ast.type.PrimitiveType;
import ca.uwaterloo.joos.ast.type.PrimitiveType.Primitive;
import ca.uwaterloo.joos.ast.type.ReferenceType;
import ca.uwaterloo.joos.ast.type.Type;
import ch.lambdaj.Lambda;

public class TypeChecker extends SemanticsVisitor {

	private Stack<Type> typeStack;
	private int checkType = 0;
	private boolean inStatic = false;

	public TypeChecker(SymbolTable table) {
		super(table);
		this.typeStack = new Stack<Type>();
		this.checkType = 0;
		this.inStatic = false;
	}

	@Override
	public boolean visit(ASTNode node) throws Exception {
		if (node instanceof TypeDeclaration) {
			if (this.getCurrentScope().getName().contains("java"))
				return false;
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
		if (node instanceof FieldDeclaration || node instanceof Block) {
			this.checkType++;
		}
		if (node instanceof MethodDeclaration || node instanceof FieldDeclaration) {
			// Change the inStatic flag according to the Method and Filed
			// declaration scopes method
			Modifiers modifiers = ((BodyDeclaration) node).getModifiers();
			inStatic = modifiers.containModifier(Modifiers.Modifier.STATIC);
		}
		super.willVisit(node);
	}

	protected void pushType(Type type) throws Exception {
		logger.info("Pushing " + type.getFullyQualifiedName());
		this.typeStack.push(type);
	}

	protected Type popType() throws Exception {
		Type type = this.typeStack.pop();
		logger.info("Poping " + type.getFullyQualifiedName());
		return type;
	}

	protected Type getCurrentType() {
		if (!this.typeStack.isEmpty())
			return this.typeStack.peek();
		return null;
	}
	
	private boolean isAssignable(Type varType, Type assignType) throws Exception {
		if (assignType instanceof ArrayType && varType instanceof ArrayType) {
			assignType = ((ArrayType) assignType).getType();
			varType = ((ArrayType) varType).getType();
		}

		if (assignType.getClass().equals(varType.getClass())) {
			if (assignType instanceof PrimitiveType) {
				// TODO: check whether all primitive types are both way
				// assignable
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
		logger.info("Checking accessibility static: " + staticAccess + " entry " + entry.getName() + 
				" within type " + declaredTypeScope.getName() + " access type " + accessingTypeScope.getName() + 
				" from type " + currentTypeScope.getName());
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
			} 
			else if (!isStatic && !accessingTypeScope.isSubclassOf(currentTypeScope.getName())) { //  || currentTypeScope.isSubclassOf(declaredTypeScope.getName())
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

		if (this.checkType == 0) {
			super.didVisit(node);
			return;
		}

		logger.info("Did visit " + node);

		/* Type Providers */
		if (node instanceof LiteralPrimary) {
			this.pushType(((LiteralPrimary) node).getType());
		} else if (node instanceof ThisPrimary) {
			String typeName = this.getCurrentScope().getParentTypeScope().getName();
			if (this.inStatic) {
				throw new Exception("Trying to access THIS within static scope " + this.getCurrentScope());
			}
			this.pushType(new ReferenceType(typeName, node));
		} else if (node instanceof SimpleName) {
			Type nameType = ((SimpleName) node).getOriginalDeclaration().getType();
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
					
					staticAccess = false;
					type = entry.getType();
					typeScope = entry.getTypeScope();
				}
			}

			entry = qualifiedName.getOriginalDeclaration();
			if (entry.getType() == type) {
				throw new Exception("Cannot resolve " + qualifiedName.getName() + " within scope " + entry.getTypeScope());
			}

			this.pushType(type);
		}

		/* Type Consumers and Providers */
		else if (node instanceof ClassCreateExpression) {
			ReferenceType type = ((ClassCreateExpression) node).getType();
			String typeName = type.getFullyQualifiedName();
			TypeScope typeScope = this.table.getType(typeName);

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
			if(constructorNode.getModifiers().containModifier(Modifier.PROTECTED) &&
					typeScope.getWithinPackage() != this.getCurrentScope().getParentTypeScope().getWithinPackage()) {
				throw new Exception("Cannot access constructor " + entry.getName() + " from scope " + this.getCurrentScope().getName());
			}

			this.pushType(type);
		} else if (node instanceof ArrayAccess) {
			Type indexType = this.popType();
			Type arrayType = this.popType();
			if (!(arrayType instanceof ArrayType) && !indexType.getFullyQualifiedName().equals("INT")) {
				throw new Exception("Accessing array, but got type " + arrayType.getFullyQualifiedName());
			}
			this.pushType(((ArrayType) arrayType).getType());
		} else if (node instanceof ArrayCreate) {
			Type dimType = this.popType();

			if (dimType == null) {
				throw new Exception("Dimension of array expecting INT but got NULL");
			} else if (!dimType.getFullyQualifiedName().equals("INT")) {
				throw new Exception("Dimension of array expecting INT but got " + dimType.getFullyQualifiedName());
			}
			this.pushType(((ArrayCreate) node).getType());
		} else if (node instanceof UnaryExpression) {
			Type exprType = this.popType();
			UnaryOperator operator = ((UnaryExpression) node).getOperator();
			if (operator.equals(UnaryOperator.NOT) && !exprType.getFullyQualifiedName().equals("BOOLEAN")) {
				throw new Exception("Unary Expression NOT expecting BOOLEAN but got " + exprType.getFullyQualifiedName());
			} else {
				this.pushType(exprType);
			}

			if (operator.equals(UnaryOperator.MINUS)) {
				if (!(exprType instanceof PrimitiveType) || exprType.getFullyQualifiedName().equals("BOOLEAN")) {
					throw new Exception("Unary Expression MINUS expecting BYTE, CHAR, INT or SHORT but got " + exprType.getFullyQualifiedName());
				}
				this.pushType(new PrimitiveType(Primitive.INT, node));
			}
		} else if (node instanceof CastExpression) {
			Type exprType = this.popType();
			Type castType = ((CastExpression) node).getType();
			// TODO: Check whether is valid casting;
			if(this.isAssignable(castType, exprType) == false && this.isAssignable(exprType, castType) == false) {
				throw new Exception("Cannot cast " + exprType.getFullyQualifiedName() + " to " + castType.getFullyQualifiedName());
			}
			this.pushType(castType);
		} else if (node instanceof InfixExpression) {
			InfixExpression infix = (InfixExpression) node;

			if (infix.getOperator().equals(InfixExpression.InfixOperator.INSTANCEOF)) {
				Type operandType = this.popType();
				Type rhsType = infix.getRHS();
				// TODO: Match operand type with Type
				if(rhsType instanceof PrimitiveType) {
					throw new Exception("Cannot instanceof a PrimitiveType");
				} else if(this.isAssignable(operandType, rhsType) == false && this.isAssignable(rhsType, operandType) == false) {
					throw new Exception("Cannot instancof " + operandType.getFullyQualifiedName() + " with " + rhsType.getFullyQualifiedName());
				}
				this.pushType(new PrimitiveType(PrimitiveType.Primitive.BOOLEAN, infix));
			} else {
				Type op2Type = this.popType();
				Type op1Type = this.popType();
				// TODO: Check operands type
				this.pushType(op1Type);
			}
			// else {
			// throw new Exception("Infix trying to " +
			// infix.getOperator().name() + " " +
			// op1Type.getFullyQualifiedName() + " with " +
			// op2Type.getFullyQualifiedName());
			// }
		} else if (node instanceof MethodInvokeExpression) {

			// Fetch all argument types
			List<Expression> arguments = ((MethodInvokeExpression) node).getArguments();
			List<Type> argTypes = new LinkedList<Type>();
			for (i = 0; i < arguments.size(); i++) {
				argTypes.add(0, this.popType());
			}

			TypeScope currentTypeScope = this.getCurrentScope().getParentTypeScope();
			Scope currentScope = this.getCurrentScope();
			TypeScope typeScope = null;
			String signature = null;
			boolean staticAccess = false;

			// Prepare typeScope and signature
			Primary invokingPrimary = ((MethodInvokeExpression) node).getPrimary();
			Name invokingName = ((MethodInvokeExpression) node).getName();
			if (invokingPrimary != null) {
				Type primaryType = this.popType();
				typeScope = this.table.getType(primaryType.getFullyQualifiedName());
				signature = typeScope.signatureOfMethod(invokingName.getSimpleName(), false, argTypes);
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
							
							staticAccess = false;
							currentScope = entry.getTypeScope();
						}
					}
				}

				if (currentScope == null) {
					throw new Exception("Unable to resolve method invoke prefix " + qualifiedName);
				}

				typeScope = (TypeScope) currentScope;
				signature = typeScope.signatureOfMethod(methodName, false, argTypes);
				logger.finer("Method " + qualifiedName + " resolved to signature " + signature);
			} else if (invokingName instanceof SimpleName) {
				staticAccess = this.inStatic;
				typeScope = this.getCurrentScope().getParentTypeScope();
				signature = typeScope.signatureOfMethod(invokingName.getSimpleName(), false, argTypes);
			}

			TableEntry entry = typeScope.getSymbols().get(signature);
			if (entry == null) {
				throw new Exception("Unknown method " + signature);
			}
			
			// Check method permission
			this.isAccessible(staticAccess, entry, typeScope, currentTypeScope);

			Type type = entry.getType();
			if (type == null) {
				this.pushType(new ReferenceType("__VOID__", node));
			} else {
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
				
				type = entry.getType();
			}
			this.pushType(type);
		} else if (node instanceof AssignmentExpression) {
			Type exprType = this.popType();
			Type varType = this.popType();
			
			if(this.isAssignable(varType, exprType) == false) {
				throw new Exception("Cannot assign " + varType.getFullyQualifiedName() + " with " + exprType.getFullyQualifiedName());
			}
			this.pushType(varType);
		}

		/* Type Consumers */
		else if (node instanceof VariableDeclaration) {
			if (((VariableDeclaration) node).getInitial() == null) {
				this.popType();
			} else {
				Type initType = this.popType();
				Type varType = this.popType();

				if(this.isAssignable(varType, initType) == false) {
					throw new Exception("Cannot initialize " + varType.getFullyQualifiedName() + " with " + initType.getFullyQualifiedName());
				}
			}
		}

		if (node instanceof FieldDeclaration || node instanceof Block) {
			this.checkType--;
		}
		if (node instanceof MethodDeclaration || node instanceof FieldDeclaration) {
			inStatic = false;
		}
		
		super.didVisit(node);
	}

}
