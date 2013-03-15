package ca.uwaterloo.joos.symboltable;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.Modifiers;
import ca.uwaterloo.joos.ast.decl.BodyDeclaration;
import ca.uwaterloo.joos.ast.decl.FieldDeclaration;
import ca.uwaterloo.joos.ast.decl.MethodDeclaration;
import ca.uwaterloo.joos.ast.expr.Expression;
import ca.uwaterloo.joos.ast.expr.MethodInvokeExpression;
import ca.uwaterloo.joos.ast.expr.name.Name;
import ca.uwaterloo.joos.ast.expr.primary.FieldAccess;
import ca.uwaterloo.joos.ast.expr.primary.Primary;
import ca.uwaterloo.joos.ast.statement.Statement;
import ca.uwaterloo.joos.ast.type.ReferenceType;
import ca.uwaterloo.joos.ast.type.Type;

public class NameLinker extends SemanticsVisitor {

	private int linkName = 0;
	private boolean inStatic = false;

	public NameLinker(SymbolTable table) {
		super(table);
		this.linkName = 0;
		this.inStatic = false;
	}

	@Override
	public boolean visit(ASTNode node) throws Exception {
		if (node instanceof Type) {
			return false;
		} else if (node instanceof MethodInvokeExpression) {
			Primary primary = ((MethodInvokeExpression) node).getPrimary();
			if(primary != null) {
				primary.accept(this);
			}
			for(Expression expr: ((MethodInvokeExpression) node).getArguments()) {
				expr.accept(this);
			}
			return false;
		} else if (node instanceof FieldAccess) {
			((FieldAccess) node).getPrimary().accept(this);
			return false;
		} else if (this.linkName > 0 && node instanceof Name) {
			String name = ((Name) node).getName();
			Scope currentScope = this.getCurrentScope();

			// Try to resolve as local/field variable access
			TableEntry result = currentScope.resolveVariableToDecl((Name) node);

			// Check whether is accessing a non-static field within a static
			// scope
			if (this.inStatic && result != null && result.getNode() instanceof FieldDeclaration) {
				FieldDeclaration field = (FieldDeclaration) result.getNode();
				if (!field.getModifiers().containModifier(Modifiers.Modifier.STATIC)) {
					throw new Exception("Non static scope " + currentScope + " accessing static field " + name);
				}
			}

			// Try to resolve as static field access
			if (result == null) {
				String typeName = name;
				while (typeName.contains(".")) {
					typeName = typeName.substring(0, typeName.lastIndexOf('.'));
					String resolvedName = currentScope.resolveReferenceType(new ReferenceType(typeName), this.table);

					if (resolvedName != null) {
						TypeScope typeScope = this.table.getType(resolvedName);
						result = typeScope.getWithinPackage().getType(resolvedName);
					}

					if (result != null) {
						break;
					}
				}
			}

			if (result != null) {
				((Name) node).setOriginalDeclaration(result);
				logger.finer(name + " => " + result.getName() + "\tParent: " + node.getParent());
			} else {
				throw new Exception("Fail to resolve " + name + " in scope " + currentScope);
			}
		}
		return true;
	}

	@Override
	public void willVisit(ASTNode node) throws Exception {
		if (node instanceof Statement || node instanceof Expression || node instanceof FieldDeclaration) {
			if (!(node instanceof Name))
				linkName++;
		}
		if (node instanceof MethodDeclaration || node instanceof FieldDeclaration) {
			// Change the inStatic flag according to the Method and Filed
			// declaration scopes method
			Modifiers modifiers = ((BodyDeclaration) node).getModifiers();
			inStatic = modifiers.containModifier(Modifiers.Modifier.STATIC);
		}
		super.willVisit(node);
	}

	@Override
	public void didVisit(ASTNode node) throws Exception {
		if (node instanceof Statement || node instanceof Expression || node instanceof FieldDeclaration) {
			if (!(node instanceof Name))
				linkName--;
		}
		if (node instanceof MethodDeclaration || node instanceof FieldDeclaration) {
			inStatic = false;
		}
		super.didVisit(node);
	}

}
