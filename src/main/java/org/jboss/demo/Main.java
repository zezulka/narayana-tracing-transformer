 package org.jboss.demo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

public class Main {

	public static void main(String[] args) {
		for(String fPath : args) {
			CompilationUnit cu;
			try {
				File f = new File(fPath);
				cu = StaticJavaParser.parse(f);
				cu.accept(new TryStmtTransformer(fPath), null)
				  .accept(new ImportRemover(fPath), null);
				PrintWriter w = new PrintWriter(f, "UTF-8");
				w.write(cu.toString());
				w.close();	
			} catch (FileNotFoundException e) {
				throw new RuntimeException(String.format("Could not find the file '%s'.", fPath), e);
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		}
	}

}
