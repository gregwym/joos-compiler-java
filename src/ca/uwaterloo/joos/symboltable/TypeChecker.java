package ca.uwaterloo.joos.symboltable;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.decl.FieldDeclaration;
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

	public TypeChecker(SymbolTable table) {
		super(table);
		this.typeStack = new Stack<Type>();
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
			String typeName = this.getParentTypeScope().getName();
			this.pushType(new ReferenceType(typeName, node));
		} else if (node instanceof SimpleName) {
			Type nameType = ((SimpleName) node).getOriginalDeclaration().getType();
			this.pushType(nameType);
		} else if (node instanceof QualifiedName) {
			QualifiedName qualifiedName = (QualifiedName) node;
			List<String> components = qualifiedName.getComponents();

			TableEntry entry = qualifiedName.getOriginalDeclaration();
			Type type = entry.getType();
			TypeScope typeScope = entry.getTypeScope();
			for (String component : components.subList(1, components.size())) {
				if (type instanceof ArrayType) {
					if (component.equals("length")) {
						type = new PrimitiveType(Primitive.INT, node);
						break;
					} else {
						throw new Exception("Unkown ArrayType field " + component + " in " + qualifiedName.getName());
					}
				}
				entry = typeScope.resolveVariableToDecl(new SimpleName(component, node));
				if (entry != null) {
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
			this.pushType(castType);
		} else if (node instanceof InfixExpression) {
			InfixExpression infix = (InfixExpression) node;

			if (infix.getOperator().equals(InfixExpression.InfixOperator.INSTANCEOF)) {
				Type operandType = this.popType();
				// TODO: Match operand type with Type
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

			Scope currentScope = this.getCurrentScope();
			TypeScope typeScope = null;
			String signature = null;

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
					TableEntry entry = currentScope.resolveVariableToDecl(new SimpleName(components.get(i), node));
					if (entry == null) {
						currentScope = null;
						break;
					}
					currentScope = entry.getTypeScope();
				}

				// Fail to resolve as a non-static method
				if (currentScope == null) {
					String typeName = qualifiedName;
					for (i = components.size() - 1; i > 0; i--) {
						typeName = Lambda.join(components.subList(0, i), ".");
						typeName = this.getCurrentScope().resolveReferenceType(new ReferenceType(typeName, node), this.table);
						if (typeName != null) {
							currentScope = this.table.getType(typeName);
							break;
						}
					}
					if (currentScope != null) {
						String fieldName = null;
						for (; i < components.size() - 1; i++) {
							fieldName = components.get(i);
							TableEntry entry = currentScope.resolveVariableToDecl(new SimpleName(fieldName, node));
							if (entry == null) {
								currentScope = null;
								break;
							}
							currentScope = entry.getTypeScope();
						}
					}
				}

				if (currentScope == null) {
					throw new Exception("Unable to resolve method invoke prefix " + qualifiedName);
				}

				typeScope = (TypeScope) currentScope;
				signature = typeScope.signatureOfMethod(methodName, false, argTypes);
				logger.info("Method " + qualifiedName + " resolved to signature " + signature);
			} else if (invokingName instanceof SimpleName) {
				typeScope = this.getParentTypeScope();
				signature = typeScope.signatureOfMethod(invokingName.getSimpleName(), false, argTypes);
			}

			TableEntry entry = typeScope.getSymbols().get(signature);
			if (entry == null) {
				throw new Exception("Unknown method " + signature);
			}

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
				TableEntry entry = typeScope.resolveVariableToDecl(fieldName);
				if (entry == null) {
					throw new Exception("Unknown field " + fieldName.getName() + " in primary type " + primaryType.getFullyQualifiedName());
				}
				type = entry.getType();
			}
			this.pushType(type);
		} else if (node instanceof AssignmentExpression) {
			Type exprType = this.popType();
			Type lhsType = this.popType();
			if (!exprType.equals(lhsType)) {
				throw new Exception("Assigning " + lhsType.getFullyQualifiedName() + " with " + exprType.getFullyQualifiedName());
			}
			this.pushType(lhsType);
		}

		/* Type Consumers */
		else if (node instanceof VariableDeclaration) {
			if (((VariableDeclaration) node).getInitial() == null) {
				this.popType();
			} else {
				Type initType = this.popType();
				Type varType = this.popType();

				if (initType instanceof ArrayType && varType instanceof ArrayType) {
					initType = ((ArrayType) initType).getType();
					varType = ((ArrayType) varType).getType();
				}

				if (initType.getClass().equals(varType.getClass())) {
					if (initType instanceof PrimitiveType) {
						// TODO: check whether all primitive types are both way
						// assignable
					} else if (initType instanceof ReferenceType && !initType.getFullyQualifiedName().equals("__NULL__")) {
						String fullName = initType.getFullyQualifiedName();
						TypeScope typeScope = this.table.getType(fullName);
						boolean result = typeScope.isSubclassOf(varType.getFullyQualifiedName());
						if (result == false) {
							throw new Exception("Cannot initialize " + varType.getFullyQualifiedName() + " with " + initType.getFullyQualifiedName());
						}
					}
				} else if (varType instanceof ArrayType && initType.getFullyQualifiedName().equals("__NULL__")) {

				} else if (initType instanceof ArrayType && varType.getFullyQualifiedName().matches("^java.(lang.Object|lang.Cloneable|io.Serializable)$")) {

				} else {
					throw new Exception("Initialing " + varType.getFullyQualifiedName() + " with " + initType.getFullyQualifiedName());
				}
			}
		}

		if (node instanceof FieldDeclaration || node instanceof Block) {
			this.checkType--;
		}

		super.didVisit(node);
	}

}
