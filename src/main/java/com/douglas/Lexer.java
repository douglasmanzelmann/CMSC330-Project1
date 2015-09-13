package com.douglas;

// CMSC 330
// Project 1
// Duane J. Jarc
// March 25, 2014

// Netbeans under Windows 8

import java.io.*;

// Tnis class provides the lexical analyzer for project 1

class Lexer
{
    private static final int KEYWORDS = 11;
    private StreamTokenizer tokenizer;
    private String punctuation = ",:;.()";
    private Token[] punctuationTokens =
    {
        Token.COMMA, Token.COLON, Token.SEMICOLON, Token.PERIOD, Token.LEFT_PAREN, Token.RIGHT_PAREN
    };

    // Constructor that creates a lexical analyzer object given the source file

    public Lexer(String fileName) throws FileNotFoundException
    {
        tokenizer = new StreamTokenizer(new FileReader(fileName));
        tokenizer.ordinaryChar('.');
        tokenizer.quoteChar('"');
    }

    // Returns the next token in the input stream
    
    public Token getNextToken() throws SyntaxError, IOException
    {
        int token = tokenizer.nextToken();
        switch (token)
        {
            case StreamTokenizer.TT_NUMBER:
                return Token.NUMBER;
            case StreamTokenizer.TT_WORD:
                for (Token aToken : Token.values())
                {
                    if (aToken.ordinal() == KEYWORDS)
                        break;
                    if (aToken.name().equals(tokenizer.sval.toUpperCase()))
                        return aToken;
                }
                throw new SyntaxError(lineNo(), "Invalid token " + getLexeme());
            case StreamTokenizer.TT_EOF:
                return Token.EOF;
            case '"':
                return Token.STRING;
            default:
                for (int i = 0; i < punctuation.length(); i++)
                    if (token == punctuation.charAt(i))
                        return punctuationTokens[i];
        }
        return Token.EOF;
    }

    // Returns the lexeme associated with the current token
    
    public String getLexeme()
    {
        return tokenizer.sval;
    }

    // Returns the numeric value of the current token for numeric tokens
    
    public double getValue()
    {
        return tokenizer.nval;
    }

    // Returns the currebt line of the input file
    
    public int lineNo()
    {
        return tokenizer.lineno();
    }

    public static void main(String[] args) throws SyntaxError, IOException {
        Lexer lexer;
        try {
            lexer = new Lexer("C:\\Users\\580782\\Documents\\Personal\\CMSC330-Project1\\CMSC330-Project1\\src\\main\\resources\\example_input_file.txt");
            Token current = lexer.getNextToken();
            while (current != Token.EOF) {
                System.out.println("Current: " + current);
                if (lexer.getLexeme() != null)
                    System.out.println("Lexeme: " + lexer.getLexeme());
                else
                    System.out.println("Value: " + lexer.getValue());

                current = lexer.getNextToken();
            }
        } catch (FileNotFoundException e) { e.printStackTrace(); }
    }
}
