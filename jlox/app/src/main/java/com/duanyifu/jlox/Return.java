package com.duanyifu.jlox;

/**
 * @author yifuduan on 2021/9/16
 */
public class Return extends RuntimeException {
    final Object value;

    Return(Object value) {
        super(null, null, false, false);
        this.value = value;
    }
}
