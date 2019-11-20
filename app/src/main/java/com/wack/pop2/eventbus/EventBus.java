package com.wack.pop2.eventbus;

import android.util.Log;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class EventBus {

    public interface Subscriber {
        void onEvent(GameEvent event, EventPayload payload);
    }

    private static EventBus sEventBus;
    private static final String TAG = EventBus.class.getName();

    private Map<GameEvent, Set<Subscriber>> mEventSubscribers = new HashMap<>();

    public static void init() {
        if (sEventBus != null) {
            throw new IllegalStateException(
                    "Cannot initialize a new event bus when on already exists. You must destroy the existing event bus first");
        }
        sEventBus = new EventBus();
    }

    public static void destroy() {
        if (sEventBus == null) {
            throw new IllegalStateException(
                    "Cannot destroy event bus when it doesn't exist. Create a new one first");
        }
        sEventBus = null;
    }

    public static EventBus get() {
        if (sEventBus == null) {
            throw new IllegalStateException("Cannot get an instance of event bus. No event bus initialized");
        }
        return sEventBus;
    }
    private EventBus() {}

    private Set<Subscriber> getSubscribers(GameEvent event) {
        if (!mEventSubscribers.containsKey(event)) {
            mEventSubscribers.put(event, new HashSet());
        }
        return mEventSubscribers.get(event);
    }

    public EventBus subscribe(GameEvent event, Subscriber subscriber) {
        Set<Subscriber> subscribers = getSubscribers(event);
        if (subscribers.contains(subscriber)) {
            throw new IllegalStateException("Event " + event + " is already subscribed to by subscriber " + subscriber);
        }
        subscribers.add(subscriber);
        return this;
    }

    public EventBus unSubscribe(GameEvent event, Subscriber subscriber) {
        Set<Subscriber> subscribers = getSubscribers(event);
        if (!subscribers.contains(subscriber)) {
            throw new IllegalStateException("Subscriber " + subscriber + " was never subscribed to event " + event);
        }
        subscribers.remove(subscriber);
        return this;
    }

    public void sendEvent(GameEvent event) {
        sendEvent(event, new EmptyEventPayload());
    }

    public void sendEvent(GameEvent event, EventPayload payload) {
        Set<Subscriber> subscribers = getSubscribers(event);
        if (subscribers.isEmpty()) {
            Log.e(TAG, "There are no subscribers for event " + event);
        }
        Iterator<Subscriber> it = subscribers.iterator();
        while (it.hasNext()) {
            it.next().onEvent(event, payload);
        }
    }
}
