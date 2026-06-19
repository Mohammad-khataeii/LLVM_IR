import java_cup.runtime.Symbol;

%%
%public
%class scanner
%cup
%line
%column
%unicode
%state MATRIX

%{
    /* I keep the inside of [ ... ] together and let the parser build the matrix */
    private final StringBuilder matrixBuffer = new StringBuilder();

    private Symbol sym(int type) {
        return new Symbol(type, yyline + 1, yycolumn + 1);
    }

    private Symbol sym(int type, Object value) {
        return new Symbol(type, yyline + 1, yycolumn + 1, value);
    }
%}

LineTerminator = \r|\n|\r\n
WhiteSpace = [ \t\f]+
Identifier = [A-Za-z_][A-Za-z0-9_]*
Integer = [0-9]+

%%

<YYINITIAL> {
    "%" [^\r\n]*                 { /* MATLAB-style comment */ }
    {WhiteSpace}                 { /* skip */ }
    {LineTerminator}+            { return sym(sym.NEWLINE); }

    /* Language keywords */
    "for"                        { return sym(sym.FOR); }
    "if"                         { return sym(sym.IF); }
    "else"                       { return sym(sym.ELSE); }
    "end"                        { return sym(sym.END); }
    "disp"                       { return sym(sym.DISP); }

    /* Comparisons and arithmetic operators */
    "=="                         { return sym(sym.EQEQ); }
    "~="                         { return sym(sym.NE); }
    ">="                         { return sym(sym.GE); }
    "<="                         { return sym(sym.LE); }
    "="                          { return sym(sym.ASSIGN); }
    ">"                          { return sym(sym.GT); }
    "<"                          { return sym(sym.LT); }
    "+"                          { return sym(sym.PLUS); }
    "-"                          { return sym(sym.MINUS); }
    "*"                          { return sym(sym.TIMES); }
    ":"                          { return sym(sym.COLON); }
    "("                          { return sym(sym.LPAREN); }
    ")"                          { return sym(sym.RPAREN); }
    ","                          { return sym(sym.COMMA); }
    ";"                          { return sym(sym.SEMI); }

    "["                          { matrixBuffer.setLength(0); yybegin(MATRIX); }

    {Integer}                    { return sym(sym.NUMBER, Integer.valueOf(yytext())); }
    {Identifier}                 { return sym(sym.ID, yytext()); }
}

<MATRIX> {
    /* Everything stays in the buffer until the closing bracket */
    "]"                          { yybegin(YYINITIAL); return sym(sym.MATRIX_LITERAL, matrixBuffer.toString()); }
    [^]                          { matrixBuffer.append(yytext()); }
}

<<EOF>>                         { return sym(sym.EOF); }
