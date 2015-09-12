package com.douglas;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Recursive Descent Parser
 */
public class RCP {
    protected Lexer lexer;

    public RCP() {
        try {
            lexer = new Lexer("C:\\Users\\douga_000\\Documents\\CMSC330\\src\\main\\resources\\example_input_file.txt");
        } catch (FileNotFoundException e) { e.printStackTrace(); }
    }

    public void init() throws SyntaxError, IOException {
        window();
    }

    private boolean window() throws SyntaxError, IOException {
        if (lexer.getNextToken() == Token.WINDOW) {
            System.out.println("wee");
        }

        return false;
    }

    private boolean layout() {

        return false;
    }

    private boolean layoutType() {

        return false;
    }

    private boolean widget() {

        return false;
    }

    private boolean button() {

        return false;
    }

    private boolean label() {

        return false;
    }

    private boolean panel() {

        return false;
    }

    private boolean textField() {

        return false;
    }

    private boolean radioButton() {

        return false;
    }

    public static void main(String[] args) throws SyntaxError, IOException {
        RCP rcp = new RCP();
        rcp.init();

    }
}
