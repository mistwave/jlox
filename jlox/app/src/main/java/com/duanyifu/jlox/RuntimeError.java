package com.duanyifu.jlox;

/**
 * @author yifuduan on 2021/9/3
 */
public class RuntimeError extends RuntimeException {
    final Token token;

    RuntimeError(Token token, String message) {
        super(message);
        this.token = token;
    }
}
