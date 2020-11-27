package com.stupidfungames.pop.eventbus;

import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventBus {

  public interface Subscriber {

    void onEvent(GameEvent event, EventPayload payload);
  }

  private static EventBus sEventBus;
  private static final String TAG = EventBus.class.getName();

  private Map<GameEvent, List<Subscriber>> mEventSubscribers = new HashMap<>();
  private Map<GameEvent, EventPayload> mLastEventPayloads = new HashMap<>();

  public static EventBus get() {
    if (sEventBus == null) {
      sEventBus = new EventBus();
    }
    return sEventBus;
  }

  private EventBus() {
  }


  public void onDestroy() {
    for (GameEvent event : mEventSubscribers.keySet()) {
      if (mEventSubscribers.get(event) != null && !mEventSubscribers.get(event).isEmpty()) {
        List<Subscriber> subscribers = mEventSubscribers.get(event);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("You forgot to unsubscribe from " + event + " in:");
        for (Subscriber subscriber : subscribers) {
          stringBuilder.append(subscriber.getClass().getSimpleName());
        }
        throw new IllegalStateException(stringBuilder.toString());
      }
    }
    mEventSubscribers.clear();
    mLastEventPayloads.clear();
  }

  public boolean containsSubscriber(GameEvent event, Subscriber subscriber) {
    return getSubscribers(event).contains(subscriber);
  }

  public EventBus subscribe(GameEvent event, Subscriber subscriber) {
    return this.subscribe(event, subscriber, false);
  }

  public EventBus subscribe(GameEvent event, Subscriber subscriber, boolean notifyOfLastEvent) {
    List<Subscriber> subscribers = getSubscribers(event);
    if (subscribers.contains(subscriber)) {
      throw new IllegalStateException(
          "Event " + event + " is already subscribed to by subscriber " + subscriber);
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
    List<Subscriber> subscribers = getSubscribers(event);
    if (!subscribers.contains(subscriber)) {
      throw new IllegalStateException(
          "Subscriber " + subscriber + " was never subscribed to event " + event);
    }
    subscribers.remove(subscriber);
    return this;
  }

  public void sendEvent(GameEvent event) {
    sendEvent(event, new EmptyEventPayload());
  }

  public void sendEvent(GameEvent event, EventPayload payload) {
    mLastEventPayloads.put(event, payload);
    List<Subscriber> subscribers = getSubscribers(event);
    if (subscribers.isEmpty()) {
      Log.e(TAG, "There are no subscribers for event " + event);
    }
    for (int i = 0; i < subscribers.size(); i++) {
      if (i < subscribers.size()) {
        subscribers.get(i).onEvent(event, payload);
      }
    }
  }

  private List<Subscriber> getSubscribers(GameEvent event) {
    if (!mEventSubscribers.containsKey(event)) {
      mEventSubscribers.put(event, new ArrayList<Subscriber>());
    }
    return mEventSubscribers.get(event);
  }
}
