 package org.jboss.demo;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

public class Main {

	/*
	 * Toy which outputs the modified AST (as a valid Java source) to stdout.
	 * For the expected output and further information, please consult the unit tests. 
	 */
	public static void main(String[] args) throws Exception {
		CompilationUnit cu = StaticJavaParser.parse(Main.class.getResourceAsStream("/Test.java"));
		cu.accept(new TryStmtTransformer("Test.java"), null).accept(new ImportRemover("Test.java"), null);
		System.out.println(cu);
	}

}
