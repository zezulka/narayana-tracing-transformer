package org.jboss.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.Visitable;

public class BasicActionTransformerTest {

	@Test
	public void basic() {
		String input = "public void tracingStatementsOutsideTryBlock() {\n" + 
				"    int a;\n" + 
				"    TracingUtils.finishWithoutRemoval(\"FEDC-A987-1234\");\n" + 
				"    a++;\n" + 
				"}";
		String expected = "public void tracingStatementsOutsideTryBlock() {\n" + 
				"    int a;\n" + 
				"    a++;\n" + 
				"}";
		parseAndAssert(input, expected);
	}
	
	@Test
	public void basicNew() {
		String input = "public void tracingStatementsOutsideTryBlock() {\n" + 
				"    int a;\n" + 
				"    TracingUtils.start(\"FEDC-A987-1234\");\n" + 
				"    a++;\n" + 
				"}";
		String expected = "public void tracingStatementsOutsideTryBlock() {\n" + 
				"    int a;\n" + 
				"    a++;\n" + 
				"}";
		parseAndAssert(input, expected);
	}

	public void parseAndAssert(String input, String expected) {
		MethodDeclaration md = StaticJavaParser.parseMethodDeclaration(input);
		assertEquals(expected, transform(md));
	}
	
	private static String transform(Visitable v) {
		return v.accept(new BasicActionTransformer("Test.java"), null).toString();
	}
}
