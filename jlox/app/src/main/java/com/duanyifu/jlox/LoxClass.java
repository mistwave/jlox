package com.duanyifu.jlox;

import java.util.List;
import java.util.Map;

public class LoxClass implements LoxCallable{
    final String name;
    private LoxClass superclass;
    private final Map<String, LoxFunction> methods;

    LoxClass(String name, LoxClass superclass, Map<String, LoxFunction> methods) {
        this.name = name;
        this.superclass = superclass;
        this.methods = methods;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        LoxInstance instance = new LoxInstance(this);
        LoxFunction initializer = findMethod("init");
        if (initializer != null) {
            initializer.bind(instance).call(interpreter, arguments);
        }
        return instance;
    }

    @Override
    public int arity() {
        LoxFunction initializer = findMethod("init");
        if (initializer == null) {
            return 0;
        }
        return initializer.arity();
    }

    public LoxFunction findMethod(String lexeme) {
        if (methods.containsKey(lexeme)) {
            return methods.get(lexeme);
        }
        if (superclass != null) {
            return superclass.findMethod(lexeme);
        }

        return null;
    }
}
