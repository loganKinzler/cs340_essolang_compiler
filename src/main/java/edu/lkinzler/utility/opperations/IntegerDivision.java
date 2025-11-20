package edu.lkinzler.utility.opperations;

public class IntegerDivision implements EssolangBinaryOpperation<Integer> {
    @Override
    public Integer getInstruction() {return 204;}

    @Override
    public Integer operate(Integer a, Integer b) {return a / b;}
}