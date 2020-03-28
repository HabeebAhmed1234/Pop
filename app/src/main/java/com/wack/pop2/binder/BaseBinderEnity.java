package com.wack.pop2.binder;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseBinderEnity {

    private final Map<Class, Object> bindings = new HashMap<>();
    private final BaseBinderEnity parent;

    private final Binder binder = new Binder() {
        @Override
        public <T> Binder bind(Class<T> type, T instance) {
            if (bindings.containsKey(type)) {
                throw new IllegalArgumentException("Type " + type.getName() + " is already bound to an instance");
            }
            bindings.put(type, instance);
            return this;
        }
    };

    public BaseBinderEnity(BaseBinderEnity parent) {
        this.parent = parent;
        createBindings(binder);
    }

    public <T> T get(Class<T> type) {
        if (!bindings.containsKey(type)) {
            if (parent == null) {
                Log.e("BaseBinderEnity", "No binding found for type " + type);
                return null;
            }
            return parent.get(type);
        }
        return (T) bindings.get(type);
    }

    protected abstract void createBindings(Binder binder);
}
