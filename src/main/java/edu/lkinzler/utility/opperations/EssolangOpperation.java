package edu.lkinzler.utility.opperations;

public interface EssolangOpperation<A, B, C> {
    public Integer getInstruction();
    public C operate(A a, B b);
}