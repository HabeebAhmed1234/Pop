package com.wack.pop2.binder;

public interface Binder {

    <T> Binder bind(Class<T> type, T instance);
}
