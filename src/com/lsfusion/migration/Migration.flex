package com.lsfusion.migration;

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
FIRST_ID_LETTER	= [a-zA-Z_]
NEXT_ID_LETTER = [a-zA-Z_0-9]

%%
<YYINITIAL> {
    ({LINE_WS} | {EOL})+                  { return com.intellij.psi.TokenType.WHITE_SPACE; }
    "//" [^\n\r]*                         { return COMMENTS; }
    
    // the canonical class names, then the names of files and links of a specific format, which the server accepts with a warning
    "INTEGER" | "DOUBLE" | "LONG" | "BOOLEAN" | "TBOOLEAN"
    | "DATE" | "TIME" | "DATETIME" | "ZDATETIME" | "YEAR"
    | "DATEINTERVAL" | "DATETIMEINTERVAL" | "TIMEINTERVAL" | "ZDATETIMEINTERVAL"
    | "STRING" | "NUMERIC" | "COLOR" | "JSON" | "JSONTEXT" | "XML" | "HTML" | "TSVECTOR" | "TSQUERY"
    | "FILE" | "RAWFILE" | "NAMEDFILE" | "LINK" | "RAWLINK"
    | "WORDFILE" | "IMAGEFILE" | "PDFFILE" | "VIDEOFILE" | "DBFFILE" | "EXCELFILE" | "TEXTFILE" | "CSVFILE" | "HTMLFILE" | "JSONFILE" | "XMLFILE" | "TABLEFILE"
    | "WORDLINK" | "IMAGELINK" | "PDFLINK" | "VIDEOLINK" | "DBFLINK" | "EXCELLINK" | "TEXTLINK" | "CSVLINK" | "HTMLLINK" | "JSONLINK" | "XMLLINK" | "TABLELINK"      { return PRIMITIVE_TYPE; }
                      
    "V" {DIGITS} ("." {DIGITS})*          { return VERSION; }
  
  
    ","                                   { return COMMA; }
    "."                                   { return POINT; }
    
    "->"                                  { return ARROW; }
    
    "{"                                   { return LBRACE; }
    "}"                                   { return RBRACE; }
    
    "["                                   { return LSQBR; }
    "]"                                   { return RSQBR; }
    
    "("                                   { return LBRACKET; }
    ")"                                   { return RBRACKET; }
        
    "?"                                 { return UNKNOWNCLASS; }
    
    "CLASS"                   			{ return CLASS; }
    "OBJECT"                  			{ return OBJECT; }
    "PROPERTY"                			{ return PROPERTY; }
    "TABLE"                   			{ return TABLE; }
    "NAVIGATOR"                         { return NAVIGATOR; }
    "ACTION"                            { return ACTION; }
    "CONCAT"                            { return CONCAT; }
    "STORED"                            { return STORED; }
    "FORM"                              { return FORM; }
    
    {FIRST_ID_LETTER} {NEXT_ID_LETTER}*   { return ID; }
    
    [^] { return com.intellij.psi.TokenType.BAD_CHARACTER; }
}
