// This class file is intended to be used as a playground for all
// possible situations under which transformation can occur.
public class A {
    public static void main(String... main) {
        System.out.println("foobar");
    }

    public void emptyStatement() {
        ;
    }

    public void nonTracingStatements() {
    	int a, b, c;
        a;
        b;
        c;
    }

    public void mostTrivialNonTracing() {
    	int a;
        System.out.println("abcd");
        Span span = new DefaultSpanBuilder(a);
        try (String s = new String("abcd")) {
            System.out.println("efgh");
        } finally {
            span.finish();
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
    
    public void f() {
        System.out.println("abcd");
        Span span = new DefaultSpanBuilder(a);
        try (String b = new String("asdf"); Scope s = Tracing.activateSpan(span) ; AnotherType c = GimmeThis.fromStaticMethod(1,2,3)) {
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
}