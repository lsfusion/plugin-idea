package com.lsfusion.lang;
import com.intellij.lexer.*;
import com.intellij.psi.tree.IElementType;
import static com.lsfusion.lang.psi.LSFTypes.*;
import com.lsfusion.lang.psi.LSFTokenType;
import java.util.HashMap;

%%

%{
  public LSFLexer() {
    this((java.io.Reader)null);
  }
    
  private static final HashMap<IElementType, String> tokenDebugNames = new HashMap<>();
  static {
    tokenDebugNames.put(com.intellij.psi.TokenType.WHITE_SPACE, "White Space");
    tokenDebugNames.put(COMMENTS, "Comment");
    tokenDebugNames.put(LEX_LOGICAL_LITERAL, "Logical");
    tokenDebugNames.put(PRIMITIVE_TYPE, "Primitive Type");
    tokenDebugNames.put(LEX_STRING_LITERAL, "String");
    tokenDebugNames.put(LEX_UINT_LITERAL, "Integer");
    tokenDebugNames.put(LEX_ULONG_LITERAL, "Long");
    tokenDebugNames.put(LEX_UDOUBLE_LITERAL, "Double");
    tokenDebugNames.put(LEX_UNUMERIC_LITERAL, "Numeric");
    tokenDebugNames.put(LEX_DATE_LITERAL, "Date");
    tokenDebugNames.put(LEX_DATETIME_LITERAL, "Datetime");
    tokenDebugNames.put(LEX_TIME_LITERAL, "Time");
    tokenDebugNames.put(LEX_COLOR_LITERAL, "Color");
    tokenDebugNames.put(LEX_CODE_LITERAL, "Code");
    tokenDebugNames.put(DOLLAR, "$");
    tokenDebugNames.put(EQ_OPERAND, "==/!=");
    tokenDebugNames.put(LESS_EQUALS, "<=");
    tokenDebugNames.put(LESS, "<");
    tokenDebugNames.put(GREATER_EQUALS, ">=");
    tokenDebugNames.put(GREATER, ">");
    tokenDebugNames.put(QUESTION, "?");
    tokenDebugNames.put(MINUS, "-");
    tokenDebugNames.put(PLUS, "+");
    tokenDebugNames.put(MULT, "*");
    tokenDebugNames.put(DIV, "/");
    tokenDebugNames.put(ADDOR_OPERAND, "(+)/(-)");
    tokenDebugNames.put(SEMI, ";");
    tokenDebugNames.put(COLON, ":");
    tokenDebugNames.put(COMMA, ",");
    tokenDebugNames.put(POINT, ".");
    tokenDebugNames.put(EQUALS, "=");
    tokenDebugNames.put(PLUSEQ, "+=");
    tokenDebugNames.put(ARROW, "<-");
    tokenDebugNames.put(FOLLOWS, "=>");
    tokenDebugNames.put(LBRAC, "(");
    tokenDebugNames.put(RBRAC, ")");
    tokenDebugNames.put(LBRACE, "{");
    tokenDebugNames.put(RBRACE, "}");
    tokenDebugNames.put(LSQBR, "[");
    tokenDebugNames.put(RSQBR, "]");
    tokenDebugNames.put(ATSIGN, "@");
    tokenDebugNames.put(ATSIGN2, "@@");
    tokenDebugNames.put(FAKETWODASHES, "##");
    tokenDebugNames.put(FAKETHREEDASHES, "###");
  }
    
  public static String getTokenDebugName(IElementType elementType) {
    return tokenDebugNames.get(elementType); 
  }
%}

