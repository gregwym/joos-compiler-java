/**
 * 
 */
package ca.uwaterloo.joos.weeder;

import ca.uwaterloo.joos.ast.ASTNode;
import ca.uwaterloo.joos.ast.FileUnit;
import ca.uwaterloo.joos.ast.body.TypeBody;
import ca.uwaterloo.joos.ast.decl.ClassDeclaration;
import ca.uwaterloo.joos.ast.decl.TypeDeclaration;
import ca.uwaterloo.joos.ast.visitor.ASTVisitor;
import ca.uwaterloo.joos.weeder.Weeder.WeedException;

/**
 * @author Greg Wang
 * 
 */
public class TypeDeclarationChecker extends ASTVisitor {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ca.uwaterloo.joos.ast.visitor.ASTVisitor#willVisit(ca.uwaterloo.joos.
	 * ast.ASTNode)
	 */
	@Override
	public void willVisit(ASTNode node) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ca.uwaterloo.joos.ast.visitor.ASTVisitor#visit(ca.uwaterloo.joos.ast.
	 * ASTNode)
	 */
	@Override
	public boolean visit(ASTNode node) throws Exception {
		if (node instanceof TypeDeclaration) {
			if (node instanceof ClassDeclaration) {
				TypeDeclaration typeDecl = (TypeDeclaration) node;
				TypeBody typeBody = typeDecl.getBody();
				if (typeBody.getConstructors().size() < 1) {
					throw new WeedException("Class body must contain at least one constructor");
				}
			}

			ASTNode parent = node.getParent();
			while (parent != null && !(parent instanceof FileUnit))
				parent = parent.getParent();
			if (parent != null) {
				String filename = parent.getIdentifier();
				if (filename.matches(".+\\.java")) {
					filename = filename.replaceFirst("[.][^.]+$", "");
				}

				if (!filename.equals(node.getIdentifier()))
					throw new WeedException("Type declaration name does not match the filename");

			}
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ca.uwaterloo.joos.ast.visitor.ASTVisitor#didVisit(ca.uwaterloo.joos.ast
	 * .ASTNode)
	 */
	@Override
	public void didVisit(ASTNode node) {

	}

}
