//
// Created by YifuDuan on 2021/10/12.
//

#ifndef CLOX_VM_H
#define CLOX_VM_H

#include "chunk.h"

typedef struct {
    Chunk* chunk;
    uint8_t* ip;
    Value stack[STACK_MAX];
    Value* stackTop;
} VM;

typedef enum {
    INTERPRET_OK,
    INTERPRET_COMPILE_ERROR,
    INTERPRET_RUNTIME_ERROR,
} InterpretResult;

void initVM();

void freeVM();

InterpretResult interpret(Chunk* chunk);

#endif //CLOX_VM_H
