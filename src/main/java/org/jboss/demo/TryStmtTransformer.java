package org.jboss.demo;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;

public class TryStmtTransformer extends ModifierVisitor<Void> {

	private final String sourceName;
	// Use the fully qualified class name to avoid import
	// hardwired substitution for now (and maybe forever)
	private static final Expression SUBSTITUTE = StaticJavaParser.parseExpression("com.arjuna.ats.arjuna.logging.BenchmarkLogger.logMessage()");

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
					.orElseThrow(() -> new TransformerException(sourceName, tryStmt, "spans should be closed (span.finish()) in the finally block of the appropriate try statement."));
			removeAllTracingHelperCalls(tryStmt);
			tryStmt.getParentNode().get().walk(VariableDeclarationExpr.class, vde -> {
				if (isSpanVariableStatement(vde, finBlock)) {
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
		// we have to manually call the recursion here
		tryStmt.getTryBlock().walk(TryStmt.class, n -> this.visit(n, null));
		return tryStmt;
	}
	
	private void removeAllTracingHelperCalls(TryStmt t) {
		removeAllTracingHelpersForBlock(t.getTryBlock());
		t.getFinallyBlock().ifPresent(b -> removeAllTracingHelpersForBlock(b));
	}
	
    private void removeAllTracingHelpersForBlock(BlockStmt t) {
		t.walk(MethodCallExpr.class, mce -> {
			if(mce.toString().startsWith("TracingUtils.")) {
				mce.removeForced();
			}
		});
	}
	
	/**
	 * @return true if at least on Scope resource was deleted from the try "header".
	 */
	private boolean removeAllScopeResources(TryStmt t) {
		return t.getResources()
				.removeIf(e -> e.findFirst(VariableDeclarator.class).get().getTypeAsString().equals("Scope"));
	}

	/**
	 * Side effect of calling this method is that if the variable declaration is indeed
	 * of a Span, this statement is substituted with whatever is present in the SUBSTITUTE Node.
	 */
	private boolean isSpanVariableDeclaration(VariableDeclarationExpr vde) {
		boolean res = vde.findFirst(VariableDeclarator.class).get().getTypeAsString().equals("Span");
		if(res) {
			vde.replace(SUBSTITUTE);	
		}
		return res;
	}

	/**
	 * first, we need to check whether the expression is actually a Span variable
	 * declaration (with var name <NAME>)
	 * 
	 * second, we need to make sure that a statement "<NAME>.finish()" finishing the
	 * span is present in the finally block and remove those
	 */
	private boolean isSpanVariableStatement(VariableDeclarationExpr varDeclaration, BlockStmt finBlock) {
		return isSpanVariableDeclaration(varDeclaration)
				&& finBlock.getStatements().removeIf(finallyStmt -> finallyStmt.toString().contains(
						varDeclaration.findFirst(VariableDeclarator.class).get().getNameAsString()));
	}
}
