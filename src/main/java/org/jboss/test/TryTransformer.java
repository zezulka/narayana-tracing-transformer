package org.jboss.test;

import java.util.Optional;

import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;

public class TryTransformer extends ModifierVisitor<Void> {
	@Override
	public Visitable visit(TryStmt n, Void arg) {
		super.visit(n, arg);
		int noResPre = n.getResources().size();
		n.getResources().removeIf(e -> {
			VariableDeclarationExpr vde = (VariableDeclarationExpr) e;
			VariableDeclarator vd = vde.findFirst(VariableDeclarator.class).get();
			return vd.getTypeAsString().equals("Scope");
		});
		Optional<BlockStmt> fin = n.getFinallyBlock();
		if(n.getResources().size() < noResPre) {
			final String scopeVarName;
			for(VariableDeclarationExpr vde : n.getParentNode().get().findAll(VariableDeclarationExpr.class)) {
				if(vde.findFirst(VariableDeclarator.class).get().getTypeAsString().equals("Span")) {
					scopeVarName = vde.findFirst(SimpleName.class).get().asString();
					BlockStmt b = fin.get();
					b.getStatements().removeIf(e -> e.toString().contains(scopeVarName + ".finish()"));
					vde.remove();
					break;
				}
			}
		}
		if(fin.isPresent() && fin.get().getChildNodes().isEmpty()) {
			fin.get().remove();
		}
		if(!fin.isPresent() && n.getCatchClauses().isEmpty() && n.getResources().isEmpty()) {
			n.replace(n.getTryBlock());
		}
		return n;
	}
}
