package com.crumbed.crumbmmo.utils;

public record Some<T>(T inner) implements Option<T> {
    @Override
    public boolean isSome() {
        return true;
    }

    @Override
    public boolean isNone() {
        return false;
    }

    @Override
    public T unwrap() throws NullPointerException {
        return inner;
    }

    @Override
    public T unwrapOr(Object ignored) { return inner; }

    @Override
    public String toString() {
        return inner.toString();
    }
}
