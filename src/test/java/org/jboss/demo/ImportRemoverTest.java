package org.jboss.demo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.visitor.Visitable;

class ImportRemoverTest {

	@Test
	void noop() {
		parseAndAssertUnchanged("");
	}
	
	@Test
	void testNarayanaTracingImport() {
		String input = "import io.narayana.tracing.SpanName;";
		String expected = "";
		parseAndAssert(input, expected);
	}
	
	@Test
	void testNarayanaImport() {
		String input = "import io.narayana.ClassOutsideTheTracingPackage;";
		parseAndAssertUnchanged(input);
	}
	
	@Test
	void testOpentracingImport() {
		String input = "import io.opentracing.Span;";
		String expected = "";
		parseAndAssert(input, expected);
	}
	
	@Test
	void testRegularImport() {
		String input = "import java.sql.Date;";
		parseAndAssertUnchanged(input);
	}
	
	@Test
	void testMixedImport() {
		String input = "import java.sql.Date;\nimport io.opentracing.Span;";
		String expected = "import java.sql.Date;";
		parseAndAssert(input, expected);
	}

	private static void parseAndAssert(String input, String expected) {
		// we cannot use parseImport (and ImportDeclaration)
		// directly since the javaparser
		// refuses to remove the import statement if there is no
		// root parent above it
		CompilationUnit id = StaticJavaParser.parse(input);
		assertEquals(expected, transform(id).replace("\n", ""));
	}

	private static void parseAndAssertUnchanged(String input) {
		parseAndAssert(input, input);
	}

	private static String transform(Visitable v) {
		return v.accept(new ImportRemover(), null).toString();
	}

}
