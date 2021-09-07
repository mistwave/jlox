package com.duanyifu.jlox;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yifuduan on 2021/9/6
 */
public class Environment {

    final Environment enclosing;

    private final Map<String, Object> values = new HashMap<>();

    private enum NOT_INIT{
        INSTANCE
    }

    public Environment() {
        enclosing = null;
    }

    public Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    void define(String name, Object value) {
        values.put(name, value);
    }

    void define(String name) {
        values.put(name, NOT_INIT.INSTANCE);
    }

    void assign(Token name, Object value) {
        if (values.containsKey(name.lexeme)) {
            values.put(name.lexeme, value);
            return;
        }

        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }

        throw new RuntimeError(name,
                "Undefined variable '" + name.lexeme + "'.");
    }

    Object get(Token name) {
        if (values.containsKey(name.lexeme)) {
           Object value = values.get(name.lexeme);

           if (value == NOT_INIT.INSTANCE) {
               throw new RuntimeError(name,
                       "Uninitialized variable '" + name.lexeme + "'.");
           }

           return value;
        }

        if (enclosing != null) {
            return enclosing.get(name);
        }

        throw new RuntimeError(name,
                "Undefined variable '" + name.lexeme + "'.");
    }
}
