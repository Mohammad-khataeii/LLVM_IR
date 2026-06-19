# MATLAB-like Parser

Small MATLAB-like compiler that parses a source program and prints the program result.

## Build

```sh
jflex scanner.jflex
java -cp ".;java-cup-11b.jar" java_cup.Main -parser parser -symbols sym parser.cup
javac -cp ".;java-cup-11b.jar" Main.java parser.java scanner.java sym.java
```

The build regenerates `scanner.java`, `parser.java`, and `sym.java`, then compiles the Java compiler.

## Run for Output

```sh
java -cp ".;java-cup-11b.jar" Main tests/base.txt
```

This runs the base source program and prints only the final result.

## Generate LLVM IR

```sh
java -cp ".;java-cup-11b.jar" Main tests/base.txt build/base.ll
```

This writes the generated LLVM IR into `build/base.ll`.

Additional runs:

```sh
java -cp ".;java-cup-11b.jar" Main tests/full.txt
java -cp ".;java-cup-11b.jar" Main tests/extra_feature_indexing.txt
```

Variant runs for other sample files:

```sh
java -cp ".;java-cup-11b.jar" Main tests/base1.txt
java -cp ".;java-cup-11b.jar" Main tests/base2.txt
java -cp ".;java-cup-11b.jar" Main tests/full1.txt
java -cp ".;java-cup-11b.jar" Main tests/full2.txt
java -cp ".;java-cup-11b.jar" Main tests/extra1.txt
java -cp ".;java-cup-11b.jar" Main tests/extra2.txt
```

## Implemented Features

- scalar assignment
- matrix literals
- matrix element assignment
- matrix addition
- matrix multiplication
- scalar multiplication
- matrix-scalar multiplication
- matrix element access
- `for`
- nested `for`
- `if` / `else`
- `disp`

When `disp` receives a simple variable, such as `disp(D)`, the output includes the variable name followed by `=`.

## Extra Feature

The extra feature is matrix element access inside expressions, for example:

```matlab
Y = K(2,1) + K(1,2);
```
