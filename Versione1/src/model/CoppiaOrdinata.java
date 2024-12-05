package model;

public class CoppiaOrdinata<T> {
    private final T primo;
    private final T secondo;

    public CoppiaOrdinata(T first, T second) {
        this.primo = first;
        this.secondo = second;
    }

    public T getPrimo() {
        return primo;
    }

    public T getSecondo() {
        return secondo;
    }


}
