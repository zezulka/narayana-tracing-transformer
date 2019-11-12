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
		// the new new RootSpanBuilder catches the use case when we create a root scope which should happen
		// only in at one place of code, so this is a very subtle but needed corner case
		if (mceStr.startsWith("TracingUtils.") || mceStr.startsWith("new RootSpanBuilder")) {
			mce.removeForced();
		}
		return mce;
	}
}
