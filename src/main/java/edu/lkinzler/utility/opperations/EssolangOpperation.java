package edu.lkinzler.utility.opperations;

@FunctionalInterface
public interface EssolangOpperation<T> {
    public T operate(T a, T b);
}