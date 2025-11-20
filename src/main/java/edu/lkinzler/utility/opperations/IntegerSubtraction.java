package edu.lkinzler.utility.opperations;

public class IntegerSubtraction implements EssolangOpperation<Integer, Integer, Integer> {
    @Override
    public Integer getInstruction() {return 202;}

    @Override
    public Integer operate(Integer a, Integer b) {return a - b;}
}