package edu.lkinzler.utility.opperations;

public interface EssolangExternalOpperation<A, B> extends EssolangOpperation<A, B, B> {
    // the other side (commutative)
    public B opperate(B b, A a);
}
