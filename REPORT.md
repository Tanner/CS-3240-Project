# Implementation
We structured our LL1 parser into several object-oriented classes. We first constructed the LL1Lexer class,
that simply scans in the input program file and for each token it matches it with a TokenType (which is an
enumeration). This is done with a regular expression that is defined for each TokenType. This class outputs
the tokenized program to a file on the disk.

Then we built the LL1Grammar class that scans in the grammar description and sets the start variable,
terminals, non-terminals, and rules of the specified grammar. We built several classes to do this:
	
	* a Rule, which has a Variable for the left side and a list of RuleElements for the right side
	* a RuleElement is the superclass of Variable and Terminal
	* a Variable is a non-terminal
	* a Terminal is a terminal

We wrote two methods in the LL1Grammar class to remove left recursion and left factoring from the grammar's rules.
Any time there is an error that occurs when parsing the grammar description, an LL1GrammarException is thrown
with a string description of what went wrong.

From here, we provide the processed grammar instance as an argument in the LL1ParsingTable's constructor.
The constructor constructs the first and follow sets from the grammar, and then uses those data structures
to construct the parsing table (which is a Map of Variables to a Map of Terminals to a list of RuleElements).
With each token that is added to each first set, we create a TerminalPair instance that pairs the terminal
with the list of RuleElements that comes from the right-side of the rule that is responsible for that
Terminal being in the first set. With the follow set, we do the same thing except we just pair each Terminal
in the set with a list of RuleElements made up of only one EmptyString instance. This way, we can easily
construct the parsing table by just inserting these lists of RuleElements into each appropriate cell, if
necessary.

The class that drives all of these classes is the LL1Parser class. After the parsing table instance has been
fully constructed, the LL1Parser starts the actually parsing of the tokenized program. The parser has a stack
and it begins by adding the grammar's start Variable and reading in a token from the tokenized file. It then
uses the parsing table instance to look up what list of rule elements need to be added to the stack for the
Variable (on the stack) and Terminal (from the tokenized file). This process continues. If the element popped
off of the parsing stack is a Terminal, then that Terminal must match the token read in from the tokenized
program. If these two Terminals are of the same TokenType, then the parser reads in the next Token from the
tokenized program. The parsing is successful when there is no input remaining and the stack is empty. Any
time there is an error that occurs when parsing (e.g. if nothing is returned from the parsing table lookup),
then we throw a LL1ParseException with a string description of what went wrong. 

# Code Output

## Tiny Grammar

### CLI Arguments
`tests/tiny/grammar.txt tests/tiny/test.txt -v`

### Token File

BEGIN ID ASSIGN INTNUM SEMICOLON ID ASSIGN INTNUM SEMICOLON READ LEFTPAR ID RIGHTPAR SEMICOLON ID ASSIGN INTNUM PLUS ID PLUS LEFTPAR INTNUM MULTIPLY INTNUM MODULO INTNUM RIGHTPAR SEMICOLON END 

### Terminal

#### Grammar
	Terminals: BEGIN END PRINT READ ASSIGN ID SEMICOLON COMMA INTNUM LEFTPAR RIGHTPAR PLUS MINUS MULTIPLY MODULO $ 
	Variables: <Tiny-program> <statement-list> <statement> <id-list> <exp-list> <exp> <bin-op> <statement-list_tail> <id-list_tail> <exp-list_tail> <exp_tail> 
	Start Variable: <Tiny-program>
	Rules:
	<Tiny-program> : BEGIN <statement-list> END 
	<statement-list_tail> : <statement> <statement-list_tail> 
	<statement-list_tail> : {epsilon} 
	<statement-list> : <statement> <statement-list_tail> 
	<statement> : PRINT LEFTPAR <exp-list> RIGHTPAR SEMICOLON 
	<statement> : ID ASSIGN <exp> SEMICOLON 
	<statement> : READ LEFTPAR <id-list> RIGHTPAR SEMICOLON 
	<id-list_tail> : COMMA ID <id-list_tail> 
	<id-list_tail> : {epsilon} 
	<id-list> : ID <id-list_tail> 
	<exp-list_tail> : COMMA <exp> <exp-list_tail> 
	<exp-list_tail> : {epsilon} 
	<exp-list> : <exp> <exp-list_tail> 
	<exp> : ID <exp_tail> 
	<exp> : INTNUM <exp_tail> 
	<exp> : LEFTPAR <exp> RIGHTPAR <exp_tail> 
	<exp_tail> : <bin-op> <exp> <exp_tail> 
	<exp_tail> : {epsilon} 
	<bin-op> : PLUS 
	<bin-op> : MINUS 
	<bin-op> : MULTIPLY 
	<bin-op> : MODULO
	
#### First Set
	<Tiny-program> BEGIN
	<statement-list> PRINT ID READ
	<statement> PRINT ID READ
	<id-list> ID
	<exp-list> ID INTNUM LEFTPAR
	<exp> ID INTNUM LEFTPAR
	<bin-op> PLUS MINUS MULTIPLY MODULO
	<statement-list_tail> {epsilon} PRINT ID READ
	<id-list_tail> COMMA {epsilon}
	<exp-list_tail> COMMA {epsilon}
	<exp_tail> {epsilon} PLUS MINUS MULTIPLY MODULO
	
