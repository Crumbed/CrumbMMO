package com.crumbed.crumbmmo.utils;

public record None<T>() implements Option<T> {
    @Override
    public boolean isSome() {
        return false;
    }

    @Override
    public boolean isNone() {
        return true;
    }

    @Override
    public T unwrap() throws NullPointerException {
        throw new NullPointerException("Cannot unwrap on a None value");
    }

    @Override
    public String toString() {
        return "None";
    }
}
