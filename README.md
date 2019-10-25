This little toy project is intended to be used for transformation of the Narayana code base but can be a basis of
any java source code parsing / transformation. It uses the JavaParser library which is still under active development
(but is already ready for quite advanced use). There is an excellent book written by the authors of the JavaParser library
which you can grab for free on their website (as of 25 Oct 2019): https://leanpub.com/javaparservisited

The main goal of the transformer is to take any opentracing code and transform it into a regular log statement. The main
reason why we do this is that we can compare performance overhead of regular logging and tracing by running benchmarks
on the various Narayana "versions".

Legend:
RES_STMT = your any regular resource declaration, e.g. BufferedReader br = new BufferedReader(new FileReader(path))
SUBSTITUTE = statement which we want to substitute the tracing functionality with (in most of the cases, this will
             be the above mentioned standard log statement), this will be passed as a String to the program
             (not as ugly as it seems!)
FIN_STMT = statement found in the appropriate finally clause (this is one of the many reasons why we cannot use
           a regex because the body of the outermost try statement can have nested "try"s in them!)

First scenario:
try(<RES_STMT>*Scope scopeName = Tracing.activateSpan(spanName);<RES_STMT>*) {
    <BODY>
} catch (...) {
    ...
} ... {
} finally {
    <FIN_STMT>*
    spanName.finish();
    <FIN_STMT>*
}

       ||
       ||
       ||
       \/

<SUBSTITUTE>
try(<RES_STMT>*) {
    <BODY>
} catch (...) {
    ...
} ... {
} finally {
    <FIN_STMT>*
}

Second scenario:
try(Scope varName = Tracing.activateSpan(spanName)) {
    <BODY>
} finally {
   spanName.finish();
}
       ||
       ||
       ||
       \/

<SUBSTITUTE>
<BODY>

The second scenario can be looked upon as an optimisation of the first, more general one because we could theoretically leave the try
block as it was but it would serve no purpose.
