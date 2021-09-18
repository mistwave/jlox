package com.duanyifu.jlox;

import java.util.List;

/**
 * @author yifuduan on 2021/9/13
 */
interface LoxCallable {
    Object call(Interpreter interpreter, List<Object> arguments);

    int arity();
}
