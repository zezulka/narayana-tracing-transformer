package org.jboss.transformer.test;

import io.narayana.ClassWhichShouldntBeDeleted;
import io.narayana.more.pkgs.ClassWhichShouldntBeDeleted;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import com.arjuna.ats.arjuna.ObjectType;
import com.arjuna.ats.arjuna.StateManager;

// This class file is intended to be used as a playground for all
// possible situations under which transformation can occur.
public class A {

    public static void main(String... main) {
        System.out.println("Hello World!");
    }

    public void emptyStatement() {
        ;
    }

    public void nonTracingStatements() {
        int a, b, c;
        a++;
        b--;
        c;
    }

    public void mostTrivialNonTracing() {
        int a;
        System.out.println("abcd");
        a++;
        try (String s = new String("abcd")) {
            System.out.println("efgh");
        }
    }

    public void anotherMostTrivialNonTracing() {
        int a;
        System.out.println("abcd");
        a++;
        try (String s = new String("abcd")) {
            System.out.println("efgh");
        } finally {
            // let's pretend we can close a String
            s.close();
        }
    }

    public void mostTrivial() {
        int a;
        System.out.println("abcd");
        com.arjuna.ats.arjuna.logging.BenchmarkLogger.logMessage();
        {
            System.out.println("efgh");
        }
    }

    public void helperFunctionsInsideTryBody() {
        int a;
        System.out.println("abcd");
        com.arjuna.ats.arjuna.logging.BenchmarkLogger.logMessage();
        {
            System.out.println("efgh");
            Another.staticTracingMethod(which, should, remain);
        }
    }

    public void helperFunctionsInsideFinallyBody() {
        int a;
        System.out.println("abcd");
        com.arjuna.ats.arjuna.logging.BenchmarkLogger.logMessage();
        {
            System.out.println("efgh");
        }
    }

    public void helperFunctionsInsideFinallyBodyTwo() {
        int a;
        System.out.println("abcd");
        com.arjuna.ats.arjuna.logging.BenchmarkLogger.logMessage();
        try {
            System.out.println("efgh");
        } finally {
            a++;
        }
    }

    public void spanDeclarationSpanningOverMultipleLines() {
        System.out.println("abcd");
        com.arjuna.ats.arjuna.logging.BenchmarkLogger.logMessage();
        {
            System.out.println("efgh");
        }
    }

    public void multipleResources() {
        System.out.println("abcd");
        com.arjuna.ats.arjuna.logging.BenchmarkLogger.logMessage();
        try (String b = new String("asdf");
            AnotherType c = GimmeThis.fromStaticMethod(1, 2, 3)) {
            System.out.println("efgh");
        }
    }

    public void finallyBlockWithNontracingStatements() {
        System.out.println("abcd");
        com.arjuna.ats.arjuna.logging.BenchmarkLogger.logMessage();
        try (String b = new String("asdf")) {
            System.out.println("efgh");
        } finally {
            anotherStatement;
            andAnotherStatement;
        }
    }

    public void tracingStatementsOutsideTryBlock() {
        int a;
        a++;
    }

    public void tracingStatementsOutsideTryBlockNew() {
        int a;
        a++;
    }
}
