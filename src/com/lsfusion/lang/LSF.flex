package com.lsfusion.lang;
import com.intellij.lexer.*;
import com.intellij.psi.tree.IElementType;
import static com.lsfusion.lang.psi.LSFTypes.*;

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
CODE_LITERAL = \<\{([^{}]|[\r\n]|((\{|\})+([^{}<>]|[\r\n])))*(\{|\})?\}\>

%%
<YYINITIAL> {
  ({LINE_WS} | {EOL})+                  { return com.intellij.psi.TokenType.WHITE_SPACE; }
  "//" [^\n\r]*                         { return COMMENTS; }


  ("TRUE" | "FALSE")                    { return LEX_LOGICAL_LITERAL; }

    "INTEGER" | "LONG" | "NUMERIC[" {DIGITS} "," {DIGITS} "]" | "DOUBLE"
  | "DATE" | "DATETIME" | "TIME" | "YEAR"
  | "STRING[" {DIGITS} "]" | "ISTRING[" {DIGITS} "]" | "VARSTRING[" {DIGITS} "]" | "VARISTRING[" {DIGITS} "]"
  | "TEXT" | "RICHTEXT"
  | "WORDFILE" | "IMAGEFILE" | "PDFFILE" | "CUSTOMFILE" | "EXCELFILE"
  | "WORDLINK" | "IMAGELINK" | "PDFLINK" | "CUSTOMLINK" | "EXCELLINK"
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
  "NATIVE"                			    { return NATIVE; }
  "ACTION"                  			{ return ACTION; }
  "ADD"                     			{ return ADD; }
  "ADDFORM"                 			{ return ADDFORM; }
  "ADDOBJ"                  			{ return ADDOBJ; }
  "ADDNESTEDFORM"          			    { return ADDNESTEDFORM; }
  "ADDSESSIONFORM"          			{ return ADDSESSIONFORM; }
  "AFTER"                   			{ return AFTER; }
  "GOAFTER"                   			{ return GOAFTER; }
  "AGGPROP"                 			{ return AGGPROP; }
  "AGGR"                    			{ return AGGR; }
  "NAGGR"                    			{ return NAGGR; }
  "AND"                     			{ return AND; }
  "APPLY"                   			{ return APPLY; }
  "AS"                      			{ return AS; }
  "ASON"                      			{ return ASON; }
  "ASSIGN"                  			{ return ASSIGN; }
  "ASYNCUPDATE"             			{ return ASYNCUPDATE; }
  "ATTACH"                  			{ return ATTACH; }
  "AUTO"                                { return AUTO; }
  "AUTOREFRESH"                         { return AUTOREFRESH; }
  "AUTOSET"                 			{ return AUTOSET; }
  "BACKGROUND"              			{ return BACKGROUND; }
  "BCC"                     			{ return BCC; }
  "BEFORE"                  			{ return BEFORE; }
  "BOTTOM"                  			{ return BOTTOM; }
  "BREAK"                   			{ return BREAK; }
  "BY"                      			{ return BY; }
  "CANCEL"                  			{ return CANCEL; }
  "CASE"                    			{ return CASE; }
  "CATCH"                               { return CATCH; }
  "CC"                      			{ return CC; }
  "CENTER"                  			{ return CENTER; }
  "CHANGE"                  			{ return CHANGE; }
  "CHANGECLASS"             			{ return CHANGECLASS; }
  "CHANGED"                 			{ return CHANGED; }
  "CHANGEWYS"               			{ return CHANGEWYS; }
  "CHARSET"               			    { return CHARSET; }
  "CHECK"                   			{ return CHECK; }
  "CHECKED"                 			{ return CHECKED; }
  "CLASS"                   			{ return CLASS; }
  "CLOSE"                   			{ return CLOSE; }
  "COLUMNS"                 			{ return COLUMNS; }
  "COMPLEX"                 			{ return COMPLEX; }
  "CONNECTION"                 			{ return CONNECTION; }
  "NOHINT"                 			    { return NOHINT; }
  "CONCAT"                  			{ return CONCAT; }
  "CONFIRM"                 			{ return CONFIRM; }
  "CONSTRAINT"              			{ return CONSTRAINT; }
  "CONTAINERH"              			{ return CONTAINERH; }
  "CONTAINERV"              			{ return CONTAINERV; }
  "COLUMNS"             			    { return COLUMNS; }
  "LEADING"             			    { return LEADING; }
  "TRAILING"             			    { return TRAILING; }
  "STRETCH"             			    { return STRETCH; }
  "CSV"                  			    { return CSV; }
  "CUSTOM"                  			{ return CUSTOM; }
  "CYCLES"                  			{ return CYCLES; }
  "DATA"                    			{ return DATA; }
  "DBF"                    			    { return DBF; }
  "DEFAULT"                 			{ return DEFAULT; }
  "DELAY"              			        { return DELAY; }
  "DELETE"                  			{ return DELETE; }
  "DELETESESSION"           			{ return DELETESESSION; }
  "DESC"                    			{ return DESC; }
  "DESIGN"                  			{ return DESIGN; }
  "DIALOG"                  			{ return DIALOG; }
  "DO"                      			{ return DO; }
  "DOC"                      			{ return DOC; }
  "DOCKED"                  			{ return DOCKED; }
  "DOCKEDMODAL"             			{ return DOCKEDMODAL; }
  "DOCX"                    			{ return DOCX; }
  "DRAWROOT"                			{ return DRAWROOT; }
  "DRILLDOWN"                    	    { return DRILLDOWN; }
  "DROP"                    			{ return DROP; }
  "DROPCHANGED"             			{ return DROPCHANGED; }
  "DROPPED"                 			{ return DROPPED; }
  "DROPSET"                 			{ return DROPSET; }
  "ECHO"                    			{ return ECHO; }
  "EDIT"                    			{ return EDIT; }
  "EDITABLE"                			{ return EDITABLE; }
  "EDITFORM"                			{ return EDITFORM; }
  "EDITKEY"                 			{ return EDITKEY; }
  "EDITNESTEDFORM"         			    { return EDITNESTEDFORM; }
  "EDITSESSIONFORM"         			{ return EDITSESSIONFORM; }
  "ELSE"                    			{ return ELSE; }
  "EMAIL"                   			{ return EMAIL; }
  "END"                     			{ return END; }
  "EVAL"                    			{ return EVAL; }
  "EVENTID"                 			{ return EVENTID; }
  "EVENTS"                  			{ return EVENTS; }
  "EXCEPTLAST"              			{ return EXCEPTLAST; }
  "EXCLUSIVE"               			{ return EXCLUSIVE; }
  "EXEC"                    			{ return EXEC; }
  "NEWEXECUTOR"                    		{ return NEWEXECUTOR; }
  "EXPORT"                    			{ return EXPORT; }
  "EXTEND"                  			{ return EXTEND; }
  "EXTERNAL"                  			{ return EXTERNAL; }
  "FILE"                  			    { return FILE; }
  "FILTER"                  			{ return FILTER; }
  "FILTERGROUP"             			{ return FILTERGROUP; }
  "FILTERS"                 			{ return FILTERS; }
  "FINALLY"                             { return FINALLY; }
  "FIRST"                   			{ return FIRST; }
  "FIXED"                   			{ return FIXED; }
  "FIXEDCHARWIDTH"          			{ return FIXEDCHARWIDTH; }
  "FOCUS"                  				{ return FOCUS; }
  "FOOTER"                  			{ return FOOTER; }
  "FOR"                     			{ return FOR; }
  "FORCE"                   			{ return FORCE; }
  "FOREGROUND"              			{ return FOREGROUND; }
  "FORM"                    			{ return FORM; }
  "FORMS"                   			{ return FORMS; }
  "FORMULA"                 			{ return FORMULA; }
  "FROM"                    			{ return FROM; }
  "FULL"              			        { return FULL; }
  "FULLSCREEN"              			{ return FULLSCREEN; }
  "GLOBAL"                              { return GLOBAL; }
  "GRID"                    			{ return GRID; }
  "GROUP"                   			{ return GROUP; }
  "GROUPCHANGE"                         { return GROUPCHANGE; }
  "EQUAL"                               { return EQUAL; }
  "HALIGN"                  			{ return HALIGN; }
  "HEADER"                  			{ return HEADER; }
  "HIDE"                    			{ return HIDE; }
  "HIDESCROLLBARS"          			{ return HIDESCROLLBARS; }
  "HIDETITLE"               			{ return HIDETITLE; }
  "HINTNOUPDATE"            			{ return HINTNOUPDATE; }
  "HINTTABLE"               			{ return HINTTABLE; }
  "HORIZONTAL"              			{ return HORIZONTAL; }
  "HTML"                    			{ return HTML; }
  "IF"                      			{ return IF; }
  "IMAGE"                   			{ return IMAGE; }
  "IMPORT"                   			{ return IMPORT; }
  "IMPOSSIBLE"              			{ return IMPOSSIBLE; }
  "IN"                      			{ return IN; }
  "INDEX"                   			{ return INDEX; }
  "INDEXED"                 			{ return INDEXED; }
  "INIT"                    			{ return INIT; }
  "INITFILTER"                 			{ return INITFILTER; }
  "INLINE"                  			{ return INLINE; }
  "INPUT"                   			{ return INPUT; }
  "IS"                      			{ return IS; }
  "JDBC"                  			    { return JDBC; }
  "JOIN"                    			{ return JOIN; }
  "LAST"                                { return LAST; }
  "LEFT"                    			{ return LEFT; }
  "LIMIT"                   			{ return LIMIT; }
  "LIST"                    			{ return LIST; }
  "LOADFILE"                			{ return LOADFILE; }
  "LOCAL"                   			{ return LOCAL; }
  "LOGGABLE"                			{ return LOGGABLE; }
  "MANAGESESSION"           			{ return MANAGESESSION; }
  "NOMANAGESESSION"                     { return NOMANAGESESSION; }
  "MAX"                     			{ return MAX; }
  "MDB"                     			{ return MDB; }
  "LAST"                     			{ return LAST; }
  "MAXCHARWIDTH"            			{ return MAXCHARWIDTH; }
  "MENU"                    			{ return MENU; }
  "MEMO"                    			{ return MEMO; }
  "MESSAGE"                 			{ return MESSAGE; }
  "META"                    			{ return META; }
  "MIN"                     			{ return MIN; }
  "MINCHARWIDTH"            			{ return MINCHARWIDTH; }
  "MODAL"                   			{ return MODAL; }
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
  "NEWSESSION"              			{ return NEWSESSION; }
  "NEWSQL"              			    { return NEWSQL; }
  "NEWTHREAD"              			    { return NEWTHREAD; }
  "NO"                      			{ return NO; }
  "NOCANCEL"                			{ return NOCANCEL; }
  "NOINLINE"                			{ return NOINLINE; }
  "NOHEADER"                			{ return NOHEADER; }
  "NOT"                     			{ return NOT; }
  "NOWAIT"                              { return NOWAIT; }
  "NULL"                    			{ return NULL; }
  "OBJECT"                  			{ return OBJECT; }
  "OBJECTS"                 			{ return OBJECTS; }
  "CONTEXTFILTER"              			{ return CONTEXTFILTER; }
  "OBJVALUE"                			{ return OBJVALUE; }
  "OK"                      			{ return OK; }
  "ON"                      			{ return ON; }
  "OPENFILE"                			{ return OPENFILE; }
  "OPTIMISTICASYNC"                     { return OPTIMISTICASYNC; }
  "OR"                      			{ return OR; }
  "ORDER"                   			{ return ORDER; }
  "OVERRIDE"                			{ return OVERRIDE; }
  "PAGESIZE"                			{ return PAGESIZE; }
  "PANEL"                   			{ return PANEL; }
  "PARENT"                  			{ return PARENT; }
  "PARTITION"               			{ return PARTITION; }
  "PDF"                     			{ return PDF; }
  "PERSISTENT"              			{ return PERSISTENT; }
  "PERIOD"                              { return PERIOD; }    
  "PG"                                  { return PG; }    
  "POSITION"                			{ return POSITION; }
  "PREFCHARWIDTH"           			{ return PREFCHARWIDTH; }
  "PREV"                    			{ return PREV; }
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
  "RTF"                     			{ return RTF; }
  "SAVEFILE"                            { return SAVEFILE; }
  "SCROLL"                              { return SCROLL; }
  "SEEK"                    			{ return SEEK; }
  "SELECTION"               			{ return SELECTION; }
  "SELECTOR"                			{ return SELECTOR; }
  "SERIALIZABLE"                        { return SERIALIZABLE; }
  "SESSION"                 			{ return SESSION; }
  "SET"                     			{ return SET; }
  "SETCHANGED"              			{ return SETCHANGED; }
  "SCHEDULE"              			    { return SCHEDULE; }
  "SHORTCUT"                			{ return SHORTCUT; }
  "SHOW"                    			{ return SHOW; }
  "SHOWDEP"                 			{ return SHOWDEP; }
  "SHOWDROP"                			{ return SHOWDROP; }
  "SHOWIF"                  			{ return SHOWIF; }
  "SINGLE"                  			{ return SINGLE; }
  "SHEET"                  			    { return SHEET; }
  "SPLITH"                  			{ return SPLITH; }
  "SPLITV"                  			{ return SPLITV; }
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
  "TOP"                     			{ return TOP; }
  "TREE"                    			{ return TREE; }
  "TRY"                                 { return TRY; }
  "UNGROUP"                 			{ return UNGROUP; }
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
  "##"                                  { return FAKETWODASHES;}
  "###"                                 { return FAKETHREEDASHES;}

  {FIRST_ID_LETTER} {NEXT_ID_LETTER}*   { return ID; }

  [^] { return com.intellij.psi.TokenType.BAD_CHARACTER; }
}
