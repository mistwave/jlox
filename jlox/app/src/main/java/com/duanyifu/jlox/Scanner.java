package com.duanyifu.jlox;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.duanyifu.jlox.TokenType.*;

/**
 * @author yifuduan on 2021/8/17
 */
public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    private static final Map<String, TokenType> keywords;

    static {
        keywords = Map.ofEntries(
                Map.entry("and", AND),
                Map.entry("class", CLASS),
                Map.entry("else", ELSE),
                Map.entry("true", TRUE),
                Map.entry("false", FALSE),
                Map.entry("fun", FUN),
                Map.entry("for", FOR),
                Map.entry("if", IF),
                Map.entry("nil", NIL),
                Map.entry("or", OR),
                Map.entry("print", PRINT),
                Map.entry("return", RETURN),
                Map.entry("super", SUPER),
                Map.entry("this", THIS),
                Map.entry("var", VAR),
                Map.entry("while", WHILE)
        );
    }

    private int start = 0;
    private int current = 0;
    private int line = 1;

    public Scanner(String source) {
        this.source = source;
    }

    List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    void scanToken() {
        char c = advance();
        switch (c) {
            case '(':
                addToken(LEFT_PAREN);
                break;
            case ')':
                addToken(RIGHT_PAREN);
                break;
            case '{':
                addToken(LEFT_BRACE);
                break;
            case '}':
                addToken(RIGHT_BRACE);
                break;
            case ',':
                addToken(COMMA);
                break;
            case '.':
                addToken(DOT);
                break;
            case '-':
                addToken(MINUS);
                break;
            case '+':
                addToken(PLUS);
                break;
            case ';':
                addToken(SEMICOLON);
                break;
            case '/':
                if (match('/')) {
                    // comment goes until the end of the line
                    // and don't add token
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else {
                    addToken(SLASH);
                }
                break;
            case '*':
                addToken(STAR);
                break;

            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG);
                break;

            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;

            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;

            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;

            case ' ':
            case '\r':
            case '\t':
                // ignored
                break;

            case '\n':
                line++;
                break;

            case '"': // string literal
                string();
                break;

            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    Lox.error(line, "Unexpected character.");
                }
                break;
        }
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z')
                || (c >= 'A' && c <= 'Z')
                || c == '_';
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAlphaNumeric(char c) {
        return isDigit(c) || isAlpha(c);
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) advance();

        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        addToken(type != null ? type : IDENTIFIER);
    }

    private void number() {
        while (isDigit(peek())) advance();

        // look for a fractional part
        if (peek() == '.' && isDigit(peekNext())) {
            advance();

            while (isDigit(peek())) advance();
        }

        addToken(NUMBER,
                Double.parseDouble(source.substring(start, current)));
    }

    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }

        if (isAtEnd()) {
            Lox.error(line, "Unterminated string.");
            return;
        }

        // for the closing "
        advance();

        // "abc"
        // ^    ^
        // |    |
        // |   current
        // start
        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    private boolean match(char expected) {
        if (isAtEnd()) {
            return false;
        }

        if (source.charAt(current) != expected) {
            return false;
        }

        current++;
        return true;
    }

    char advance() {
        return source.charAt(current++);
    }

    private char peek() {
        if (isAtEnd()) {
            return '\0';
        }

        return source.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    void addToken(TokenType type) {
        addToken(type, null);
    }

    void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }


    boolean isAtEnd() {
        return current >= source.length();
    }


}
