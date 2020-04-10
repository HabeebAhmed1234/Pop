package com.stupidfungames.pop.binder;

public interface Binder {

    <T> Binder bind(Class<T> type, T instance);
}
