package edu.lkinzler.utility.opperations;

public class IntegerMultiplication implements EssolangBinaryOpperation<Integer> {
    @Override
    public Integer getInstruction() {return 203;}

    @Override
    public Integer operate(Integer a, Integer b) {return a * b;}
}