#### Follow Set
	<Tiny-program> $
	<statement-list> END
	<statement> PRINT ID READ END
	<id-list> RIGHTPAR
	<exp-list> RIGHTPAR
	<exp> SEMICOLON COMMA RIGHTPAR PLUS MINUS MULTIPLY MODULO
	<bin-op> ID INTNUM LEFTPAR
	<statement-list_tail> END
	<id-list_tail> RIGHTPAR
	<exp-list_tail> RIGHTPAR
	<exp_tail> SEMICOLON COMMA RIGHTPAR PLUS MINUS MULTIPLY MODULO
	
#### Parsing Table
	                         |                           BEGIN  |          END  |                                              PRINT  |                                             READ  |  ASSIGN  |                                    ID  |    SEMICOLON  |                            COMMA  |                    INTNUM  |                                 LEFTPAR  |     RIGHTPAR  |                           PLUS  |                          MINUS  |                       MULTIPLY  |                         MODULO  |  $  
	------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	  <Tiny-program>         |  [BEGIN, <statement-list>, END]  |               |                                                     |                                                   |          |                                        |               |                                   |                            |                                          |               |                                 |                                 |                                 |                                 |     
	                         |                                  |               |                                                     |                                                   |          |                                        |               |                                   |                            |                                          |               |                                 |                                 |                                 |                                 |     
	  <statement-list>       |                                  |               |               [<statement>, <statement-list_tail>]  |             [<statement>, <statement-list_tail>]  |          |  [<statement>, <statement-list_tail>]  |               |                                   |                            |                                          |               |                                 |                                 |                                 |                                 |     
	                         |                                  |               |                                                     |                                                   |          |                                        |               |                                   |                            |                                          |               |                                 |                                 |                                 |                                 |     
	  <statement>            |                                  |               |  [PRINT, LEFTPAR, <exp-list>, RIGHTPAR, SEMICOLON]  |  [READ, LEFTPAR, <id-list>, RIGHTPAR, SEMICOLON]  |          |        [ID, ASSIGN, <exp>, SEMICOLON]  |               |                                   |                            |                                          |               |                                 |                                 |                                 |                                 |     
	                         |                                  |               |                                                     |                                                   |          |                                        |               |                                   |                            |                                          |               |                                 |                                 |                                 |                                 |     
	  <id-list>              |                                  |               |                                                     |                                                   |          |                  [ID, <id-list_tail>]  |               |                                   |                            |                                          |               |                                 |                                 |                                 |                                 |     
	                         |                                  |               |                                                     |                                                   |          |                                        |               |                                   |                            |                                          |               |                                 |                                 |                                 |                                 |     
	  <exp-list>             |                                  |               |                                                     |                                                   |          |              [<exp>, <exp-list_tail>]  |               |                                   |  [<exp>, <exp-list_tail>]  |                [<exp>, <exp-list_tail>]  |               |                                 |                                 |                                 |                                 |     
	                         |                                  |               |                                                     |                                                   |          |                                        |               |                                   |                            |                                          |               |                                 |                                 |                                 |                                 |     
	  <exp>                  |                                  |               |                                                     |                                                   |          |                      [ID, <exp_tail>]  |               |                                   |      [INTNUM, <exp_tail>]  |  [LEFTPAR, <exp>, RIGHTPAR, <exp_tail>]  |               |                                 |                                 |                                 |                                 |     
	                         |                                  |               |                                                     |                                                   |          |                                        |               |                                   |                            |                                          |               |                                 |                                 |                                 |                                 |     
	  <bin-op>               |                                  |               |                                                     |                                                   |          |                                        |               |                                   |                            |                                          |               |                         [PLUS]  |                        [MINUS]  |                     [MULTIPLY]  |                       [MODULO]  |     
	                         |                                  |               |                                                     |                                                   |          |                                        |               |                                   |                            |                                          |               |                                 |                                 |                                 |                                 |     
	  <statement-list_tail>  |                                  |  [{epsilon}]  |               [<statement>, <statement-list_tail>]  |             [<statement>, <statement-list_tail>]  |          |  [<statement>, <statement-list_tail>]  |               |                                   |                            |                                          |               |                                 |                                 |                                 |                                 |     
	                         |                                  |               |                                                     |                                                   |          |                                        |               |                                   |                            |                                          |               |                                 |                                 |                                 |                                 |     
	  <id-list_tail>         |                                  |               |                                                     |                                                   |          |                                        |               |      [COMMA, ID, <id-list_tail>]  |                            |                                          |  [{epsilon}]  |                                 |                                 |                                 |                                 |     
	                         |                                  |               |                                                     |                                                   |          |                                        |               |                                   |                            |                                          |               |                                 |                                 |                                 |                                 |     
	  <exp-list_tail>        |                                  |               |                                                     |                                                   |          |                                        |               |  [COMMA, <exp>, <exp-list_tail>]  |                            |                                          |  [{epsilon}]  |                                 |                                 |                                 |                                 |     
	                         |                                  |               |                                                     |                                                   |          |                                        |               |                                   |                            |                                          |               |                                 |                                 |                                 |                                 |     
	  <exp_tail>             |                                  |               |                                                     |                                                   |          |                                        |  [{epsilon}]  |                      [{epsilon}]  |                            |                                          |  [{epsilon}]  |  [<bin-op>, <exp>, <exp_tail>]  |  [<bin-op>, <exp>, <exp_tail>]  |  [<bin-op>, <exp>, <exp_tail>]  |  [<bin-op>, <exp>, <exp_tail>]  |      

