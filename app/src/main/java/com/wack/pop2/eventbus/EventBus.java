package com.wack.pop2.eventbus;

import android.util.Log;

import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

// TODO: make this threadsafe
public class EventBus {

    public interface Subscriber {
        void onEvent(GameEvent event, EventPayload payload);
    }

    private static EventBus sEventBus;
    private static final String TAG = EventBus.class.getName();

    private Map<GameEvent, ConcurrentLinkedQueue<Subscriber>> mEventSubscribers = new ConcurrentHashMap<>();
    private Map<GameEvent, EventPayload> mLastEventPayloads = new ConcurrentHashMap<>();

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

    public boolean containsSubscriber(GameEvent event, Subscriber subscriber) {
        return getSubscribers(event).contains(subscriber);
    }

    public EventBus subscribe(GameEvent event, Subscriber subscriber) {
        return this.subscribe(event, subscriber, false);
    }

    public EventBus subscribe(GameEvent event, Subscriber subscriber, boolean notifyOfLastEvent) {
        Queue<Subscriber> subscribers = getSubscribers(event);
        if (subscribers.contains(subscriber)) {
            throw new IllegalStateException("Event " + event + " is already subscribed to by subscriber " + subscriber);
        }
        subscribers.add(subscriber);

        // If there was already an event with a payload fired for this event then bring the new subscriber
        // Up to speed by passing in the payload
        if (notifyOfLastEvent && mLastEventPayloads.containsKey(event)) {
            subscriber.onEvent(event, mLastEventPayloads.get(event));
        }
        return this;
    }

    public EventBus unSubscribe(GameEvent event, Subscriber subscriber) {
        Queue<Subscriber> subscribers = getSubscribers(event);
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
        mLastEventPayloads.put(event, payload);
        Queue<Subscriber> subscribers = getSubscribers(event);
        if (subscribers.isEmpty()) {
            Log.e(TAG, "There are no subscribers for event " + event);
        }
        Iterator<Subscriber> it = subscribers.iterator();
        while (it.hasNext()) {
            it.next().onEvent(event, payload);
        }
    }

    private Queue<Subscriber> getSubscribers(GameEvent event) {
        if (!mEventSubscribers.containsKey(event)) {
            mEventSubscribers.put(event, new ConcurrentLinkedQueue<Subscriber>());
        }
        return mEventSubscribers.get(event);
    }
}
