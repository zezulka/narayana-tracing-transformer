package org.jboss.demo;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import static org.junit.jupiter.api.Assertions.*;

// Basically a copy of the main in sources. We run all the transformations and check for the result.
class AllModifiersTest {

	@Test
	public void runEverything() {
		String srcName = "Test.java";
		CompilationUnit cu = StaticJavaParser.parse(Main.class.getResourceAsStream("/" + srcName));
		cu.accept(new TryStmtTransformer(srcName), null).accept(new ImportRemover(srcName), null);
		String transformed = cu.toString();
		String expected;
		try(Scanner s = new Scanner(Main.class.getResourceAsStream("/TestTransformed.java"))) {
			expected = s.useDelimiter("\\Z").next() + "\n";
			assertEquals(expected, transformed);
		}
	}
}
