package com.crumbed.crumbmmo.utils;



public sealed interface Option<T> permits Some, None {
    boolean isSome();
    boolean isNone();

    T unwrap() throws NullPointerException;

    static <T> Option<T> some(T inner) { return new Some<>(inner); }
    static <T> Option<T> none() { return new None<>(); }

    @Override
    String toString();
}


























