package com.douglas;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.Stack;

/**
 * Recursive Descent Parser
 */
public class RCP {
    private Lexer lexer; // An instance of the lexer
    private JFrame frame; // The default Container / GUI element
    private Stack<java.awt.Container> containers; // A stack that will contain nested Panels
    private Container container; // The current container

    /**
     * Constructor
     *
     * @param file path
     */
    public RCP(String file) {
        // Initialize the stack of containers
        containers = new Stack<>();
        // Open the file to read
        try {
            lexer = new Lexer(file);
        } catch (FileNotFoundException e) { e.printStackTrace(); }
    }

    /**
     * Initiates the parsing and sets the GUI to be visible
     * @throws SyntaxError
     * @throws IOException
     */
    public void init() throws SyntaxError, IOException {
        window();
        frame.setVisible(true);
    }

    /**
     * Get the current container (JFrame or JPanel)

     * @return a container
     */
    public Container getCurrentContainer() {
        return container;
    }

    /**
     * Add a nested container/JPanel, add the current container to the stack
     * and make this the current container
     * @param container
     */
    public void addAndSetCurrentContainer(Container container) {
        if (this.container != null) {
            containers.push(getCurrentContainer());
        }
        this.container = container;
    }

    /**
     * Creates the main window / JFrame, parses the name and dimension
     * calls layout() to parse the layout, and calls widgets() to parse
     * the widgets.
     * @return True if it can parse, false if it cannot
     * @throws SyntaxError
     * @throws IOException
     */
    private boolean window() throws SyntaxError, IOException {
        double width;
        double height;

        if (lexer.getNextToken() == Token.WINDOW
                && lexer.getNextToken() == Token.STRING) {
            frame = new JFrame(lexer.getLexeme());
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            addAndSetCurrentContainer(frame);

            if (lexer.getNextToken() == Token.LEFT_PAREN
                    && lexer.getNextToken() == Token.NUMBER) {
                width = lexer.getValue();
                if (lexer.getNextToken() == Token.COMMA
                        && lexer.getNextToken() == Token.NUMBER) {
                    height = lexer.getValue();
                    if (lexer.getNextToken() == Token.RIGHT_PAREN) {
                        frame.setSize((int)width, (int)height);
                    }
                }
            }
        }

        if (!layout()) {
            return false;
        }

        widgets();

        return false;
    }

    /**
     * Parses the layout, including checking for layout type via function calls
     * @return True if it can parse, false if it cannot
     * @throws SyntaxError
     * @throws IOException
     */
    private boolean layout() throws SyntaxError, IOException {
        Token firstLayout = lexer.getNextToken();
        String firstLayoutLexeme = lexer.getLexeme();
        Token secondLayout = lexer.getNextToken();
        String secondLayoutLexeme = lexer.getLexeme();

        if (firstLayout == Token.LAYOUT){
            return (layoutTypeFlow(secondLayout) || layoutTypeGrid(secondLayout));
        }

        return false;
    }

    /**
     * Parses for layout type flow
     * @param layout
     * @return True if it can parse, false if it cannot
     * @throws SyntaxError
     * @throws IOException
     */
    private boolean layoutTypeFlow(Token layout) throws SyntaxError, IOException {
        if (layout == Token.FLOW && lexer.getNextToken() == Token.COLON) {
            Container container = getCurrentContainer();
            container.setLayout(new FlowLayout());
            return true;
        }

        return false;
    }

