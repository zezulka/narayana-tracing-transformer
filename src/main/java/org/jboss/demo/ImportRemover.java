package org.jboss.demo;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.visitor.ModifierVisitor;

/*
 * Remove all the unnecessary imports related to tracing.
 * Getting rid of unused imports is not only good practice in general,
 * but in this way, we will can be even more certain that the transformation
 * went well if the transformed Narayana compiles successfully.
 */
public class ImportRemover extends ModifierVisitor<Void> {
    private final String sourceName;
	
	public ImportRemover(String sourceName) {
 		this.sourceName = sourceName;
 	}
	
	// for testing purposes only
	ImportRemover() {
		this(null);
	}
	
	@Override
	public Node visit(ImportDeclaration n, Void arg) {
		String iName = n.getNameAsString();
		if((iName.startsWith("io.narayana.tracing") || iName.startsWith("io.opentracing")) && !n.remove()) {
			throw new RuntimeException(String.format("Could not remove import %s in file %s.", iName, sourceName));
		}
		return n;
	}
}