#### Parsing Stack                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            
	[<Tiny-program>]
	Popped <Tiny-program>
	[END, <statement-list>, BEGIN]
	Popped BEGIN
	Parsed T_BEGIN
	[END, <statement-list>]
	Popped <statement-list>
	[END, <statement-list_tail>, <statement>]
	Popped <statement>
	[END, <statement-list_tail>, SEMICOLON, <exp>, ASSIGN, ID]
	Popped ID
	Parsed T_ID
	[END, <statement-list_tail>, SEMICOLON, <exp>, ASSIGN]
	Popped ASSIGN
	Parsed T_ASSIGN
	[END, <statement-list_tail>, SEMICOLON, <exp>]
	Popped <exp>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, INTNUM]
	Popped INTNUM
	Parsed T_INTNUM
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>]
	Popped <exp_tail>
	[END, <statement-list_tail>, SEMICOLON, {epsilon}]
	Popped {epsilon}
	[END, <statement-list_tail>, SEMICOLON]
	Popped SEMICOLON
	Parsed T_SEMICOLON
	[END, <statement-list_tail>]
	Popped <statement-list_tail>
	[END, <statement-list_tail>, <statement>]
	Popped <statement>
	[END, <statement-list_tail>, SEMICOLON, <exp>, ASSIGN, ID]
	Popped ID
	Parsed T_ID
	[END, <statement-list_tail>, SEMICOLON, <exp>, ASSIGN]
	Popped ASSIGN
	Parsed T_ASSIGN
	[END, <statement-list_tail>, SEMICOLON, <exp>]
	Popped <exp>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, INTNUM]
	Popped INTNUM
	Parsed T_INTNUM
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>]
	Popped <exp_tail>
	[END, <statement-list_tail>, SEMICOLON, {epsilon}]
	Popped {epsilon}
	[END, <statement-list_tail>, SEMICOLON]
	Popped SEMICOLON
	Parsed T_SEMICOLON
	[END, <statement-list_tail>]
	Popped <statement-list_tail>
	[END, <statement-list_tail>, <statement>]
	Popped <statement>
	[END, <statement-list_tail>, SEMICOLON, RIGHTPAR, <id-list>, LEFTPAR, READ]
	Popped READ
	Parsed T_READ
	[END, <statement-list_tail>, SEMICOLON, RIGHTPAR, <id-list>, LEFTPAR]
	Popped LEFTPAR
	Parsed T_LEFTPAR
	[END, <statement-list_tail>, SEMICOLON, RIGHTPAR, <id-list>]
	Popped <id-list>
	[END, <statement-list_tail>, SEMICOLON, RIGHTPAR, <id-list_tail>, ID]
	Popped ID
	Parsed T_ID
	[END, <statement-list_tail>, SEMICOLON, RIGHTPAR, <id-list_tail>]
	Popped <id-list_tail>
	[END, <statement-list_tail>, SEMICOLON, RIGHTPAR, {epsilon}]
	Popped {epsilon}
	[END, <statement-list_tail>, SEMICOLON, RIGHTPAR]
	Popped RIGHTPAR
	Parsed T_RIGHTPAR
	[END, <statement-list_tail>, SEMICOLON]
	Popped SEMICOLON
	Parsed T_SEMICOLON
	[END, <statement-list_tail>]
	Popped <statement-list_tail>
	[END, <statement-list_tail>, <statement>]
	Popped <statement>
	[END, <statement-list_tail>, SEMICOLON, <exp>, ASSIGN, ID]
	Popped ID
	Parsed T_ID
	[END, <statement-list_tail>, SEMICOLON, <exp>, ASSIGN]
	Popped ASSIGN
	Parsed T_ASSIGN
	[END, <statement-list_tail>, SEMICOLON, <exp>]
	Popped <exp>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, INTNUM]
	Popped INTNUM
	Parsed T_INTNUM
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>]
	Popped <exp_tail>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <exp>, <bin-op>]
	Popped <bin-op>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <exp>, PLUS]
	Popped PLUS
	Parsed T_PLUS
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <exp>]
	Popped <exp>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <exp_tail>, ID]
	Popped ID
	Parsed T_ID
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <exp_tail>]
	Popped <exp_tail>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <exp_tail>, <exp>, <bin-op>]
	Popped <bin-op>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <exp_tail>, <exp>, PLUS]
	Popped PLUS
	Parsed T_PLUS
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <exp_tail>, <exp>]
	Popped <exp>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <exp_tail>, <exp_tail>, RIGHTPAR, <exp>, LEFTPAR]
	Popped LEFTPAR
	Parsed T_LEFTPAR
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <exp_tail>, <exp_tail>, RIGHTPAR, <exp>]
	Popped <exp>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <exp_tail>, <exp_tail>, RIGHTPAR, <exp_tail>, INTNUM]
	Popped INTNUM
	Parsed T_INTNUM
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <exp_tail>, <exp_tail>, RIGHTPAR, <exp_tail>]
	Popped <exp_tail>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <exp_tail>, <exp_tail>, RIGHTPAR, <exp_tail>, <exp>, <bin-op>]
	Popped <bin-op>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <exp_tail>, <exp_tail>, RIGHTPAR, <exp_tail>, <exp>, MULTIPLY]
	Popped MULTIPLY
	Parsed T_MULTIPLY
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <exp_tail>, <exp_tail>, RIGHTPAR, <exp_tail>, <exp>]
	Popped <exp>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <exp_tail>, <exp_tail>, RIGHTPAR, <exp_tail>, <exp_tail>, INTNUM]
	Popped INTNUM
	Parsed T_INTNUM
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <exp_tail>, <exp_tail>, RIGHTPAR, <exp_tail>, <exp_tail>]
	Popped <exp_tail>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <exp_tail>, <exp_tail>, RIGHTPAR, <exp_tail>, <exp_tail>, <exp>, <bin-op>]
	Popped <bin-op>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <exp_tail>, <exp_tail>, RIGHTPAR, <exp_tail>, <exp_tail>, <exp>, MODULO]
	Popped MODULO
	Parsed T_MODULO
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <exp_tail>, <exp_tail>, RIGHTPAR, <exp_tail>, <exp_tail>, <exp>]
	Popped <exp>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <exp_tail>, <exp_tail>, RIGHTPAR, <exp_tail>, <exp_tail>, <exp_tail>, INTNUM]
	Popped INTNUM
	Parsed T_INTNUM
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <exp_tail>, <exp_tail>, RIGHTPAR, <exp_tail>, <exp_tail>, <exp_tail>]
	Popped <exp_tail>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <exp_tail>, <exp_tail>, RIGHTPAR, <exp_tail>, <exp_tail>, {epsilon}]
	Popped {epsilon}
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <exp_tail>, <exp_tail>, RIGHTPAR, <exp_tail>, <exp_tail>]
	Popped <exp_tail>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <exp_tail>, <exp_tail>, RIGHTPAR, <exp_tail>, {epsilon}]
	Popped {epsilon}
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <exp_tail>, <exp_tail>, RIGHTPAR, <exp_tail>]
	Popped <exp_tail>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <exp_tail>, <exp_tail>, RIGHTPAR, {epsilon}]
	Popped {epsilon}
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <exp_tail>, <exp_tail>, RIGHTPAR]
	Popped RIGHTPAR
	Parsed T_RIGHTPAR
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <exp_tail>, <exp_tail>]
	Popped <exp_tail>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <exp_tail>, {epsilon}]
	Popped {epsilon}
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <exp_tail>]
	Popped <exp_tail>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, {epsilon}]
	Popped {epsilon}
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>]
	Popped <exp_tail>
	[END, <statement-list_tail>, SEMICOLON, {epsilon}]
	Popped {epsilon}
	[END, <statement-list_tail>, SEMICOLON]
	Popped SEMICOLON
	Parsed T_SEMICOLON
	[END, <statement-list_tail>]
	Popped <statement-list_tail>
	[END, {epsilon}]
	Popped {epsilon}
	[END]
	Popped END
	Successful parse!