    /**
     * Parses for layout type grid
     * @param layout
     * @return
     * @throws SyntaxError
     * @throws IOException
     */
    private boolean layoutTypeGrid(Token layout)  throws SyntaxError, IOException {
        double rows, cols, hgap, vgap;

        // Walks through the layout type grid syntax of
        // Layout Grid(rows, cols, horizontal gap, vertical gap):
        if (layout == Token.GRID && lexer.getNextToken() == Token.LEFT_PAREN) {
            if (lexer.getNextToken() == Token.NUMBER) {
                rows = lexer.getValue();
                if (lexer.getNextToken() == Token.COMMA
                        && lexer.getNextToken() == Token.NUMBER) {
                    cols = lexer.getValue();

                    // Checking to see if the next token is a ')'
                    // or if it's a comma followed by a number
                    Token optionalParams = lexer.getNextToken();
                    // Branch that finds a ')'
                    if (optionalParams == Token.RIGHT_PAREN) {
                        if (lexer.getNextToken() == Token.COLON) {
                            Container container = getCurrentContainer();
                            container.setLayout(new GridLayout((int) rows, (int) cols));
                            return true;
                        }
                        else {
                            throw new SyntaxError(lexer.lineNo(), "Missing a colon");
                        }
                    }

                    // Branch that finds a comma followed by a number
                    else if (optionalParams == Token.COMMA
                            && lexer.getNextToken() == Token.NUMBER) {
                        hgap = lexer.getValue();

                        if (lexer.getNextToken() == Token.COMMA
                                && lexer.getNextToken() == Token.NUMBER) {
                            vgap = lexer.getValue();
                            if (lexer.getNextToken() == Token.RIGHT_PAREN
                                    && lexer.getNextToken() == Token.COLON) {
                                Container container = getCurrentContainer();
                                container.setLayout(
                                        new GridLayout((int)rows, (int)cols,
                                                (int) hgap, (int) vgap));
                                return true;
                            }
                            else {
                                throw new SyntaxError(lexer.lineNo(), "Missing a colon");
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Recursive Widgets function. Effectively checks first for widget() && widgets()
     * the for widget().
     * @return True if it can parse, false if it cannot
     * @throws SyntaxError
     * @throws IOException
     */
    private boolean widgets() throws SyntaxError, IOException {
        return (widget() && widgets());
    }

    /**
     * Checks for each type of widget according to the grammar:
     * Button STRING ';' |
     * Group radio_buttons End ';' |
     * Label STRING ';' |
     * Panel layout widgets End ';' |
     * Textfield NUMBER ';'
     *
     * @return True if it can parse, false if it cannot
     * @throws SyntaxError
     * @throws IOException
     */
    private boolean widget() throws SyntaxError, IOException {
        Token current = lexer.getNextToken();
        return (button(current) || group(current) ||
                label(current) || panel(current) || textField(current));
    }

    /**
     * Parses a button
     * @param token the next token
     * @return True if it can parse, false if it cannot
     * @throws SyntaxError
     * @throws IOException
     */
    private boolean button(Token token) throws SyntaxError, IOException {
        String text;

        if (token == Token.BUTTON && lexer.getNextToken() == Token.STRING) {
            text = lexer.getLexeme();
            if (lexer.getNextToken() == Token.SEMICOLON) {
                Container container = getCurrentContainer();
                container.add(new JButton(text));
                return true;
            }
            else {
                throw new SyntaxError(lexer.lineNo(), "Missing a semi-colon");
            }
        }

        return false;
    }

    /**
     * Parses a group
     * @param token the next token
     * @return True if it can parse, false if it cannot
     * @throws SyntaxError
     * @throws IOException
     */
    private boolean group(Token token) throws SyntaxError, IOException {
        if (token == Token.GROUP) {
            while (radioButtons(lexer.getNextToken())) {
            }
            if (lexer.getLexeme().equals("End") && lexer.getNextToken() == Token.SEMICOLON){
                return true;
            }
            else {
                throw new SyntaxError(lexer.lineNo(), "Missing a semi-colon");
            }
        }
        return false;
    }

    /**
     * Parses radio buttons recursively, according to the grammar:
     *  radio_button radio_buttons | radio_button
     * @param token the next token
     * @return True if it can parse, false if it cannot
     * @throws SyntaxError
     * @throws IOException
     */
    private boolean radioButtons(Token token) throws SyntaxError, IOException {
        return (radioButton(token) && radioButtons(lexer.getNextToken()));
    }

    /**
     * Parses radio button
     * @param token the next token
     * @return True if it can parse, false if it cannot
     * @throws SyntaxError
     * @throws IOException
     */
    private boolean radioButton(Token token) throws SyntaxError, IOException {
        String text;

        if (token == Token.RADIO && lexer.getNextToken() == Token.STRING) {
            text = lexer.getLexeme();
            if (lexer.getNextToken() == Token.SEMICOLON) {
                java.awt.Container container = getCurrentContainer();
                container.add(new JRadioButton(text));
                return true;
            }
            else {
                throw new SyntaxError(lexer.lineNo(), "Missing a semi-colon");
            }
        }

        return false;
    }

    /**
     * Parses a label
     * @param token the next token
     * @return True if it can parse, false if it cannot
     * @throws SyntaxError
     * @throws IOException
     */
    private boolean label(Token token) throws SyntaxError, IOException {
        String text;

        if (token == Token.LABEL && lexer.getNextToken() == Token.STRING) {
            text = lexer.getLexeme();
            if (lexer.getNextToken() == Token.SEMICOLON){
                java.awt.Container container = getCurrentContainer();
                container.add(new JLabel(text));
                return true;
            } else {
                throw new SyntaxError(lexer.lineNo(), "Missing semi-colon");
            }
        }

        return false;
    }

    /**
     * Parses a panel, recursively according to the grammar:
     * Panel layout widgets End ';'
     * @param token the next token
     * @return True if it can parse, false if it cannot
     * @throws SyntaxError
     * @throws IOException
     */
    private boolean panel(Token token) throws SyntaxError, IOException {
        JPanel panel;
        if (token == Token.PANEL) {
            panel = new JPanel();
            addAndSetCurrentContainer(panel);
            if (layout()) {
                while (widgets()) {
                }
                containers.get(containers.size() - 1).add(getCurrentContainer());

                /// end is the current here. need to get that.
                ////
                if (lexer.getLexeme().equals("End") && lexer.getNextToken() == Token.SEMICOLON) {
                    container = containers.pop();
                    return true;
                }
                else {
                    throw new SyntaxError(lexer.lineNo(), "Syntax Error");
                }
            }
        }

        return false;
    }

    /**
     * Parses a text field
     * @param token the next token
     * @return True if it can parse, false if it cannot
     * @throws SyntaxError
     * @throws IOException
     */
    private boolean textField(Token token) throws SyntaxError, IOException {
        double length;

        if (token == Token.TEXTFIELD) {
            if (lexer.getNextToken() == Token.NUMBER) {
                length = lexer.getValue();
                if (lexer.getNextToken() == Token.SEMICOLON) {
                    Container container = getCurrentContainer();
                    container.add(new JTextField((int)length));
                    return true;
                }
                else {
                    throw new SyntaxError(lexer.lineNo(), "Missing a semi-colon");
                }
            }
        }

        return false;
    }

    public static void main(String[] args) throws SyntaxError, IOException {
        Scanner input = new Scanner(System.in);

        System.out.println("Enter the directory of the file you wish to parse: ");
        String file = input.next();

        RCP rcp = new RCP(file);
        rcp.init();
    }
}
