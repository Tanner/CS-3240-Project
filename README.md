# LL1 Parser
Created by Tanner Smith and Ryan Ashcraft.

# Instructions
To run the project, simply compile and run `java LL1Parser [path to grammar file] [path to file to input file]`.

For example: `java LL1Parser grammar.txt test.txt`

An optional `-v` argument is not required and can be appended on the end of the above example to enable verbose mode.

Note: In order to see the First and Follow sets, you must enable verbose mode.

# Output

After successfully parsing a grammar, the program will create two files:

* A tokenized version of the input file will be written in the current working directory with the extension of .tok.
* A text file containing the parsing table.

# Examples
Example grammar files and files to parse can be found in the tests/ directory.