## Tiny Grammar with Precedence

### CLI Arguments
`tests/tiny_precedence/grammar.txt tests/tiny_precedence/test.txt -v`

### Token File
BEGIN ID ASSIGN INTNUM SEMICOLON ID ASSIGN INTNUM SEMICOLON READ LEFTPAR ID RIGHTPAR SEMICOLON ID ASSIGN INTNUM PLUS ID PLUS LEFTPAR INTNUM MULTIPLY INTNUM MODULO INTNUM RIGHTPAR SEMICOLON END

### Terminal

#### Grammar
	Terminals: BEGIN END PRINT READ ASSIGN ID SEMICOLON COMMA INTNUM LEFTPAR RIGHTPAR PLUS MINUS MULTIPLY MODULO $ 
	Variables: <Tiny-program> <statement-list> <statement> <id-list> <exp-list> <exp> <term> <term1> <add-op> <factor> <statement-list_tail> <id-list_tail> <exp-list_tail> <exp_tail> <term_tail> <term1_tail> 
	Start Variable: <Tiny-program>
	Rules:
	<Tiny-program> : BEGIN <statement-list> END 
	<statement-list_tail> : <statement> <statement-list_tail> 
	<statement-list_tail> : {epsilon} 
	<statement-list> : <statement> <statement-list_tail> 
	<statement> : PRINT LEFTPAR <exp-list> RIGHTPAR SEMICOLON 
	<statement> : ID ASSIGN <exp> SEMICOLON 
	<statement> : READ LEFTPAR <id-list> RIGHTPAR SEMICOLON 
	<id-list_tail> : COMMA ID <id-list_tail> 
	<id-list_tail> : {epsilon} 
	<id-list> : ID <id-list_tail> 
	<exp-list_tail> : COMMA <exp> <exp-list_tail> 
	<exp-list_tail> : {epsilon} 
	<exp-list> : <exp> <exp-list_tail> 
	<exp_tail> : <add-op> <term> <exp_tail> 
	<exp_tail> : {epsilon} 
	<exp> : <term> <exp_tail> 
	<term_tail> : MULTIPLY <term1> <term_tail> 
	<term_tail> : {epsilon} 
	<term> : <term1> <term_tail> 
	<term1_tail> : MODULO <factor> <term1_tail> 
	<term1_tail> : {epsilon} 
	<term1> : <factor> <term1_tail> 
	<add-op> : PLUS 
	<add-op> : MINUS 
	<factor> : LEFTPAR <exp> RIGHTPAR 
	<factor> : ID 
	<factor> : INTNUM
	
