package com.duanyifu.jlox;

import lombok.AllArgsConstructor;

/**
 * @author yifuduan on 2021/8/18
 */
public abstract class Expr {


    @AllArgsConstructor
    static class Literal extends Expr {
        final Token token;
    }

    @AllArgsConstructor
    static class Unary extends Expr {
        final Token prefix;
        final Expr expr;
    }

    @AllArgsConstructor
    static class Binary extends Expr {
        final Expr left;
        final Token operator;
        final Expr right;
    }

    @AllArgsConstructor
    static class Grouping extends Expr {
        final Expr expr;
    }




}
