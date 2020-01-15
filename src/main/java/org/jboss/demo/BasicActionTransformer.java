package org.jboss.demo;

import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;

public class BasicActionTransformer extends ModifierVisitor<Void> {

	private final String sourceName;

	public BasicActionTransformer(String sourceName) {
		this.sourceName = sourceName;
	}

	@Override
	public Visitable visit(MethodCallExpr mce, Void arg) {
		String mceStr = mce.toString();
		if (mceStr.startsWith("TracingUtils.")) {
			mce.removeForced();
		}
		return mce;
	}
}