#### First Set
	<Tiny-program> BEGIN
	<statement-list> PRINT ID READ
	<statement> PRINT ID READ
	<id-list> ID
	<exp-list> COMMA {epsilon} PLUS MINUS MULTIPLY LEFTPAR ID INTNUM
	<exp> {epsilon} PLUS MINUS MULTIPLY LEFTPAR ID INTNUM
	<term> MULTIPLY {epsilon} LEFTPAR ID INTNUM
	<term1> LEFTPAR ID INTNUM
	<add-op> PLUS MINUS
	<factor> LEFTPAR ID INTNUM
	<statement-list_tail> {epsilon} PRINT ID READ
	<id-list_tail> COMMA {epsilon}
	<exp-list_tail> COMMA {epsilon}
	<exp_tail> {epsilon} PLUS MINUS
	<term_tail> MULTIPLY {epsilon}
	<term1_tail> MODULO {epsilon}
	
#### Follow Set
	<Tiny-program> $
	<statement-list> END
	<statement> PRINT ID READ END
	<id-list> RIGHTPAR
	<exp-list> RIGHTPAR
	<exp> SEMICOLON COMMA RIGHTPAR
	<term> PLUS MINUS SEMICOLON COMMA RIGHTPAR
	<term1> MULTIPLY PLUS MINUS SEMICOLON COMMA RIGHTPAR
	<add-op> MULTIPLY LEFTPAR ID INTNUM PLUS MINUS SEMICOLON COMMA RIGHTPAR
	<factor> MODULO MULTIPLY PLUS MINUS SEMICOLON COMMA RIGHTPAR
	<statement-list_tail> END
	<id-list_tail> RIGHTPAR
	<exp-list_tail> RIGHTPAR
	<exp_tail> SEMICOLON COMMA RIGHTPAR
	<term_tail> PLUS MINUS SEMICOLON COMMA RIGHTPAR
	<term1_tail> MULTIPLY PLUS MINUS SEMICOLON COMMA RIGHTPAR
	
