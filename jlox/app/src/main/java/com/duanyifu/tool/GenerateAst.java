package com.duanyifu.tool;

import java.io.IOException;
import java.util.List;

/**
 * @author yifuduan on 2021/8/18
 */
public class GenerateAst {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage generate_ast <output directory>");
            System.exit(64);
        }

        String outputDir = args[0];

        defineAst(outputDir, "Expr", List.of(
                "Binary   : Expr left, Token operator, Expr right",
                "Grouping : Expr expression",
                "Literal  : Object value",
                "Unary    : Token operator, Expr right"
        ));
    }

    static void defineAst(String dir, String filename, List<String> syntaxList) {

    }
}
