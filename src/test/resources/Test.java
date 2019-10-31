package org.jboss.transformer.test;

import io.narayana.ClassWhichShouldntBeDeleted;
import io.narayana.more.pkgs.ClassWhichShouldntBeDeleted;
import io.narayana.tracing.SpanName;
import io.narayana.tracing.TagName;
import io.narayana.tracing.Tracing;
import io.narayana.tracing.Tracing.DefaultSpanBuilder;
import io.narayana.tracing.TransactionStatus;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.log.Fields;
import io.opentracing.tag.Tags;
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
        Span span = new DefaultSpanBuilder(a);
        try (Scope s = Tracing.activateSpan(span)) {
            System.out.println("efgh");
        } finally {
            span.finish();
        }
    }

    public void helperFunctionsInsideTryBody() {
        int a;
        System.out.println("abcd");
        Span span = new DefaultSpanBuilder(a);
        try (Scope s = Tracing.activateSpan(span)) {
            System.out.println("efgh");
            Tracing.addTag(TagName.ABCD, null);
            Another.staticTracingMethod(which, should, remain);
        } finally {
            span.finish();
        }
    }

    public void helperFunctionsInsideFinallyBody() {
        int a;
        System.out.println("abcd");
        Span span = new DefaultSpanBuilder(a);
        try (Scope s = Tracing.activateSpan(span)) {
            System.out.println("efgh");
        } finally {
            span.finish();
            Tracing.finishWithoutRemoval("FEDC-A987-1234");
        }
    }

    public void helperFunctionsInsideFinallyBodyTwo() {
        int a;
        System.out.println("abcd");
        Span span = new DefaultSpanBuilder(a);
        try (Scope s = Tracing.activateSpan(span)) {
            System.out.println("efgh");
        } finally {
            span.finish();
            Tracing.finishWithoutRemoval("FEDC-A987-1234");
            a++;
        }
    }

    public void spanDeclarationSpanningOverMultipleLines() {
        System.out.println("abcd");
        Span span = new DefaultSpanBuilder(a)
                    .tag(b)
                    .tag(c);
        try (Scope s = Tracing.activateSpan(span)) {
            System.out.println("efgh");
        } finally {
            span.finish();
        }
    }
    
    public void multipleResources() {
        System.out.println("abcd");
        Span span = new DefaultSpanBuilder(a);
        try (String b = new String("asdf"); Scope s = Tracing.activateSpan(span); AnotherType c = GimmeThis.fromStaticMethod(1, 2, 3)) {
            System.out.println("efgh");
        } finally {
            span.finish();
        }
    }
    
    public void finallyBlockWithNontracingStatements() {
        System.out.println("abcd");
        Span span = new DefaultSpanBuilder(a);
        try(String b = new String("asdf"); Scope s = Tracing.activateSpan(span)) {
            System.out.println("efgh");
        } finally {
            anotherStatement;
            span.finish();
            andAnotherStatement;
        }
    }

    public void tracingStatementsOutsideTryBlock() {
        int a;
        Tracing.finishWithoutRemoval("FEDC-A987-1234");
        a++;
    }
   
    public void tracingStatementsOutsideTryBlockNew() {
        int a;
        new Tracing.RootScopeBuilder().build();
        a++;
    }
}
