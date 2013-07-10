package com.simpleplugin;
import com.intellij.lexer.*;
import com.intellij.psi.tree.IElementType;
import static com.simpleplugin.psi.LSFTypes.*;

%%

%{
  public LSFLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class LSFLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode
%eof{ return;
%eof}

EOL="\r"|"\n"|"\r\n"
LINE_WS=[\ \t\f]
WHITE_SPACE=({LINE_WS}|{EOL})+
COMMENTS=("//")[^\r\n]*
FIRST_ID_LETTER	= ([a-zA-Z])
NEXT_ID_LETTER = ([a-zA-Z_0-9])
ID = {FIRST_ID_LETTER} {NEXT_ID_LETTER}*
DIGITS = ([0-9])+
DECIMAL_INTEGER_LITERAL = {DIGITS}
STR_LITERAL_CHAR = [a-zA-Z]
QUOTED_LITERAL = '\'' {STR_LITERAL_CHAR}* '\''
PRIMITIVE_TYPE  = 'INTEGER' | 'DOUBLE' | 'LONG'

%%
<YYINITIAL> {
  {WHITE_SPACE}                { return com.intellij.psi.TokenType.WHITE_SPACE; }

  {COMMENTS}               { return COMMENTS; }
  ";"                          { return SEMI; }
  ","                      { return COMMA; }
  ":"                     { return COLUMN; }
  "CLASS"                      { return CLASS; }
  "ABSTRACT"                   { return ABSTRACT; }
  "("                      { return LBRAC; }
  ")"                      { return RBRAC; }
  "="                     { return EQUALS; }
  "<"                       { return LESS; }
  ">"                    { return GREATER; }
  "<="                { return LESS_EQUALS; }
  ">="             { return GREATER_EQUALS; }
  "(+)"                      { return BRSUM; }
  "(-)"                    { return BRMINUS; }
  "IF"                         { return IF; }
  "OR"                         { return OR; }
  "XOR"                        { return XOR; }
  "AND"                        { return AND; }
  "NOT"                        { return NOT; }
  "=="                  { return CMPEQUALS; }
  "!="               { return CMPNOTEQUALS; }
  "IS"                         { return IS; }
  "AS"                         { return AS; }
  "+"                        { return SUM; }
  "-"                      { return MINUS; }
  "*"                       { return MULT; }
  "["                      { return LSQBR; }
  {DECIMAL_INTEGER_LITERAL}    { return DECIMAL_INTEGER_LITERAL; }
  "]"                      { return RSQBR; }
  {QUOTED_LITERAL}             { return QUOTED_LITERAL; }
  "MULTI"                      { return MULTI; }
  "JOIN"                       { return JOIN; }
  "OVERRIDE"                   { return OVERRIDE; }
  "EXCLUSIVE"                  { return EXCLUSIVE; }
  "THEN"                       { return THEN; }
  "ELSE"                       { return ELSE; }
  "SUM"                        { return GSUM; }
  "MAX"                        { return MAX; }
  "MIN"                        { return MIN; }
  "CASE"                       { return CASE; }
  "WHEN"                       { return WHEN; }
  "PARTITION"                  { return PARTITION; }
  "PREV"                       { return PREV; }
  "UNGROUP"                    { return UNGROUP; }
  "PROPORTION"                 { return PROPORTION; }
  "STRICT"                     { return STRICT; }
  "ROUND"                      { return ROUND; }
  "LIMIT"                      { return LIMIT; }
  "BY"                         { return BY; }
  "ORDER"                      { return ORDER; }
  "DESC"                       { return DESC; }
  "WINDOW"                     { return WINDOW; }
  "EXCEPTLAST"                 { return EXCEPTLAST; }
  "RECURSION"                  { return RECURSION; }
  "STEP"                       { return STEP; }
  "CYCLES"                     { return CYCLES; }
  "YES"                        { return YES; }
  "NO"                         { return NO; }
  "IMPOSSIBLE"                 { return IMPOSSIBLE; }
  "STRUCT"                     { return STRUCT; }
  {PRIMITIVE_TYPE}             { return PRIMITIVE_TYPE; }
  "CONCAT"                     { return CONCAT; }
  "CHANGED"                    { return CHANGED; }
  "SET"                        { return SET; }
  "DROPPED"                    { return DROPPED; }
  "SETCHANGED"                 { return SETCHANGED; }
  "DROPCHANGED"                { return DROPCHANGED; }
  "DROPSET"                    { return DROPSET; }
  "DATA"                       { return DATA; }
  "SESSION"                    { return SESSION; }
  "CHECKED"                    { return CHECKED; }
  "FORMULA"                    { return FORMULA; }
  "AGGR"                       { return AGGR; }
  "EQUAL"                      { return EQUAL; }
  "GROUP"                      { return GROUP; }
  "WHERE"                      { return WHERE; }
  {ID}                         { return ID; }


  [^] { return com.intellij.psi.TokenType.BAD_CHARACTER; }
}
