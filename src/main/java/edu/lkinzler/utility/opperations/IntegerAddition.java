package edu.lkinzler.utility.opperations;

public class IntegerAddition implements EssolangBinaryOpperation<Integer> {
    public IntegerAddition() {}

    @Override
    public Integer getInstruction() {return 201;}

    @Override
    public Integer operate(Integer a, Integer b) {return a + b;}
}