%public
%class LSFLexer
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
STR_LITERAL_CHAR = ("\\" .) | [^\n\r'\\]
DIGIT = [0-9]
DIGITS = [0-9]+
EDIGITS	= [0-9]*
HEX_DIGIT =	[0-9a-fA-F]
FIRST_ID_LETTER	= [a-zA-Z]
NEXT_ID_LETTER = [a-zA-Z_0-9]
CODE_LITERAL = <\{([^{}]|[\r\n]|((\{|\})+([^{}<>]|[\r\n])))*(\{|\})?\}>

%%
<YYINITIAL> {
  ({LINE_WS} | {EOL})+                  { return com.intellij.psi.TokenType.WHITE_SPACE; }
  "//" [^\n\r]*                         { return COMMENTS; }


  ("TRUE" | "FALSE")                    { return LEX_LOGICAL_LITERAL; }

    "INTEGER" | "LONG" | "NUMERIC[" {DIGITS} "," {DIGITS} "]" | "DOUBLE"
  | "DATE" | "DATETIME" | "TIME" | "YEAR"
  | "STRING[" {DIGITS} "]" | "ISTRING[" {DIGITS} "]" | "VARSTRING[" {DIGITS} "]" | "VARISTRING[" {DIGITS} "]"
  | "TEXT" | "ITEXT" | "RICHTEXT"
  | "WORDFILE" | "IMAGEFILE" | "PDFFILE" | "RAWFILE" | "FILE" | "EXCELFILE"
  | "WORDLINK" | "IMAGELINK" | "PDFLINK" | "RAWLINK"  | "LINK" | "EXCELLINK"
  | "BOOLEAN"
  | "COLOR"                             { return PRIMITIVE_TYPE; }


  "'" {STR_LITERAL_CHAR}* "'"           { return LEX_STRING_LITERAL; }

  {DIGITS}                              { return LEX_UINT_LITERAL; }
  {DIGITS}[Ll]                          { return LEX_ULONG_LITERAL; }
  {DIGITS} "." {EDIGITS}[Dd]            { return LEX_UDOUBLE_LITERAL; }
  {DIGITS} "." {EDIGITS}                { return LEX_UNUMERIC_LITERAL; }
  {DIGIT}{4} _ {DIGIT}{2} _ {DIGIT}{2}  { return LEX_DATE_LITERAL; }
  {DIGIT}{4} _ {DIGIT}{2} _ {DIGIT}{2} _ {DIGIT}{2} : {DIGIT}{2}  { return LEX_DATETIME_LITERAL; }
  {DIGIT}{2} : {DIGIT}{2}               { return LEX_TIME_LITERAL; }
  "#" {HEX_DIGIT}{6}                    { return LEX_COLOR_LITERAL; }
  {CODE_LITERAL}                        { return LEX_CODE_LITERAL; }

  "$"                                   { return DOLLAR; }

  "==" | "!="                           { return EQ_OPERAND; }
  "<="                                  { return LESS_EQUALS; }
  "<"                                   { return LESS; }
  ">="                                  { return GREATER_EQUALS; }
  ">"                                   { return GREATER; }
  
  "?"                                   { return QUESTION;}

  "-"                                   { return MINUS; }
  "+"                                   { return PLUS; }
  "*"                                   { return MULT; }
  "/"                                   { return DIV; }
  "(+)" | "(-)"                         { return ADDOR_OPERAND; }

  ";"                                   { return SEMI; }
  ":"                                   { return COLON; }
  ","                                   { return COMMA; }
  "."                                   { return POINT; }

  "="                                   { return EQUALS; }
  "+="                                  { return PLUSEQ; }
  "<-"                                  { return ARROW; }
  "=>"                                  { return FOLLOWS; }

  "("                                   { return LBRAC; }
  ")"                                   { return RBRAC; }

  "{"                                   { return LBRACE; }
  "}"                                   { return RBRACE; }

  "["                                   { return LSQBR; }
  "]"                                   { return RSQBR; }

  "@"                                   { return ATSIGN; }
  "@@"                                  { return ATSIGN2; }

  "ABSTRACT"                			{ return ABSTRACT; }
  "ACTIVATE"                			{ return ACTIVATE; }
  "ACTIVE"                			    { return ACTIVE; }
  "ATTR"                			    { return ATTR; }
  "ASK"                			        { return ASK; }
  "NATIVE"                			    { return NATIVE; }
  "ACTION"                  			{ return ACTION; }
  "AFTER"                   			{ return AFTER; }
  "GOAFTER"                   			{ return GOAFTER; }
  "AGGR"                    			{ return AGGR; }
  "NAGGR"                    			{ return NAGGR; }
  "ALL"                     			{ return ALL; }
  "AND"                     			{ return AND; }
  "APPEND"                   			{ return APPEND; }
  "APPLY"                   			{ return APPLY; }
  "AS"                      			{ return AS; }
  "ASON"                      			{ return ASON; }
  "ASYNCUPDATE"             			{ return ASYNCUPDATE; }
  "ATTACH"                  			{ return ATTACH; }
  "AUTOREFRESH"                         { return AUTOREFRESH; }
  "AUTOSET"                 			{ return AUTOSET; }
  "BACKGROUND"              			{ return BACKGROUND; }
  "BCC"                     			{ return BCC; }
  "BEFORE"                  			{ return BEFORE; }
  "BOTTOM"                  			{ return BOTTOM; }
  "BOX"                  			    { return BOX; }
  "BREAK"                   			{ return BREAK; }
  "BY"                      			{ return BY; }
  "CANCEL"                  			{ return CANCEL; }
  "CASE"                    			{ return CASE; }
  "CC"                      			{ return CC; }
  "CENTER"                  			{ return CENTER; }
  "CHANGE"                  			{ return CHANGE; }
  "CHANGECLASS"             			{ return CHANGECLASS; }
  "CHANGEABLE"                			{ return CHANGEABLE; }
  "CHANGEKEY"                 			{ return CHANGEKEY; }
  "CHANGED"                 			{ return CHANGED; }
  "CHANGEWYS"               			{ return CHANGEWYS; }
  "CHARSET"               			    { return CHARSET; }
  "CHARWIDTH"               			{ return CHARWIDTH; }
  "CHECK"                   			{ return CHECK; }
  "CHECKED"                 			{ return CHECKED; }
  "CLASS"                   			{ return CLASS; }
  "CLASSCHOOSER"                   		{ return CLASSCHOOSER; }
  "CLIENT"                   		    { return CLIENT; }
  "CLOSE"                   			{ return CLOSE; }
  "COLUMNS"                 			{ return COLUMNS; }
  "COMPLEX"                 			{ return COMPLEX; }
  "CONNECTION"                 			{ return CONNECTION; }
  "CONTEXTMENU"                			{ return CONTEXTMENU; }
  "NOHINT"                 			    { return NOHINT; }
  "CONCAT"                  			{ return CONCAT; }
  "CONFIRM"                 			{ return CONFIRM; }
  "CONSTRAINT"              			{ return CONSTRAINT; }
  "CONTAINERH"              			{ return CONTAINERH; }
  "CONTAINERV"              			{ return CONTAINERV; }
  "COLUMNS"             			    { return COLUMNS; }
  "START"             			        { return START; }
  "END"             			        { return END; }
  "STRETCH"             			    { return STRETCH; }
  "CANONICALNAME"                  		{ return CANONICALNAME; }
  "CSV"                  			    { return CSV; }
  "CUSTOM"                  			{ return CUSTOM; }
  "CYCLES"                  			{ return CYCLES; }
  "DATA"                    			{ return DATA; }
  "DBF"                    			    { return DBF; }
  "DEFAULT"                 			{ return DEFAULT; }
  "DEFAULTCOMPARE"                 	    { return DEFAULTCOMPARE; }
  "DELAY"              			        { return DELAY; }
  "DELETE"                  			{ return DELETE; }
  "DESC"                    			{ return DESC; }
  "DESIGN"                  			{ return DESIGN; }
  "DIALOG"                  			{ return DIALOG; }
  "DO"                      			{ return DO; }
  "DOC"                      			{ return DOC; }
  "DOCKED"                  			{ return DOCKED; }
  "DOCX"                    			{ return DOCX; }
  "DRAWROOT"                			{ return DRAWROOT; }
  "DRILLDOWN"                    	    { return DRILLDOWN; }
  "DROP"                    			{ return DROP; }
  "DROPCHANGED"             			{ return DROPCHANGED; }
  "DROPPED"                 			{ return DROPPED; }
  "ECHO"                    			{ return ECHO; }
  "EDIT"                    			{ return EDIT; }
  "ELSE"                    			{ return ELSE; }
  "EMAIL"                   			{ return EMAIL; }
  "END"                     			{ return END; }
  "EVAL"                    			{ return EVAL; }
  "EVENTID"                 			{ return EVENTID; }
  "EVENTS"                  			{ return EVENTS; }
  "EXCEPTLAST"              			{ return EXCEPTLAST; }
  "EXCLUSIVE"               			{ return EXCLUSIVE; }
  "EXEC"                    			{ return EXEC; }
  "EXTERNAL"                            { return EXTERNAL; }
  "NEWEXECUTOR"                    		{ return NEWEXECUTOR; }
  "EXPORT"                    			{ return EXPORT; }
  "EXTEND"                  			{ return EXTEND; }
  "FIELDS"                  			{ return FIELDS; }
  "FILE"                  			    { return FILE; }
  "FILTER"                  			{ return FILTER; }
  "FILTERGROUP"             			{ return FILTERGROUP; }
  "FILTERGROUPS"             			{ return FILTERGROUPS; }
  "FILTERS"                 			{ return FILTERS; }
  "FINALLY"                             { return FINALLY; }
  "FIRST"                   			{ return FIRST; }
  "FIXED"                   			{ return FIXED; }
  "FLOAT"                     			{ return FLOAT; }
  "FOLDER"                              { return FOLDER; }
  "FOOTER"                  			{ return FOOTER; }
  "FOR"                     			{ return FOR; }
  "FORCE"                   			{ return FORCE; }
  "FOREGROUND"              			{ return FOREGROUND; }
  "FORM"                    			{ return FORM; }
  "FORMS"                   			{ return FORMS; }
  "FORMULA"                 			{ return FORMULA; }
  "FROM"                    			{ return FROM; }
  "FULL"              			        { return FULL; }
  "GLOBAL"                              { return GLOBAL; }
  "GRID"                    			{ return GRID; }
  "GRIDBOX"                    			{ return GRIDBOX; }
  "GROUP"                   			{ return GROUP; }
  "GROUPCHANGE"                         { return GROUPCHANGE; }
  "EQUAL"                               { return EQUAL; }
  "HALIGN"                  			{ return HALIGN; }
  "HEADER"                  			{ return HEADER; }
  "HEADERS"                  			{ return HEADERS; }
  "HIDE"                    			{ return HIDE; }
  "HIDESCROLLBARS"          			{ return HIDESCROLLBARS; }
  "HIDETITLE"               			{ return HIDETITLE; }
  "HINTNOUPDATE"            			{ return HINTNOUPDATE; }
  "HINTTABLE"               			{ return HINTTABLE; }
  "HORIZONTAL"              			{ return HORIZONTAL; }
  "HTML"                    			{ return HTML; }
  "HTTP"                    			{ return HTTP; }
  "IF"                      			{ return IF; }
  "IMAGE"                   			{ return IMAGE; }
  "IMPORT"                   			{ return IMPORT; }
  "IMPOSSIBLE"              			{ return IMPOSSIBLE; }
  "IN"                      			{ return IN; }
  "INDEX"                   			{ return INDEX; }
  "INDEXED"                 			{ return INDEXED; }
  "INIT"                    			{ return INIT; }
  "INLINE"                  			{ return INLINE; }
  "INPUT"                   			{ return INPUT; }
  "INTERNAL"                            { return INTERNAL; }
  "IS"                      			{ return IS; }
  "JAVA"                  			    { return JAVA; }
  "JOIN"                    			{ return JOIN; }
  "JSON"                    			{ return JSON; }
  "KEYPRESS"                    		{ return KEYPRESS; }
  "LAST"                                { return LAST; }
  "LEFT"                    			{ return LEFT; }
  "LIKE"                   			    { return LIKE; }
  "LIMIT"                   			{ return LIMIT; }
  "LIST"                    			{ return LIST; }
  "LOCAL"                   			{ return LOCAL; }
  "LOGGABLE"                			{ return LOGGABLE; }
  "LSF"                    			    { return LSF; }
  "MANAGESESSION"           			{ return MANAGESESSION; }
  "NOMANAGESESSION"                     { return NOMANAGESESSION; }
  "MAX"                     			{ return MAX; }
  "MDB"                     			{ return MDB; }
  "LAST"                     			{ return LAST; }
  "MATERIALIZED"            			{ return MATERIALIZED; }
  "MENU"                    			{ return MENU; }
  "MEMO"                    			{ return MEMO; }
  "MESSAGE"                 			{ return MESSAGE; }
  "META"                    			{ return META; }
  "MIN"                     			{ return MIN; }
  "MODULE"                  			{ return MODULE; }
  "MOVE"                                { return MOVE; }
  "MS"                                  { return MS; }
  "MULTI"                   			{ return MULTI; }
  "NAME"                    			{ return NAME; }
  "NAMESPACE"               			{ return NAMESPACE; }
  "NAVIGATOR"               			{ return NAVIGATOR; }
  "NESTED"                     			{ return NESTED; }
  "NESTEDSESSION"              			{ return NESTEDSESSION; }
  "NEW"                     			{ return NEW; }
  "NEWEDIT"                     	    { return NEWEDIT; }
  "NEWSESSION"              			{ return NEWSESSION; }
  "NEWSQL"              			    { return NEWSQL; }
  "NEWTHREAD"              			    { return NEWTHREAD; }
  "NO"                      			{ return NO; }
  "NOCANCEL"                			{ return NOCANCEL; }
  "NOESCAPE"                			{ return NOESCAPE; }
  "NOCHANGE"                			{ return NOCHANGE; }
  "NOCONSTRAINTFILTER"                  { return NOCONSTRAINTFILTER; }
  "NOINLINE"                			{ return NOINLINE; }
  "NOHEADER"                			{ return NOHEADER; }
  "NOT"                     			{ return NOT; }
  "WAIT"                                { return WAIT; }
  "NOWAIT"                              { return NOWAIT; }
  "NULL"                    			{ return NULL; }
  "NONULL"                    			{ return NONULL; }
  "OBJECT"                  			{ return OBJECT; }
  "OBJECTS"                 			{ return OBJECTS; }
  "CONSTRAINTFILTER"              		{ return CONSTRAINTFILTER; }
  "OK"                      			{ return OK; }
  "ON"                      			{ return ON; }
  "OPTIMISTICASYNC"                     { return OPTIMISTICASYNC; }
  "OR"                      			{ return OR; }
  "ORDER"                   			{ return ORDER; }
  "OVERRIDE"                			{ return OVERRIDE; }
  "PAGESIZE"                			{ return PAGESIZE; }
  "PANEL"                   			{ return PANEL; }
  "PARENT"                  			{ return PARENT; }
  "PARTITION"               			{ return PARTITION; }
  "PASSWORD"                            { return PASSWORD; }
  "PDF"                     			{ return PDF; }
  "PERIOD"                              { return PERIOD; }    
  "PG"                                  { return PG; }    
  "POSITION"                			{ return POSITION; }
  "PREV"                    			{ return PREV; }
  "PREVIEW"                    			{ return PREVIEW; }
  "NOPREVIEW"                  			{ return NOPREVIEW; }
  "PARAMS"                   			{ return PARAMS; }
  "PRINT"                   			{ return PRINT; }
  "PRIORITY"                			{ return PRIORITY; }
  "PROPERTIES"              			{ return PROPERTIES; }
  "PROPERTY"                			{ return PROPERTY; }
  "PROPERTYDRAW"               			{ return PROPERTYDRAW; }
  "PROPORTION"              			{ return PROPORTION; }
  "QUERYCLOSE"              			{ return QUERYCLOSE; }
  "QUERYOK"                 			{ return QUERYOK; }
  "QUICKFILTER"                         { return QUICKFILTER; }
  "READ"                			    { return READ; }
  "READONLY"                			{ return READONLY; }
  "READONLYIF"              			{ return READONLYIF; }
  "RECURSION"               			{ return RECURSION; }
  "REFLECTION"                  		{ return REFLECTION; }
  "REGEXP"                  			{ return REGEXP; }
  "REMOVE"                  			{ return REMOVE; }
  "REPORTFILES"              			{ return REPORTFILES; }
  "REQUEST"                 			{ return REQUEST; }
  "REQUIRE"                 			{ return REQUIRE; }
  "RESOLVE"                 			{ return RESOLVE; }
  "RETURN"                  			{ return RETURN; }
  "RGB"                     			{ return RGB; }
  "RIGHT"                   			{ return RIGHT; }
  "ROUND"                   			{ return ROUND; }
  "ROOT"                   			    { return ROOT; }
  "RTF"                     			{ return RTF; }
  "SCROLL"                              { return SCROLL; }
  "SEEK"                    			{ return SEEK; }
  "SELECTOR"                			{ return SELECTOR; }
  "SERIALIZABLE"                        { return SERIALIZABLE; }
  "SET"                     			{ return SET; }
  "SETCHANGED"              			{ return SETCHANGED; }
  "SETDROPPED"                 			{ return SETDROPPED; }
  "SCHEDULE"              			    { return SCHEDULE; }
  "SHOW"                    			{ return SHOW; }
  "SHOWDEP"                 			{ return SHOWDEP; }
  "SHOWIF"                  			{ return SHOWIF; }
  "SHOWTYPE"                  			{ return SHOWTYPE; }
  "SINGLE"                  			{ return SINGLE; }
  "SHEET"                  			    { return SHEET; }
  "SPLITH"                  			{ return SPLITH; }
  "SPLITV"                  			{ return SPLITV; }
  "SQL"                       			{ return SQL; }
  "STEP"                    			{ return STEP; }
  "STRICT"                  			{ return STRICT; }
  "STRUCT"                  			{ return STRUCT; }
  "SUBJECT"                 			{ return SUBJECT; }
  "SUM"                     			{ return SUM; }
  "TAB"                   		        { return TAB; }
  "TABBED"                  			{ return TABBED; }
  "TABLE"                   			{ return TABLE; }
  "TEXTHALIGN"              			{ return TEXTHALIGN; }
  "TEXTVALIGN"              			{ return TEXTVALIGN; }
  "THEN"                    			{ return THEN; }
  "THREADS"                    			{ return THREADS; }
  "TO"                      			{ return TO; }
  "TODRAW"                  			{ return TODRAW; }
  "TOOLBAR"                 			{ return TOOLBAR; }
  "TOOLBARBOX"                 			{ return TOOLBARBOX; }
  "TOOLBARLEFT"                			{ return TOOLBARLEFT; }
  "TOOLBARRIGHT"               			{ return TOOLBAR; }
  "TOOLBARSYSTEM"              			{ return TOOLBARSYSTEM; }
  "TOP"                     			{ return TOP; }
  "TREE"                    			{ return TREE; }
  "TRY"                                 { return TRY; }
  "UNGROUP"                 			{ return UNGROUP; }
  "USERFILTER"                 			{ return USERFILTER; }
  "VALIGN"                  			{ return VALIGN; }
  "VALUE"                               { return VALUE; }
  "VERTICAL"                			{ return VERTICAL; }
  "VIEW"                    			{ return VIEW; }
  "WHEN"                    			{ return WHEN; }
  "WHERE"                   			{ return WHERE; }
  "WHILE"                   			{ return WHILE; }
  "WRITE"                   			{ return WRITE; }
  "WINDOW"                  			{ return WINDOW; }
  "XLS"                                 { return XLS; }
  "XLSX"                                { return XLSX; }
  "XML"                                 { return XML; }
  "XOR"                     			{ return XOR; }
  "YES"                     			{ return YES; }
  "YESNO"                     			{ return YESNO; }
  "##"                                  { return FAKETWODASHES;}
  "###"                                 { return FAKETHREEDASHES;}

  {FIRST_ID_LETTER} {NEXT_ID_LETTER}*   { return ID; } // used in isLsfIdentifierPart

  [^] { return com.intellij.psi.TokenType.BAD_CHARACTER; }
}
