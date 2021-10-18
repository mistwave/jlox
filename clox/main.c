#include <stdio.h>
#include "common.h"
#include "chunk.h"
#include "debug.h"
#include "vm.h"


int main(int argc, const char* argv[]) {

    initVM();

    Chunk chunk;
    initChunk(&chunk);

    int constant = addConstant(&chunk, 1.2);
    writeChunk(&chunk, OP_CONSTANT, 123);
    writeChunk(&chunk, constant, 123);


    constant = addConstant(&chunk, 3.4);
    writeChunk(&chunk, OP_CONSTANT, 123);
    writeChunk(&chunk, constant, 123);

    writeChunk(&chunk, OP_ADD, 123);

    constant = addConstant(&chunk, 5.6);
    writeChunk(&chunk, OP_CONSTANT, 123);
    writeChunk(&chunk, constant, 123);

    writeChunk(&chunk, OP_DIVIDE, 123);

    writeChunk(&chunk, OP_NEGATE, 123);

//    disassembleChunk(&chunk, "test chunk");


    int line;

// ex15.1

// 1 * 2 + 3
    line = 1511;

    writeChunk(&chunk, OP_CONSTANT, line);
    writeChunk(&chunk, addConstant(&chunk, 1), line);
    writeChunk(&chunk, OP_CONSTANT, line);
    writeChunk(&chunk, addConstant(&chunk, 2), line);
    writeChunk(&chunk, OP_MULTIPLY, line);
    writeChunk(&chunk, OP_CONSTANT, line);
    writeChunk(&chunk, addConstant(&chunk, 3), line);
    writeChunk(&chunk, OP_ADD, line);

// 1 + 2 * 3
    line = 1512;
    writeChunk(&chunk, OP_CONSTANT, line);
    writeChunk(&chunk, addConstant(&chunk, 1), line);
    writeChunk(&chunk, OP_CONSTANT, line);
    writeChunk(&chunk, addConstant(&chunk, 2), line);
    writeChunk(&chunk, OP_CONSTANT, line);
    writeChunk(&chunk, addConstant(&chunk, 3), line);
    writeChunk(&chunk, OP_MULTIPLY, line);
    writeChunk(&chunk, OP_ADD, line);
// 3 - 2 - 1
    line = 1513;
    writeChunk(&chunk, OP_CONSTANT, line);
    writeChunk(&chunk, addConstant(&chunk, 3), line);
    writeChunk(&chunk, OP_CONSTANT, line);
    writeChunk(&chunk, addConstant(&chunk, 2), line);
    writeChunk(&chunk, OP_SUBTRACT, line);
    writeChunk(&chunk, OP_CONSTANT, line);
    writeChunk(&chunk, addConstant(&chunk, 1), line);
    writeChunk(&chunk, OP_SUBTRACT, line);
// 1 + 2 * 3 - 4 / -5
    line = 1514;
    writeChunk(&chunk, OP_CONSTANT, line);
    writeChunk(&chunk, addConstant(&chunk, 1), line);
    writeChunk(&chunk, OP_CONSTANT, line);
    writeChunk(&chunk, addConstant(&chunk, 2), line);
    writeChunk(&chunk, OP_CONSTANT, line);
    writeChunk(&chunk, addConstant(&chunk, 3), line);
    writeChunk(&chunk, OP_MULTIPLY, line);
    writeChunk(&chunk, OP_ADD, line);
    writeChunk(&chunk, OP_CONSTANT, line);
    writeChunk(&chunk, addConstant(&chunk, 4), line);
    writeChunk(&chunk, OP_CONSTANT, line);
    writeChunk(&chunk, addConstant(&chunk, 5), line);
    writeChunk(&chunk, OP_NEGATE, line);
    writeChunk(&chunk, OP_DIVIDE, line);
    writeChunk(&chunk, OP_SUBTRACT, line);

    // ex 15.2
    // 4 - 3 * -2

    line = 1521;
    writeChunk(&chunk, OP_CONSTANT, line);
    writeChunk(&chunk, addConstant(&chunk, 4), line);
    writeChunk(&chunk, OP_CONSTANT, line);
    writeChunk(&chunk, addConstant(&chunk, 3), line);
    writeChunk(&chunk, OP_CONSTANT, line);
    writeChunk(&chunk, addConstant(&chunk, 2), line);
    writeChunk(&chunk, OP_NEGATE, line);
    writeChunk(&chunk, OP_MULTIPLY, line);
    writeChunk(&chunk, OP_NEGATE, line);
    writeChunk(&chunk, OP_ADD, line);
    writeChunk(&chunk, OP_RETURN, 5555);


    interpret(&chunk);

    freeVM();
    freeChunk(&chunk);
    return 0;
}