#### Parsing Table
	                         |                           BEGIN  |          END  |                                              PRINT  |                                             READ  |  ASSIGN  |                                    ID  |    SEMICOLON  |                            COMMA  |                    INTNUM  |                     LEFTPAR  |     RIGHTPAR  |                            PLUS  |                           MINUS  |                          MULTIPLY  |                            MODULO  |  $  
	--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	  <Tiny-program>         |  [BEGIN, <statement-list>, END]  |               |                                                     |                                                   |          |                                        |               |                                   |                            |                              |               |                                  |                                  |                                    |                                    |     
	                         |                                  |               |                                                     |                                                   |          |                                        |               |                                   |                            |                              |               |                                  |                                  |                                    |                                    |     
	  <statement-list>       |                                  |               |               [<statement>, <statement-list_tail>]  |             [<statement>, <statement-list_tail>]  |          |  [<statement>, <statement-list_tail>]  |               |                                   |                            |                              |               |                                  |                                  |                                    |                                    |     
	                         |                                  |               |                                                     |                                                   |          |                                        |               |                                   |                            |                              |               |                                  |                                  |                                    |                                    |     
	  <statement>            |                                  |               |  [PRINT, LEFTPAR, <exp-list>, RIGHTPAR, SEMICOLON]  |  [READ, LEFTPAR, <id-list>, RIGHTPAR, SEMICOLON]  |          |        [ID, ASSIGN, <exp>, SEMICOLON]  |               |                                   |                            |                              |               |                                  |                                  |                                    |                                    |     
	                         |                                  |               |                                                     |                                                   |          |                                        |               |                                   |                            |                              |               |                                  |                                  |                                    |                                    |     
	  <id-list>              |                                  |               |                                                     |                                                   |          |                  [ID, <id-list_tail>]  |               |                                   |                            |                              |               |                                  |                                  |                                    |                                    |     
	                         |                                  |               |                                                     |                                                   |          |                                        |               |                                   |                            |                              |               |                                  |                                  |                                    |                                    |     
	  <exp-list>             |                                  |               |                                                     |                                                   |          |              [<exp>, <exp-list_tail>]  |               |         [<exp>, <exp-list_tail>]  |  [<exp>, <exp-list_tail>]  |    [<exp>, <exp-list_tail>]  |  [{epsilon}]  |        [<exp>, <exp-list_tail>]  |        [<exp>, <exp-list_tail>]  |          [<exp>, <exp-list_tail>]  |                                    |     
	                         |                                  |               |                                                     |                                                   |          |                                        |               |                                   |                            |                              |               |                                  |                                  |                                    |                                    |     
	  <exp>                  |                                  |               |                                                     |                                                   |          |                  [<term>, <exp_tail>]  |  [{epsilon}]  |                      [{epsilon}]  |      [<term>, <exp_tail>]  |        [<term>, <exp_tail>]  |  [{epsilon}]  |            [<term>, <exp_tail>]  |            [<term>, <exp_tail>]  |              [<term>, <exp_tail>]  |                                    |     
	                         |                                  |               |                                                     |                                                   |          |                                        |               |                                   |                            |                              |               |                                  |                                  |                                    |                                    |     
	  <term>                 |                                  |               |                                                     |                                                   |          |                [<term1>, <term_tail>]  |  [{epsilon}]  |                      [{epsilon}]  |    [<term1>, <term_tail>]  |      [<term1>, <term_tail>]  |  [{epsilon}]  |                     [{epsilon}]  |                     [{epsilon}]  |            [<term1>, <term_tail>]  |                                    |     
	                         |                                  |               |                                                     |                                                   |          |                                        |               |                                   |                            |                              |               |                                  |                                  |                                    |                                    |     
	  <term1>                |                                  |               |                                                     |                                                   |          |              [<factor>, <term1_tail>]  |               |                                   |  [<factor>, <term1_tail>]  |    [<factor>, <term1_tail>]  |               |                                  |                                  |                                    |                                    |     
	                         |                                  |               |                                                     |                                                   |          |                                        |               |                                   |                            |                              |               |                                  |                                  |                                    |                                    |     
	  <add-op>               |                                  |               |                                                     |                                                   |          |                                        |               |                                   |                            |                              |               |                          [PLUS]  |                         [MINUS]  |                                    |                                    |     
	                         |                                  |               |                                                     |                                                   |          |                                        |               |                                   |                            |                              |               |                                  |                                  |                                    |                                    |     
	  <factor>               |                                  |               |                                                     |                                                   |          |                                  [ID]  |               |                                   |                  [INTNUM]  |  [LEFTPAR, <exp>, RIGHTPAR]  |               |                                  |                                  |                                    |                                    |     
	                         |                                  |               |                                                     |                                                   |          |                                        |               |                                   |                            |                              |               |                                  |                                  |                                    |                                    |     
	  <statement-list_tail>  |                                  |  [{epsilon}]  |               [<statement>, <statement-list_tail>]  |             [<statement>, <statement-list_tail>]  |          |  [<statement>, <statement-list_tail>]  |               |                                   |                            |                              |               |                                  |                                  |                                    |                                    |     
	                         |                                  |               |                                                     |                                                   |          |                                        |               |                                   |                            |                              |               |                                  |                                  |                                    |                                    |     
	  <id-list_tail>         |                                  |               |                                                     |                                                   |          |                                        |               |      [COMMA, ID, <id-list_tail>]  |                            |                              |  [{epsilon}]  |                                  |                                  |                                    |                                    |     
	                         |                                  |               |                                                     |                                                   |          |                                        |               |                                   |                            |                              |               |                                  |                                  |                                    |                                    |     
	  <exp-list_tail>        |                                  |               |                                                     |                                                   |          |                                        |               |  [COMMA, <exp>, <exp-list_tail>]  |                            |                              |  [{epsilon}]  |                                  |                                  |                                    |                                    |     
	                         |                                  |               |                                                     |                                                   |          |                                        |               |                                   |                            |                              |               |                                  |                                  |                                    |                                    |     
	  <exp_tail>             |                                  |               |                                                     |                                                   |          |                                        |  [{epsilon}]  |                      [{epsilon}]  |                            |                              |  [{epsilon}]  |  [<add-op>, <term>, <exp_tail>]  |  [<add-op>, <term>, <exp_tail>]  |                                    |                                    |     
	                         |                                  |               |                                                     |                                                   |          |                                        |               |                                   |                            |                              |               |                                  |                                  |                                    |                                    |     
	  <term_tail>            |                                  |               |                                                     |                                                   |          |                                        |  [{epsilon}]  |                      [{epsilon}]  |                            |                              |  [{epsilon}]  |                     [{epsilon}]  |                     [{epsilon}]  |  [MULTIPLY, <term1>, <term_tail>]  |                                    |     
	                         |                                  |               |                                                     |                                                   |          |                                        |               |                                   |                            |                              |               |                                  |                                  |                                    |                                    |     
	  <term1_tail>           |                                  |               |                                                     |                                                   |          |                                        |  [{epsilon}]  |                      [{epsilon}]  |                            |                              |  [{epsilon}]  |                     [{epsilon}]  |                     [{epsilon}]  |                       [{epsilon}]  |  [MODULO, <factor>, <term1_tail>]  |     

