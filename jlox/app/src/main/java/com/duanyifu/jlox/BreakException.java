package com.duanyifu.jlox;

/**
 * @author yifuduan on 2021/9/8
 */
public class BreakException extends RuntimeException {
    Stmt surrounding;

    public BreakException() {
    }

    public BreakException(String message) {
        super(message);
    }

    public BreakException(Stmt surrounding, String message) {
        super(message);
        this.surrounding = surrounding;
    }
}
