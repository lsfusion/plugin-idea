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
    tokenDebugNames.put(LEX_T_LOGICAL_LITERAL, "TLogical");
    tokenDebugNames.put(PRIMITIVE_TYPE, "Primitive Type");
    tokenDebugNames.put(LEX_STRING_LITERAL, "String");
    tokenDebugNames.put(LEX_RAW_STRING_LITERAL, "Raw String");
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
  }
    
  public static String getTokenDebugName(IElementType elementType) {
    return tokenDebugNames.get(elementType); 
  }

  private boolean wasStringPart = false;
  private boolean startedWithID = false;
  private char rawLiteralPrefixChar;
  private int depth = 0;
%}

%public
%class LSFLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode
%eofval{
 return null;
%eofval}

////////////////////MACRO///////////////////////

EOL = \r|\n|\r\n
LINE_WS = [ \t\f]
STR_LITERAL_CHAR = ("\\" .) | [^'\\]
DIGIT = [0-9]
DIGITS = [0-9]+
EDIGITS	= [0-9]*
HEX_DIGIT =	[0-9a-fA-F]
FIRST_ID_LETTER	= [a-zA-Z]
NEXT_ID_LETTER = [a-zA-Z_0-9]
CODE_LITERAL = <\{([^{}]|[\r\n]|((\{|\})+([^{}<>]|[\r\n])))*(\{|\})?\}>

ID_LITERAL = {FIRST_ID_LETTER} {NEXT_ID_LETTER}*
NEXTID_LITERAL = {NEXT_ID_LETTER}+

RAW_STR_LITERAL_CHAR = [^']
RAW_STR_PREFIX_CHAR = [^a-zA-Z_0-9 \n\t'+*,=<>(\[\]{}()#]

INTERVAL_TYPE = "DATE" | "TIME" | "DATETIME" | "ZDATETIME"

%state META_LITERAL
%state STRING_LITERAL
%state RAW_STRING_LITERAL
%state INTERPOLATION_BLOCK
%state MULTILINE_COMMENT

%%
<YYINITIAL> {
  ({LINE_WS} | {EOL})+                  { return com.intellij.psi.TokenType.WHITE_SPACE; }
  "//" [^\n\r]*                         { return COMMENTS; }
  "/*"                                  { yybegin(MULTILINE_COMMENT); }


  ("TRUE" | "FALSE")                    { return LEX_LOGICAL_LITERAL; }
  ("TTRUE" | "TFALSE")                    { return LEX_T_LOGICAL_LITERAL; }

    "INTEGER" | "LONG" | "NUMERIC" ("[" {DIGITS} "," {DIGITS} "]")? | "DOUBLE"
  | "DATE" | "DATETIME" | "DATETIME[" [0-6] "]" | "TIME" | "TIME[" [0-6] "]" | "YEAR" | "ZDATETIME" | "ZDATETIME[" [0-6] "]" | "INTERVAL" ("["{INTERVAL_TYPE}"]")
  | "BPSTRING" | "BPISTRING" | "STRING" | "ISTRING"
  | "BPSTRING[" {DIGITS} "]" | "BPISTRING[" {DIGITS} "]" | "STRING[" {DIGITS} "]" | "ISTRING[" {DIGITS} "]"
  | "TEXT" | "RICHTEXT" | "HTMLTEXT"
  | "WORDFILE" | "IMAGEFILE" | "PDFFILE" | "VIDEOFILE" | "DBFFILE" | "RAWFILE" | "FILE" | "EXCELFILE" | "TEXTFILE" | "CSVFILE" | "HTMLFILE" | "JSONFILE" | "XMLFILE" | "TABLEFILE" | "NAMEDFILE"
  | "WORDLINK" | "IMAGELINK" | "PDFLINK" | "VIDEOLINK" | "DBFLINK" | "RAWLINK" | "LINK" | "EXCELLINK" | "TEXTLINK" | "CSVLINK" | "HTMLLINK" | "JSONLINK" | "XMLLINK" | "TABLELINK"
  | "BOOLEAN" | "TBOOLEAN"
  | "TSQUERY" | "TSVECTOR"
  | "COLOR"                             { return PRIMITIVE_TYPE; }


  {DIGITS}                              { return LEX_UINT_LITERAL; }
  {DIGITS}[Ll]                          { return LEX_ULONG_LITERAL; }
  {DIGITS} "." {EDIGITS}[Dd]            { return LEX_UDOUBLE_LITERAL; }
  {DIGITS} "." {EDIGITS}                { return LEX_UNUMERIC_LITERAL; }
  {DIGIT}{4} _ {DIGIT}{2} _ {DIGIT}{2}  { return LEX_DATE_LITERAL; }
  {DIGIT}{4} _ {DIGIT}{2} _ {DIGIT}{2} _ {DIGIT}{2} : {DIGIT}{2} (: {DIGIT}{2})?  { return LEX_DATETIME_LITERAL; }
  {DIGIT}{2} : {DIGIT}{2} (: {DIGIT}{2})?  { return LEX_TIME_LITERAL; }
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
  "ACTIONS"                  			{ return ACTIONS; }
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
  "AUTO"                                { return AUTO; }
  "AUTOREFRESH"                         { return AUTOREFRESH; }
  "AUTOSET"                 			{ return AUTOSET; }
  "BACKGROUND"              			{ return BACKGROUND; }
  "BCC"                     			{ return BCC; }
  "BEFORE"                  			{ return BEFORE; }
  "BODY"                  			    { return BODY; }
  "BODYPARAMHEADERS"                    { return BODYPARAMHEADERS; }
  "BODYPARAMNAMES"                      { return BODYPARAMNAMES; }
  "BODYURL"                  			{ return BODYURL; }
  "BOTTOM"                  			{ return BOTTOM; }
  "BOX"                  			    { return BOX; }
  "BREAK"                   			{ return BREAK; }
  "BY"                      			{ return BY; }
  "CANCEL"                  			{ return CANCEL; }
  "CASE"                    			{ return CASE; }
  "CC"                      			{ return CC; }
  "CATCH"                  			    { return CATCH; }
  "CALENDAR"                            { return CALENDAR; }
  "CENTER"                  			{ return CENTER; }
  "CHANGE"                  			{ return CHANGE; }
  "CHANGECLASS"             			{ return CHANGECLASS; }
  "CHANGEABLE"                			{ return CHANGEABLE; }
  "CHANGEKEY"                 			{ return CHANGEKEY; }
  "CHANGED"                 			{ return CHANGED; }
  "CHANGEMOUSE"                 		{ return CHANGEMOUSE; }
  "CHANGEWYS"               			{ return CHANGEWYS; }
  "CHARSET"               			    { return CHARSET; }
  "CHARWIDTH"               			{ return CHARWIDTH; }
  "CHECK"                   			{ return CHECK; }
  "CHECKED"                 			{ return CHECKED; }
  "CLASS"                   			{ return CLASS; }
  "CLASSCHOOSER"                   		{ return CLASSCHOOSER; }
  "CLASSES"                   		    { return CLASSES; }
  "CLIENT"                   		    { return CLIENT; }
  "CLOSE"                   			{ return CLOSE; }
  "COLUMN"                 			    { return COLUMN; }
  "COLUMNS"                 			{ return COLUMNS; }
  "COMPLEX"                 			{ return COMPLEX; }
  "CONNECTION"                 			{ return CONNECTION; }
  "CONTEXTMENU"                			{ return CONTEXTMENU; }
  "CONTINUE"                			{ return CONTINUE; }
  "NOHINT"                 			    { return NOHINT; }
  "COLLAPSE"                  			{ return COLLAPSE; }
  "CONCAT"                  			{ return CONCAT; }
  "CONFIG"                 			    { return CONFIG; }
  "CONFIRM"                 			{ return CONFIRM; }
  "CONSTRAINT"              			{ return CONSTRAINT; }
  "CONTAINER"              			    { return CONTAINER; }
  "CONTAINERH"              			{ return CONTAINERH; }
  "CONTAINERV"              			{ return CONTAINERV; }
  "COOKIES"             			    { return COOKIES; }
  "COOKIESTO"             			    { return COOKIESTO; }
  "STRETCH"             			    { return STRETCH; }
  "CANONICALNAME"                  		{ return CANONICALNAME; }
  "CSV"                  			    { return CSV; }
  "CUSTOM"                  			{ return CUSTOM; }
  "CYCLES"                  			{ return CYCLES; }
  "DATA"                    			{ return DATA; }
  "DB"                    			    { return DB; }
  "DBF"                    			    { return DBF; }
  "DEFAULT"                 			{ return DEFAULT; }
  "DEFAULTCOMPARE"                 	    { return DEFAULTCOMPARE; }
  "DELAY"              			        { return DELAY; }
  "DELETE"                  			{ return DELETE; }
  "DESC"                    			{ return DESC; }
  "DESIGN"                  			{ return DESIGN; }
  "DIALOG"                  			{ return DIALOG; }
  "DISABLE"                  			{ return DISABLE; }
  "DISABLEIF"                  			{ return DISABLEIF; }
  "DO"                      			{ return DO; }
  "DOC"                      			{ return DOC; }
  "DOCKED"                  			{ return DOCKED; }
  "DOCX"                    			{ return DOCX; }
  "DOWN"                    			{ return DOWN; }
  "DRAWROOT"                			{ return DRAWROOT; }
  "DRILLDOWN"                    	    { return DRILLDOWN; }
  "DROP"                    			{ return DROP; }
  "DROPCHANGED"             			{ return DROPCHANGED; }
  "DROPPED"                 			{ return DROPPED; }
  "ECHO"                    			{ return ECHO; }
  "EDIT"                    			{ return EDIT; }
  "ELSE"                    			{ return ELSE; }
  "EMAIL"                   			{ return EMAIL; }
  "EMBEDDED"                   			{ return EMBEDDED; }
  "END"                     			{ return END; }
  "ERROR"                			    { return ERROR; }
  "ESCAPE"                			    { return ESCAPE; }
  "EVAL"                    			{ return EVAL; }
  "EVENTID"                 			{ return EVENTID; }
  "EVENTS"                  			{ return EVENTS; }
  "EXCLUSIVE"               			{ return EXCLUSIVE; }
  "EXEC"                    			{ return EXEC; }
  "EXPAND"                    			{ return EXPAND; }
  "EXTERNAL"                            { return EXTERNAL; }
  "NEWEXECUTOR"                    		{ return NEWEXECUTOR; }
  "EXPORT"                    			{ return EXPORT; }
  "EXTEND"                  			{ return EXTEND; }
  "EXTID"                  			    { return EXTID; }
  "EXTKEY"                  			{ return EXTKEY; }
  "EXTNULL"                  			{ return EXTNULL; }
  "FIELDS"                  			{ return FIELDS; }
  "FILTER"                  			{ return FILTER; }
  "FILTERBOX"                  			{ return FILTERBOX; }
  "FILTERCONTROLS"                  	{ return FILTERCONTROLS; }
  "FILTERGROUP"             			{ return FILTERGROUP; }
  "FILTERGROUPS"             			{ return FILTERGROUPS; }
  "FILTERS"                 			{ return FILTERS; }
  "FINALLY"                             { return FINALLY; }
  "FIRST"                   			{ return FIRST; }
  "FIXED"                   			{ return FIXED; }
  "FLEX"                   			    { return FLEX; }
  "FLOAT"                     			{ return FLOAT; }
  "FOCUSED"                             { return FOCUSED; }
  "FOLDER"                              { return FOLDER; }
  "FOOTER"                  			{ return FOOTER; }
  "FOR"                     			{ return FOR; }
  "FOREGROUND"              			{ return FOREGROUND; }
  "FORM"                    			{ return FORM; }
  "FORMEXTID"                    		{ return FORMEXTID; }
  "FORMS"                   			{ return FORMS; }
  "FORMULA"                 			{ return FORMULA; }
  "FROM"                    			{ return FROM; }
  "FULL"              			        { return FULL; }
  "GET"                                 { return GET; }
  "GLOBAL"                              { return GLOBAL; }
  "GRID"                    			{ return GRID; }
  "GRIDBOX"                    			{ return GRIDBOX; }
  "GROUP"                   			{ return GROUP; }
  "GROUPCHANGE"                         { return GROUPCHANGE; }
  "EQUAL"                               { return EQUAL; }
  "HALIGN"                  			{ return HALIGN; }
  "HEADER"                  			{ return HEADER; }
  "HEADERS"                  			{ return HEADERS; }
  "HEADERSTO"                  			{ return HEADERSTO; }
  "HIDE"                    			{ return HIDE; }
  "HIDESCROLLBARS"          			{ return HIDESCROLLBARS; }
  "HIDETITLE"               			{ return HIDETITLE; }
  "HINT"            			        { return HINT; }
  "HINTNOUPDATE"            			{ return HINTNOUPDATE; }
  "HINTTABLE"               			{ return HINTTABLE; }
  "HORIZONTAL"              			{ return HORIZONTAL; }
  "HOVER"              			        { return HOVER; }
  "HTML"                    			{ return HTML; }
  "HTTP"                    			{ return HTTP; }
  "IF"                      			{ return IF; }
  "IMAGE"                   			{ return IMAGE; }
  "IMPORT"                   			{ return IMPORT; }
  "IMPOSSIBLE"              			{ return IMPOSSIBLE; }
  "IN"                      			{ return IN; }
  "INDEX"                   			{ return INDEX; }
  "INDEXED"                 			{ return INDEXED; }
  "INFO"                    			{ return INFO; }
  "INIT"                    			{ return INIT; }
  "INLINE"                  			{ return INLINE; }
  "INPUT"                   			{ return INPUT; }
  "INTERNAL"                            { return INTERNAL; }
  "INTERVAL"                            { return INTERVAL; }
  "IS"                      			{ return IS; }
  "ISCLASS"                      		{ return ISCLASS; }
  "JAVA"                  			    { return JAVA; }
  "JOIN"                    			{ return JOIN; }
  "JSON"                    			{ return JSON; }
  "JSONTEXT"                    		{ return JSONTEXT; }
  "KEY"                    		        { return KEY; }
  "KEYPRESS"                    		{ return KEYPRESS; }
  "LAST"                                { return LAST; }
  "LAZY"                                { return LAZY; }
  "LEFT"                    			{ return LEFT; }
  "LIKE"                   			    { return LIKE; }
  "LIMIT"                   			{ return LIMIT; }
  "LIST"                    			{ return LIST; }
  "LOCAL"                   			{ return LOCAL; }
  "LOCALASYNC"                   		{ return LOCALASYNC; }
  "LOG"                			        { return LOG; }
  "LOGGABLE"                			{ return LOGGABLE; }
  "LSF"                    			    { return LSF; }
  "MANAGESESSION"           			{ return MANAGESESSION; }
  "NOMANAGESESSION"                     { return NOMANAGESESSION; }
  "MAP"                     			{ return MAP; }
  "MATCH"                     			{ return MATCH; }
  "MAX"                     			{ return MAX; }
  "MATERIALIZED"            			{ return MATERIALIZED; }
  "MEASURE"                    			{ return MEASURE; }
  "MEASURES"                    		{ return MEASURES; }
  "MENU"                    			{ return MENU; }
  "MEMO"                    			{ return MEMO; }
  "MESSAGE"                 			{ return MESSAGE; }
  "META"                    			{ return META; }
  "MIN"                     			{ return MIN; }
  "MODULE"                  			{ return MODULE; }
  "MOVE"                                { return MOVE; }
  "MOUSE"                               { return MOUSE; }
  "MS"                                  { return MS; }
  "MULTI"                   			{ return MULTI; }
  "NAME"                    			{ return NAME; }
  "NAMESPACE"               			{ return NAMESPACE; }
  "NAVIGATOR"               			{ return NAVIGATOR; }
  "NESTED"                     			{ return NESTED; }
  "NESTEDSESSION"              			{ return NESTEDSESSION; }
  "NEW"                     			{ return NEW; }
  "NEWCONNECTION"                     	{ return NEWCONNECTION; }
  "NEWEDIT"                     	    { return NEWEDIT; }
  "NEWSESSION"              			{ return NEWSESSION; }
  "NEWSQL"              			    { return NEWSQL; }
  "NEWTHREAD"              			    { return NEWTHREAD; }
  "NO"                      			{ return NO; }
  "NOCANCEL"                			{ return NOCANCEL; }
  "NOCOMPLEX"                 			{ return NOCOMPLEX; }
  "NODEFAULT"                			{ return NODEFAULT; }
  "NOEXTID"                			    { return NOEXTID; }
  "NOESCAPE"                			{ return NOESCAPE; }
  "NOFLEX"                			    { return NOFLEX; }
  "NOCHANGE"                			{ return NOCHANGE; }
  "NOCONSTRAINTFILTER"                  { return NOCONSTRAINTFILTER; }
  "NOENCODE"                            { return NOENCODE; }
  "NOIMAGE"                			    { return NOIMAGE; }
  "NOINLINE"                			{ return NOINLINE; }
  "NOHEADER"                			{ return NOHEADER; }
  "NOSELECT"                			{ return NOSELECT; }
  "NOSETTINGS"                     	    { return NOSETTINGS; }
  "NOSTICKY"                     	    { return NOSTICKY; }
  "NOT"                     			{ return NOT; }
  "WAIT"                                { return WAIT; }
  "NOWAIT"                              { return NOWAIT; }
  "NULL"                    			{ return NULL; }
  "NONULL"                    			{ return NONULL; }
  "OBJECT"                  			{ return OBJECT; }
  "OBJECTS"                 			{ return OBJECTS; }
  "CONSTRAINTFILTER"              		{ return CONSTRAINTFILTER; }
  "OFFSET"              		        { return OFFSET; }
  "OK"                      			{ return OK; }
  "ON"                      			{ return ON; }
  "OPTIMISTICASYNC"                     { return OPTIMISTICASYNC; }
  "OPTIONS"                             { return OPTIONS; }
  "OR"                      			{ return OR; }
  "ORDER"                   			{ return ORDER; }
  "ORDERS"                   			{ return ORDERS; }
  "OVERRIDE"                			{ return OVERRIDE; }
  "PAGESIZE"                			{ return PAGESIZE; }
  "PANEL"                   			{ return PANEL; }
  "PARENT"                  			{ return PARENT; }
  "PARTITION"               			{ return PARTITION; }
  "PASSWORD"                            { return PASSWORD; }
  "PATCH"                               { return PATCH; }
  "PATTERN"                             { return PATTERN; }
  "PDF"                     			{ return PDF; }
  "PERIOD"                              { return PERIOD; }
  "PIVOT"                               { return PIVOT; }
  "PG"                                  { return PG; }    
  "POPUP"                			    { return POPUP; }
  "POSITION"                			{ return POSITION; }
  "POST"                			    { return POST; }
  "PREREAD"                			    { return PREREAD; }
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
  "PUT"              			        { return PUT; }
  "QUERYCLOSE"              			{ return QUERYCLOSE; }
  "QUERYOK"              			    { return QUERYOK; }
  "QUICKFILTER"                         { return QUICKFILTER; }
  "READ"                			    { return READ; }
  "READONLY"                			{ return READONLY; }
  "READONLYIF"              			{ return READONLYIF; }
  "RECALCULATE"               			{ return RECALCULATE; }
  "RECURSION"               			{ return RECURSION; }
  "REFLECTION"                  		{ return REFLECTION; }
  "REGEXP"                  			{ return REGEXP; }
  "REMOVE"                  			{ return REMOVE; }
  "REPLACE"              			    { return REPLACE; }
  "NOREPLACE"              			    { return NOREPLACE; }
  "REPORT"              			    { return REPORT; }
  "REPORTS"              			    { return REPORTS; }
  "REPORTFILES"              			{ return REPORTFILES; }
  "REQUEST"                 			{ return REQUEST; }
  "REQUIRE"                 			{ return REQUIRE; }
  "RESOLVE"                 			{ return RESOLVE; }
  "RETURN"                  			{ return RETURN; }
  "RGB"                     			{ return RGB; }
  "RIGHT"                   			{ return RIGHT; }
  "ROUND"                   			{ return ROUND; }
  "ROOT"                   			    { return ROOT; }
  "ROW"                   			    { return ROW; }
  "ROWS"                   			    { return ROWS; }
  "RTF"                     			{ return RTF; }
  "SCROLL"                              { return SCROLL; }
  "SEEK"                    			{ return SEEK; }
  "SELECT"                			    { return SELECT; }
  "SELECTED"                			{ return SELECTED; }
  "SELECTOR"                			{ return SELECTOR; }
  "SERIALIZABLE"                        { return SERIALIZABLE; }
  "SERVER"                              { return SERVER; }
  "SET"                     			{ return SET; }
  "SETCHANGED"              			{ return SETCHANGED; }
  "SETDROPPED"                 			{ return SETDROPPED; }
  "SETTINGS"                 			{ return SETTINGS; }
  "SCHEDULE"              			    { return SCHEDULE; }
  "SHOW"                    			{ return SHOW; }
  "SHOWDEP"                 			{ return SHOWDEP; }
  "SHOWIF"                  			{ return SHOWIF; }
  "SINGLE"                  			{ return SINGLE; }
  "SHEET"                  			    { return SHEET; }
  "SPLITH"                  			{ return SPLITH; }
  "SPLITV"                  			{ return SPLITV; }
  "SQL"                       			{ return SQL; }
  "START"             			        { return START; }
  "STEP"                    			{ return STEP; }
  "STICKY"                    			{ return STICKY; }
  "STRICT"                  			{ return STRICT; }
  "STRONG"                  			{ return STRONG; }
  "STRUCT"                  			{ return STRUCT; }
  "SUBJECT"                 			{ return SUBJECT; }
  "SUBREPORT"              			    { return SUBREPORT; }
  "SUCCESS"                     		{ return SUCCESS; }
  "SUM"                     			{ return SUM; }
  "TAB"                   		        { return TAB; }
  "TABBED"                  			{ return TABBED; }
  "TABLE"                   			{ return TABLE; }
  "TAG"                   			    { return TAG; }
  "TCP"                   			    { return TCP; }
  "TEXTHALIGN"              			{ return TEXTHALIGN; }
  "TEXTVALIGN"              			{ return TEXTVALIGN; }
  "THEN"                    			{ return THEN; }
  "THREADS"                    			{ return THREADS; }
  "TO"                      			{ return TO; }
  "DRAW"                  			    { return DRAW; }
  "TOOLBAR"                 			{ return TOOLBAR; }
  "TOOLBARBOX"                 			{ return TOOLBARBOX; }
  "TOOLBARLEFT"                			{ return TOOLBARLEFT; }
  "TOOLBARRIGHT"               			{ return TOOLBAR; }
  "TOOLBARSYSTEM"              			{ return TOOLBARSYSTEM; }
  "TOP"                     			{ return TOP; }
  "TREE"                    			{ return TREE; }
  "TRY"                                 { return TRY; }
  "UDP"                 			    { return UDP; }
  "UNGROUP"                 			{ return UNGROUP; }
  "UP"                 			        { return UP; }
  "USERFILTER"                 			{ return USERFILTER; }
  "USERFILTERS"                 		{ return USERFILTERS; }
  "VALIGN"                  			{ return VALIGN; }
  "VALUE"                               { return VALUE; }
  "VERTICAL"                			{ return VERTICAL; }
  "VIEW"                    			{ return VIEW; }
  "WARN"                    			{ return WARN; }
  "WEAK"                    			{ return WEAK; }
  "WHEN"                    			{ return WHEN; }
  "WHERE"                   			{ return WHERE; }
  "WHILE"                   			{ return WHILE; }
  "WITHIN"                    			{ return WITHIN; }
  "WRITE"                   			{ return WRITE; }
  "WINDOW"                  			{ return WINDOW; }
  "XLS"                                 { return XLS; }
  "XLSX"                                { return XLSX; }
  "XML"                                 { return XML; }
  "XOR"                     			{ return XOR; }
  "YES"                     			{ return YES; }
  "YESNO"                     			{ return YESNO; }

  ("R" | "r") {RAW_STR_PREFIX_CHAR} "'" { rawLiteralPrefixChar = yytext().charAt(yylength()-2); yybegin(RAW_STRING_LITERAL); }
  ("R" | "r") "'" {RAW_STR_LITERAL_CHAR}* "'" { return LEX_RAW_STRING_LITERAL; }

  ("###" | "##")? {ID_LITERAL} "#"?     { if (yytext().charAt(yylength()-1) == '#') yypushback(1); wasStringPart = false; startedWithID = true; yybegin(META_LITERAL);}
  ("###" | "##")? {NEXTID_LITERAL} "#"? { if (yytext().charAt(yylength()-1) == '#') yypushback(1); wasStringPart = false; startedWithID = false; yybegin(META_LITERAL); }
  ("###" | "##")? "'"                   { wasStringPart = true; startedWithID = false; yybegin(STRING_LITERAL); }

  [^] { return com.intellij.psi.TokenType.BAD_CHARACTER; }
}

<MULTILINE_COMMENT> {
    "*/"    { yybegin(YYINITIAL); return COMMENTS; }
    [^]     {}
    <<EOF>> { yybegin(YYINITIAL); return COMMENTS; }
}

<META_LITERAL> {
  ("###" | "##") {NEXTID_LITERAL} {}
  ("###" | "##") "'" { wasStringPart = true; yybegin(STRING_LITERAL); }
  [^] {
        yypushback(1);
        yybegin(YYINITIAL);
        if (!wasStringPart && startedWithID) return ID;
        else if (wasStringPart) return LEX_STRING_LITERAL;
        else return com.intellij.psi.TokenType.BAD_CHARACTER;
  }
  <<EOF>> {
            yybegin(YYINITIAL);
            if (!wasStringPart && startedWithID) return ID;
            else if (wasStringPart) return LEX_STRING_LITERAL;
            else return com.intellij.psi.TokenType.BAD_CHARACTER;
  }
}

<STRING_LITERAL> {
  "${"               { depth = 1; yybegin(INTERPOLATION_BLOCK); }
  {STR_LITERAL_CHAR} {}
  "'"                { yybegin(META_LITERAL); }
  [^]                { return com.intellij.psi.TokenType.BAD_CHARACTER; }
  <<EOF>>            { yybegin(YYINITIAL); return com.intellij.psi.TokenType.BAD_CHARACTER; }
}

<INTERPOLATION_BLOCK> {
  ("\\" .) | [^\\] {
    String ch = yytext().toString();
    if (ch.startsWith("{")) {
      ++depth;
    } else if (ch.startsWith("}")) {
      --depth;
      if (depth == 0) {
        yypushback(1);
        yybegin(STRING_LITERAL);
      }
    }
  }
  [^] { return com.intellij.psi.TokenType.BAD_CHARACTER; }
  <<EOF>> { yybegin(YYINITIAL); return com.intellij.psi.TokenType.BAD_CHARACTER; }
}

<RAW_STRING_LITERAL> {
  "'" {RAW_STR_PREFIX_CHAR} {
      if (yytext().charAt(yylength()-1) == rawLiteralPrefixChar) {
        yybegin(YYINITIAL); return LEX_RAW_STRING_LITERAL;
      }
  }
  [^] {}
  <<EOF>> { yybegin(YYINITIAL); return com.intellij.psi.TokenType.BAD_CHARACTER; }
}
