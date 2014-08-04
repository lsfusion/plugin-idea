package com.lsfusion.migration.lang;

import com.intellij.lexer.*;
import com.intellij.psi.tree.IElementType;
import static com.lsfusion.migration.lang.psi.MigrationTypes.*;

%%

%{
  public MigrationLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class MigrationLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode
%eof{
 return;
%eof}

////////////////////MACRO///////////////////////

EOL = \r|\n|\r\n
LINE_WS = [ \t\f]
DIGITS = [0-9]+
FIRST_ID_LETTER	= [a-zA-Z]
NEXT_ID_LETTER = [a-zA-Z_0-9]

%%
<YYINITIAL> {
    ({LINE_WS} | {EOL})+                  { return com.intellij.psi.TokenType.WHITE_SPACE; }
    "//" [^\n\r]*                         { return COMMENTS; }
    
    "INTEGER" | "DOUBLE" | "LONG" | "BOOLEAN"
    | "DATETIME" | "DATE" | "YEAR" | "TIME"
    | "WORDFILE" | "IMAGEFILE" | "PDFFILE" | "CUSTOMFILE" | "EXCELFILE"
    | "STRING" | "NUMERIC" | "COLOR"      { return PRIMITIVE_TYPE; }
                      
    "V" {DIGITS} ("." {DIGITS})*          { return VERSION; }
  
  
    ","                                   { return COMMA; }
    "."                                   { return POINT; }
    
    "->"                                  { return ARROW; }
    
    "{"                                   { return LBRACE; }
    "}"                                   { return RBRACE; }
    
    "["                                   { return LSQBR; }
    "]"                                   { return RSQBR; }
    
    "CLASS"                   			{ return CLASS; }
    "OBJECT"                  			{ return OBJECT; }
    "PROPERTY"                			{ return PROPERTY; }
    "TABLE"                   			{ return TABLE; }
    
    {FIRST_ID_LETTER} {NEXT_ID_LETTER}*   { return ID; }
    
    [^] { return com.intellij.psi.TokenType.BAD_CHARACTER; }
}