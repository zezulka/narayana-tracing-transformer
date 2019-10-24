package org.jboss.demo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.Visitable;

import junit.framework.Assert;

/**
 * This test suite does all the checks one by one as shown in the "/Test.java" resource.
 * To avoid clumsy copying of the AST (e.g. via defining clone()), we'll just compare the
 * toString() method of the node and compare it with the expected output.
 * 
 * @author Miloslav Zezulka (mzezulka@redhat.com)
 *
 */
class TryStmtTransformerTest {
	
	@Test
	public void emptyStatement() {
		CompilationUnit s = StaticJavaParser.parse("");
		assertEquals(transform(s), "");
	}
	
	@Test
	public void emptyStatement2() {
		Statement s = StaticJavaParser.parseStatement(";");
		assertEquals(transform(s), ";");
	}
	
	@Test
	public void mainMethodAsYouveSeenItBillionTimes() {
		String input = "public static void main(String... main) {\n"
				+ "    System.out.println(\"Hello World!\");\n"
				+ "}";
		parseAndAssertUnchanged(input);
	}
	
	@Test
	public void otherStatementsInAMethod() {
		String input = "public void nonTracingStatements() {\n" + 
				"    int a, b, c;\n" + 
				"    a++;\n" + 
				"    b--;\n" + 
				"    c;\n" + 
				"}";
		parseAndAssertUnchanged(input);
	}
	
	@Test
	public void mostTrivialTryBlockWithNoTracing() {
		String input = "public void mostTrivialNonTracing() {\n" + 
				"    int a;\n" + 
				"    System.out.println(\"abcd\");\n" + 
				"    a++;\n" + 
				"    try (String s = new String(\"abcd\")) {\n" + 
				"        System.out.println(\"efgh\");\n" + 
				"    }\n" + 
				"}";
		parseAndAssertUnchanged(input);
	}
	
	@Test
	public void mostTrivialTryBlockWithFinallyWithNoTracing() {
		String input = "public void anotherMostTrivialNonTracing() {\n" + 
				"    int a;\n" + 
				"    System.out.println(\"abcd\");\n" + 
				"    a++;\n" + 
				"    try (String s = new String(\"abcd\")) {\n" + 
				"        System.out.println(\"efgh\");\n" + 
				"    } finally {\n" + 
				"        // let's pretend we can close a String\n" + 
				"        s.close();\n" + 
				"    }\n" + 
				"}";
		parseAndAssertUnchanged(input);
	}
	
	@Test
	public void mostTrivial() {
		String input = "public void mostTrivial() {\n" + 
				"    int a;\n" + 
				"    System.out.println(\"abcd\");\n" + 
				"    Span span = new DefaultSpanBuilder(a);\n" + 
				"    try (Scope s = Tracing.activateSpan(span)) {\n" + 
				"        System.out.println(\"efgh\");\n" + 
				"    } finally {\n" + 
				"        span.finish();\n" + 
				"    }\n" + 
				"}";
		String expected = "public void mostTrivial() {\n" + 
				"    int a;\n" + 
				"    System.out.println(\"abcd\");\n" + 
				"    {\n" + 
				"        System.out.println(\"efgh\");\n" + 
				"    }\n" + 
				"}";
		parseAndAssert(input, expected);
	}
	
	@Test
	public void spanDeclarationSpanningOverMultipleLines() {
		String input = "public void spanDeclarationSpanningOverMultipleLines() {\n" + 
				"    System.out.println(\"abcd\");\n" + 
				"    Span span = new DefaultSpanBuilder(a)\n" + 
				"                .tag(b)\n" + 
				"                .tag(c);\n" + 
				"    try (Scope s = Tracing.activateSpan(span)) {\n" + 
				"        System.out.println(\"efgh\");\n" + 
				"    } finally {\n" + 
				"        span.finish();\n" + 
				"    }\n" + 
				"}";
		String expected = "public void spanDeclarationSpanningOverMultipleLines() {\n" + 
				"    System.out.println(\"abcd\");\n" + 
				"    {\n" + 
				"        System.out.println(\"efgh\");\n" + 
				"    }\n" + 
				"}";
		parseAndAssert(input, expected);
	}
	
	private static void parseAndAssert(String input, String expected) {
		MethodDeclaration md = StaticJavaParser.parseMethodDeclaration(input);
		assertEquals(transform(md), expected);
	}
	
	private static void parseAndAssertUnchanged(String input) {
		parseAndAssert(input, input);
	}
	
	private static String transform(Visitable cu) {
		return cu.accept(new TryStmtTransformer(), null).toString();
	}

}
