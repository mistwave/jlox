package com.duanyifu.jlox;


import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static com.duanyifu.jlox.TokenType.*;

/**
 * @author yifuduan on 2021/8/31
 */
public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }


    List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(declaration());
        }
        return statements;
    }

    private Stmt declaration() {
        try {
            if (match(VAR)) return varDeclaration();
            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    private Stmt varDeclaration() {
        Token name = consume(IDENTIFIER, "Expect variable name");
        Expr initializer = null;

        if (match(EQUAL)) {
            initializer = expression();
        }

        consume(SEMICOLON, "Expect ';' after variable declaration.");

        return new Stmt.Var(name, initializer);
    }

    private Stmt statement() {
        if (match(FOR)) return forStatement();
        if (match(IF)) return ifStatement();
        if (match(PRINT)) return printStatement();
        if (match(WHILE)) return whileStatement();
        if (match(LEFT_BRACE)) return new Stmt.Block(block());

        return expressionStatement();
    }

    private Stmt forStatement() {
        consume(LEFT_PAREN, "Excepted '(' after 'for'.");
        Stmt initializer;
        if (match(SEMICOLON)) {
            initializer = null;
        } else if (match(VAR)) {
            initializer = varDeclaration();
        } else {
            initializer = expressionStatement();
        }

        Expr condition = null;
        if (!check(SEMICOLON)) {
            condition = expression();
        }
        consume(SEMICOLON, "Expect ';' after loop condition");

        Expr increment = null;
        if (!check(RIGHT_PAREN)) {
            increment = expression();
        }

        consume(RIGHT_PAREN, "Expect ')' after for clauses.");
        Stmt body = statement();

        if (increment != null) {
            body = new Stmt.Block(List.of(body, new Stmt.Expression(increment)));
        }

        if (condition == null) {
            condition = new Expr.Literal(true);
        }

        body = new Stmt.While(condition, body);

        if (initializer != null) {
            body = new Stmt.Block(List.of(initializer, body));
        }

        return body;
    }

    private Stmt ifStatement() {
        consume(LEFT_PAREN, "Excepted '(' after 'if'.");
        Expr condition = expression();
        consume(RIGHT_PAREN, "Excepted ')' after if condition.");

        Stmt thenBranch = statement();
        Stmt elseBranch = null;
        if (match(ELSE)) {
            elseBranch = statement();
        }

        return new Stmt.If(condition, thenBranch, elseBranch);
    }

    private Stmt printStatement() {
        Expr value = expression();
        consume(SEMICOLON, "Except ';' after value.");
        return new Stmt.Print(value);
    }

    private Stmt whileStatement() {
        consume(LEFT_PAREN, "Excepted '(' after 'while'.");
        Expr condition = expression();
        consume(RIGHT_PAREN, "Excepted ')' after condition.");
        Stmt body = statement();

        return new Stmt.While(condition, body);
    }

    private List<Stmt> block() {
        List<Stmt> statements = new ArrayList<>();
        while (!check(RIGHT_BRACE) && !isAtEnd()) {
            statements.add(declaration());
        }

        consume(RIGHT_BRACE, "Expect '}' after block.");
        return statements;
    }

    private Stmt expressionStatement() {
        Expr value = expression();
        consume(SEMICOLON, "Except ';' after value.");
        return new Stmt.Expression(value);
    }

    private Expr expression() {
        return assignment();
    }

    private Expr assignment() {
        Expr expr = or();

        if (match(EQUAL)) {
            Token equals = previous();
            Expr value = assignment();

            if (expr instanceof Expr.Variable) {
                Token name = ((Expr.Variable) expr).name;
                return new Expr.Assign(name, value);
            }

            // dont throw
            error(equals, "Invalid assignment target.");
        }

        return expr;
    }

    private Expr or() {
        return binaryOp(this::and, OR);
    }

    private Expr and() {
        return binaryOp(this::equality, AND);
    }

    private Expr equality() {
        return binaryOp(this::comparison, BANG_EQUAL, EQUAL_EQUAL);
    }

    private Expr comparison() {
        return binaryOp(this::term, GREATER, GREATER_EQUAL, LESS, LESS_EQUAL);
    }

    private Expr term() {
        return binaryOp(this::factor, PLUS, MINUS);
    }

    private Expr factor() {
        return binaryOp(this::unary, SLASH, STAR);
    }

    private Expr unary() {
        if (match(BANG, MINUS)) {
            Token operator = previous();
            Expr right = unary();

            return new Expr.Unary(operator, right);
        }

        return primary();
    }

    private Expr primary() {
        if (match(FALSE)) return new Expr.Literal(false);
        if (match(TRUE)) return new Expr.Literal(true);
        if (match(NIL)) return new Expr.Literal(null);
        if (match(NUMBER, STRING)) return new Expr.Literal(previous().literal);
        if (match(LEFT_PAREN)) {
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }

        if (match(IDENTIFIER)) {
            return new Expr.Variable(previous());
        }

        throw error(peek(), "Expect expression.");
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();

        throw error(peek(), message);
    }

    private ParseError error(Token token, String message) {
        Lox.error(token, message);

        throw new ParseError();
    }

    private static class ParseError extends RuntimeException {
    }


    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            if (previous().type == SEMICOLON) return;
            switch (peek().type) {
                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;
            }

            advance();
        }
    }

    // for s ( (types) s )*
    private Expr binaryOp(Supplier<Expr> s, TokenType... types) {
        Expr expr = s.get();

        while (match(types)) {
            Token operator = previous();
            Expr right = s.get();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) {
            return false;
        } else {
            return peek().type == type;
        }
    }


    private Token advance() {
        if (!isAtEnd()) {
            current += 1;
        }

        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }


}
