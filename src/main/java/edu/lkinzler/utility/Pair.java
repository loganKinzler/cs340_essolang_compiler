package edu.lkinzler.utility;

import java.util.Objects;

public class Pair<A, B> {
    private A current;
    private B next;

    public Pair(A current, B next)  {
        this.current = current;
        this.next = next;
    }

    public A getCurrent() {return current;}
    public void setCurrent(A current) {this.current = current;}

    public B getNext() {return next;}
    public void setNext(B next) {this.next = next;}

    @Override
    public int hashCode() {
        return Objects.hash(current, next);
    }

    @Override
    public boolean equals(Object object) {

        // no properties to read from object since it's null
        if (object == null)
            return false;

        // different class entirely
        if (object.getClass() != this.getClass())
            return false;

        // typecast to Pair<A,B> after we know object is of class type Pair<A,B>
        Pair<A,B> objectPair = (Pair<A,B>) object;

        // incorrect typing of pair elements
        if (!objectPair.current.getClass().equals(this.current.getClass()) ||
            !objectPair.next.getClass().equals(this.next.getClass()))
            return false;

        // correct values for pair elements
        return objectPair.current.equals(this.current) && objectPair.next.equals(this.next);
    }
}