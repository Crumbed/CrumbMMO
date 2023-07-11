package com.crumbed.crumbmmo.utils;


import static java.util.Objects.requireNonNull;

public interface Result<T> {
    static <T> Result<T> ok(final T value) {
        requireNonNull(value, "The value of a Result cannot be null, consider using Option");
        return new Ok<>(value);
    }

    static <T, E extends Throwable> Result<T> err(final E error) {
        requireNonNull(error, "The error of a Result cannot be null");
        return new Err<>(error);
    }

    boolean isOk();
    boolean isErr();
    T unwrap() throws Throwable;
    Option<T> unwrapIfOk();
    Option<Throwable> unwrapErr();
    T unwrapOrElse(T backup);


    class Ok<T> implements Result<T> {
        private final T value;

        public Ok(final T value) { this.value = value; }
        @Override
        public boolean isOk() { return true; }
        @Override
        public boolean isErr() { return false; }
        @Override
        public T unwrap() throws Throwable { return value; }
        @Override
        public Option<T> unwrapIfOk() { return Option.some(value); }
        @Override
        public Option<Throwable> unwrapErr() { return Option.none(); }
        @Override
        public T unwrapOrElse(T backup) { return value; }
    }

    class Err<T> implements Result<T> {
        private final Throwable error;

        public Err(final Throwable error) { this.error = error; }

        @Override
        public boolean isOk() { return false; }
        @Override
        public boolean isErr() { return true; }
        @Override
        public T unwrap() throws Throwable { throw error; }
        @Override
        public Option<T> unwrapIfOk() { return Option.none(); }
        @Override
        public Option<Throwable> unwrapErr() { return Option.some(error); }
        @Override
        public T unwrapOrElse(T backup) { return backup; }
    }
}









































