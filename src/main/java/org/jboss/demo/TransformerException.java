package org.jboss.demo;

import com.github.javaparser.ast.Node;

/**
 * Unrecoverable exception which occurs during any part of the source code transformation.
 * @author Miloslav Zezulka
 *
 */
public class TransformerException extends RuntimeException {
	
    public TransformerException(String sourceName, Node exceptionPlace) {
    	super(String.format("Could not parse the given file %s. Place where the exception occurred:\n\n%s", sourceName, exceptionPlace));
    }
}