#### Parsing Stack                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            
	[<Tiny-program>]
	Popped <Tiny-program>
	[END, <statement-list>, BEGIN]
	Popped BEGIN
	Parsed T_BEGIN
	[END, <statement-list>]
	Popped <statement-list>
	[END, <statement-list_tail>, <statement>]
	Popped <statement>
	[END, <statement-list_tail>, SEMICOLON, <exp>, ASSIGN, ID]
	Popped ID
	Parsed T_ID
	[END, <statement-list_tail>, SEMICOLON, <exp>, ASSIGN]
	Popped ASSIGN
	Parsed T_ASSIGN
	[END, <statement-list_tail>, SEMICOLON, <exp>]
	Popped <exp>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term>]
	Popped <term>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>, <term1>]
	Popped <term1>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>, <term1_tail>, <factor>]
	Popped <factor>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>, <term1_tail>, INTNUM]
	Popped INTNUM
	Parsed T_INTNUM
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>, <term1_tail>]
	Popped <term1_tail>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>, {epsilon}]
	Popped {epsilon}
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>]
	Popped <term_tail>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, {epsilon}]
	Popped {epsilon}
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>]
	Popped <exp_tail>
	[END, <statement-list_tail>, SEMICOLON, {epsilon}]
	Popped {epsilon}
	[END, <statement-list_tail>, SEMICOLON]
	Popped SEMICOLON
	Parsed T_SEMICOLON
	[END, <statement-list_tail>]
	Popped <statement-list_tail>
	[END, <statement-list_tail>, <statement>]
	Popped <statement>
	[END, <statement-list_tail>, SEMICOLON, <exp>, ASSIGN, ID]
	Popped ID
	Parsed T_ID
	[END, <statement-list_tail>, SEMICOLON, <exp>, ASSIGN]
	Popped ASSIGN
	Parsed T_ASSIGN
	[END, <statement-list_tail>, SEMICOLON, <exp>]
	Popped <exp>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term>]
	Popped <term>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>, <term1>]
	Popped <term1>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>, <term1_tail>, <factor>]
	Popped <factor>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>, <term1_tail>, INTNUM]
	Popped INTNUM
	Parsed T_INTNUM
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>, <term1_tail>]
	Popped <term1_tail>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>, {epsilon}]
	Popped {epsilon}
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>]
	Popped <term_tail>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, {epsilon}]
	Popped {epsilon}
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>]
	Popped <exp_tail>
	[END, <statement-list_tail>, SEMICOLON, {epsilon}]
	Popped {epsilon}
	[END, <statement-list_tail>, SEMICOLON]
	Popped SEMICOLON
	Parsed T_SEMICOLON
	[END, <statement-list_tail>]
	Popped <statement-list_tail>
	[END, <statement-list_tail>, <statement>]
	Popped <statement>
	[END, <statement-list_tail>, SEMICOLON, RIGHTPAR, <id-list>, LEFTPAR, READ]
	Popped READ
	Parsed T_READ
	[END, <statement-list_tail>, SEMICOLON, RIGHTPAR, <id-list>, LEFTPAR]
	Popped LEFTPAR
	Parsed T_LEFTPAR
	[END, <statement-list_tail>, SEMICOLON, RIGHTPAR, <id-list>]
	Popped <id-list>
	[END, <statement-list_tail>, SEMICOLON, RIGHTPAR, <id-list_tail>, ID]
	Popped ID
	Parsed T_ID
	[END, <statement-list_tail>, SEMICOLON, RIGHTPAR, <id-list_tail>]
	Popped <id-list_tail>
	[END, <statement-list_tail>, SEMICOLON, RIGHTPAR, {epsilon}]
	Popped {epsilon}
	[END, <statement-list_tail>, SEMICOLON, RIGHTPAR]
	Popped RIGHTPAR
	Parsed T_RIGHTPAR
	[END, <statement-list_tail>, SEMICOLON]
	Popped SEMICOLON
	Parsed T_SEMICOLON
	[END, <statement-list_tail>]
	Popped <statement-list_tail>
	[END, <statement-list_tail>, <statement>]
	Popped <statement>
	[END, <statement-list_tail>, SEMICOLON, <exp>, ASSIGN, ID]
	Popped ID
	Parsed T_ID
	[END, <statement-list_tail>, SEMICOLON, <exp>, ASSIGN]
	Popped ASSIGN
	Parsed T_ASSIGN
	[END, <statement-list_tail>, SEMICOLON, <exp>]
	Popped <exp>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term>]
	Popped <term>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>, <term1>]
	Popped <term1>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>, <term1_tail>, <factor>]
	Popped <factor>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>, <term1_tail>, INTNUM]
	Popped INTNUM
	Parsed T_INTNUM
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>, <term1_tail>]
	Popped <term1_tail>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>, {epsilon}]
	Popped {epsilon}
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>]
	Popped <term_tail>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, {epsilon}]
	Popped {epsilon}
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>]
	Popped <exp_tail>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term>, <add-op>]
	Popped <add-op>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term>, PLUS]
	Popped PLUS
	Parsed T_PLUS
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term>]
	Popped <term>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>, <term1>]
	Popped <term1>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>, <term1_tail>, <factor>]
	Popped <factor>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>, <term1_tail>, ID]
	Popped ID
	Parsed T_ID
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>, <term1_tail>]
	Popped <term1_tail>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>, {epsilon}]
	Popped {epsilon}
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>]
	Popped <term_tail>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, {epsilon}]
	Popped {epsilon}
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>]
	Popped <exp_tail>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term>, <add-op>]
	Popped <add-op>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term>, PLUS]
	Popped PLUS
	Parsed T_PLUS
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term>]
	Popped <term>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>, <term1>]
	Popped <term1>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>, <term1_tail>, <factor>]
	Popped <factor>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>, <term1_tail>, RIGHTPAR, <exp>, LEFTPAR]
	Popped LEFTPAR
	Parsed T_LEFTPAR
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>, <term1_tail>, RIGHTPAR, <exp>]
	Popped <exp>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>, <term1_tail>, RIGHTPAR, <exp_tail>, <term>]
	Popped <term>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>, <term1_tail>, RIGHTPAR, <exp_tail>, <term_tail>, <term1>]
	Popped <term1>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>, <term1_tail>, RIGHTPAR, <exp_tail>, <term_tail>, <term1_tail>, <factor>]
	Popped <factor>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>, <term1_tail>, RIGHTPAR, <exp_tail>, <term_tail>, <term1_tail>, INTNUM]
	Popped INTNUM
	Parsed T_INTNUM
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>, <term1_tail>, RIGHTPAR, <exp_tail>, <term_tail>, <term1_tail>]
	Popped <term1_tail>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>, <term1_tail>, RIGHTPAR, <exp_tail>, <term_tail>, {epsilon}]
	Popped {epsilon}
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>, <term1_tail>, RIGHTPAR, <exp_tail>, <term_tail>]
	Popped <term_tail>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>, <term1_tail>, RIGHTPAR, <exp_tail>, <term_tail>, <term1>, MULTIPLY]
	Popped MULTIPLY
	Parsed T_MULTIPLY
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>, <term1_tail>, RIGHTPAR, <exp_tail>, <term_tail>, <term1>]
	Popped <term1>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>, <term1_tail>, RIGHTPAR, <exp_tail>, <term_tail>, <term1_tail>, <factor>]
	Popped <factor>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>, <term1_tail>, RIGHTPAR, <exp_tail>, <term_tail>, <term1_tail>, INTNUM]
	Popped INTNUM
	Parsed T_INTNUM
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>, <term1_tail>, RIGHTPAR, <exp_tail>, <term_tail>, <term1_tail>]
	Popped <term1_tail>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>, <term1_tail>, RIGHTPAR, <exp_tail>, <term_tail>, <term1_tail>, <factor>, MODULO]
	Popped MODULO
	Parsed T_MODULO
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>, <term1_tail>, RIGHTPAR, <exp_tail>, <term_tail>, <term1_tail>, <factor>]
	Popped <factor>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>, <term1_tail>, RIGHTPAR, <exp_tail>, <term_tail>, <term1_tail>, INTNUM]
	Popped INTNUM
	Parsed T_INTNUM
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>, <term1_tail>, RIGHTPAR, <exp_tail>, <term_tail>, <term1_tail>]
	Popped <term1_tail>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>, <term1_tail>, RIGHTPAR, <exp_tail>, <term_tail>, {epsilon}]
	Popped {epsilon}
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>, <term1_tail>, RIGHTPAR, <exp_tail>, <term_tail>]
	Popped <term_tail>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>, <term1_tail>, RIGHTPAR, <exp_tail>, {epsilon}]
	Popped {epsilon}
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>, <term1_tail>, RIGHTPAR, <exp_tail>]
	Popped <exp_tail>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>, <term1_tail>, RIGHTPAR, {epsilon}]
	Popped {epsilon}
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>, <term1_tail>, RIGHTPAR]
	Popped RIGHTPAR
	Parsed T_RIGHTPAR
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>, <term1_tail>]
	Popped <term1_tail>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>, {epsilon}]
	Popped {epsilon}
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, <term_tail>]
	Popped <term_tail>
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>, {epsilon}]
	Popped {epsilon}
	[END, <statement-list_tail>, SEMICOLON, <exp_tail>]
	Popped <exp_tail>
	[END, <statement-list_tail>, SEMICOLON, {epsilon}]
	Popped {epsilon}
	[END, <statement-list_tail>, SEMICOLON]
	Popped SEMICOLON
	Parsed T_SEMICOLON
	[END, <statement-list_tail>]
	Popped <statement-list_tail>
	[END, {epsilon}]
	Popped {epsilon}
	[END]
	Popped END
	Successful parse!                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         
