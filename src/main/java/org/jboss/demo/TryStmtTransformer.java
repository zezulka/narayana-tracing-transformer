package org.jboss.demo;

import java.util.List;

import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;

public class TryStmtTransformer extends ModifierVisitor<Void> {

	private final String sourceName;

	public TryStmtTransformer(String sourceName) {
		this.sourceName = sourceName;
	}
	
	// for testing purposes only
	TryStmtTransformer() {
		this(null);
	}

	@Override
	public Visitable visit(TryStmt tryStmt, Void arg) {
		if (removeAllScopeResources(tryStmt)) {
			// since we've found a Scope resource, there MUST be a finishing
			// statement in the finally block!
			BlockStmt finBlock = tryStmt.getFinallyBlock()
					.orElseThrow(() -> new TransformerException(sourceName, tryStmt));
			tryStmt.getParentNode().get().walk(VariableDeclarationExpr.class, vde -> {
				if (varDeclIsSpan(vde, finBlock)) {
					vde.removeForced();
				}
					
			});
			if (finBlock.getChildNodes().isEmpty()) {
				finBlock.remove();
				if (tryStmt.getCatchClauses().isEmpty() && tryStmt.getResources().isEmpty()) {
					tryStmt.replace(tryStmt.getTryBlock());
				}
			}
		}
		return tryStmt;
	}

	/**
	 * @return true if at least on Scope resource was deleted from the try "header".
	 */
	private boolean removeAllScopeResources(TryStmt t) {
		return t.getResources()
				.removeIf(e -> e.findFirst(VariableDeclarator.class).get().getTypeAsString().equals("Scope"));
	}

	private boolean isSpanVariableDeclaration(VariableDeclarationExpr vde) {
		return vde.findFirst(VariableDeclarator.class).get().getTypeAsString().equals("Span");
	}

	/**
	 * first, we need to check whether the expression is actually a Span variable
	 * declaration (with var name <NAME>)
	 * 
	 * second, we need to make sure that a statement "<NAME>.finish()" finishing the
	 * span is present in the finally block and remove those
	 */
	private boolean varDeclIsSpan(VariableDeclarationExpr varDeclaration, BlockStmt finBlock) {
		return isSpanVariableDeclaration(varDeclaration)
				&& finBlock.getStatements().removeIf(finallyStmt -> finallyStmt.toString().contains(
						varDeclaration.findFirst(VariableDeclarator.class).get().getNameAsString() + ".finish()"));
	}
}
