package com.douglas;

// CMSC 330
// Project 1
// Duane J. Jarc
// March 25, 2014
// Netbeans under Windows 8

// Class that defines a syntax error

class SyntaxError extends Exception
{
    
    // Constructor that creates a synatx error object given the line number and error

    public SyntaxError(int line, String description)
    {
        super("Line: " + line + " " + description);
    }
}