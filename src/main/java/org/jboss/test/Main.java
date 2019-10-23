 package org.jboss.test;

import java.io.File;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.visitor.ModifierVisitor;

public class Main {

	public static void main(String[] args) throws Exception {
		CompilationUnit cu = StaticJavaParser.parse(Main.class.getResourceAsStream("/Test.java"));
		ModifierVisitor<?> mv = new TryTransformer();
		mv.visit(cu, null);
		System.out.println(cu);
	}

}
