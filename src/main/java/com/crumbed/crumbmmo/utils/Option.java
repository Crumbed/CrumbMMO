package com.crumbed.crumbmmo.utils;

public class Option<T> {
    private final T inner;

    public Option(T inner) { this.inner = inner; }

    public boolean isSome() { return inner != null; }
    public boolean isNone() { return inner == null; }

    public T unwrap() throws NullPointerException {
        if (isNone()) throw new NullPointerException("Cannot unwrap on a None value");
        return inner;
    }

    public static <T> Option<T> some(T inner) { return new Option<>(inner); }
    public static <T> Option<T> none() { return new Option<>(null); }

    @Override
    public String toString() {
        if (isNone()) return "None";
        return inner.toString();
    }
}
