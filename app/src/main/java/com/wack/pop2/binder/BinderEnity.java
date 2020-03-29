package com.wack.pop2.binder;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public abstract class BinderEnity {

    private final Map<Class, Object> bindings = new HashMap<>();
    private final BinderEnity parent;

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

    public BinderEnity(BinderEnity parent) {
        this.parent = parent;
        createBindings(binder);
    }

    /**
     * If this is called from a constructor of an Entity then there is no gurantee that you will
     * get it
     */
    public <T> T get(Class<T> type) {
        if (!bindings.containsKey(type)) {
            if (parent == null) {
                Log.e("BaseBinderEnity", "No binding found for type " + type);
                return null;
            }
            return parent.get(type);
        }
        T object = (T)  bindings.get(type);
        if (object == null) {
            throw new IllegalStateException("Null object assigned to type " + object.getClass().getName());
        }
        return object;
    }

    protected void createBindings(Binder binder) {}
}
