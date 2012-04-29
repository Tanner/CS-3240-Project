# LL1 Parser
Created by Tanner Smith and Ryan Ashcraft.

# Instructions
To run the project, simply compile `LL1Parser.java` and run `java LL1Parser [path to grammar file] [path to file to input file]`.

For example:

    cd src/
    javac LL1Parser.java
    java LL1Parser ../tests/tiny_precedence/grammar.txt ../tests/tiny_precedence/test.txt

An optional `-v` argument is an optional runtime argument that enable verbose mode. Verbose mode prints the revised grammar, the first and follow sets, the parsing table, and the parsing operations to the the console.

# Output

After successfully parsing a grammar, the program will create two files in the active directory:

* A tokenized version of the input file will be written in the current working directory with the extension of `.tok`.
* A text file containing the parsing table named `parsing_table.txt`.

Note: For some grammars, the parsing table can be rather large. In these cases, it will be difficult to read the parsing table unless word wrap is disabled. For viewing the parsing table in the terminal without word wrap, run the command `less -S parsing_table.txt`.

# Examples
Example grammar files and programs to parse can be found in the tests/ directory.
