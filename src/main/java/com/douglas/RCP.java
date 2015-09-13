package com.douglas;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Recursive Descent Parser
 */
public class RCP {
    protected Lexer lexer;
    JFrame frame;

    public RCP() {
        try {
            lexer = new Lexer("C:\\Users\\580782\\Documents\\Personal\\CMSC330-Project1\\CMSC330-Project1\\src\\main\\resources\\example_input_file.txt");
        } catch (FileNotFoundException e) { e.printStackTrace(); }
    }

    public void init() throws SyntaxError, IOException {
        window();
        frame.setVisible(true);
    }

    private boolean window() throws SyntaxError, IOException {
        if (lexer.getNextToken() == Token.WINDOW
                && lexer.getNextToken() == Token.STRING) {
            frame = new JFrame(lexer.getLexeme());
            if (lexer.getNextToken() == Token.LEFT_PAREN
                    && lexer.getNextToken() == Token.NUMBER) {
                double width = lexer.getValue();
                if (lexer.getNextToken() == Token.COMMA
                        && lexer.getNextToken() == Token.NUMBER) {
                    frame.setSize((int)width, (int)lexer.getValue());
                    if (lexer.getNextToken() == Token.RIGHT_PAREN) {

                    }
                }
            }
        }

        if (layout()) {

        }

        return false;
    }

    private boolean layout() throws SyntaxError, IOException {
        if (lexer.getNextToken() == Token.STRING && lexer.getLexeme().equals(Token.LAYOUT)){
            return (layoutTypeFlow() || layoutTypeGrid());
        }

        return false;
    }

    private boolean layoutTypeFlow() throws SyntaxError, IOException {
        if (lexer.getNextToken() == Token.LAYOUT && lexer.getNextToken() == Token.FLOW) {
            frame.setLayout(new FlowLayout());
            return true;
        }

        return false;
    }

    // need to have dimension
    // already selected getNextToken... how do I get it back?
    private boolean layoutTypeGrid() {


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
