package com.company;


public class CustomChar {

    private char character;
    private Integer priority;
    private CustomChar rightChild;
    private CustomChar leftChild;
    private String code = "";

    public CustomChar(char character, int priority) {
        this.character = character;
        this.priority = priority;
    }

    public char getCharacter() {
        return character;
    }

    public int getPriority() {
        return priority;
    }

    public CustomChar getRightChild() {
        return rightChild;
    }

    public void setRightChild(CustomChar rightChild) {
        this.rightChild = rightChild;
    }

    public CustomChar getLeftChild() {
        return leftChild;
    }

    public void setLeftChild(CustomChar leftChild) {
        this.leftChild = leftChild;
    }

    @Override
    public String toString() {
        String result = " = ";
        return character + result + priority;
    }

    public void setCode(String s) {
        code = s;
    }

    public String getCode() {
        return code;
    }

}