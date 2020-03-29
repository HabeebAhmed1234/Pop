package com.wack.pop2.interaction;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.binder.Binder;
import com.wack.pop2.binder.BinderEnity;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.GameEvent;
import com.wack.pop2.eventbus.InteractionScoreEventPayload;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;

public class InteractionCounter extends BaseEntity {

    public static final int TOUCH_INSTANCE_INTERACTION_WEIGHT = 1;

    private static final int TIME_BUCKET_SECONDS = 1;

    private int interactionsOfCurrentTimeBucket = 0;
    private TimerHandler timeBucketHandler = new TimerHandler(
            TIME_BUCKET_SECONDS,
            true,
            new ITimerCallback() {
                @Override
                public void onTimePassed(TimerHandler pTimerHandler) {
                    onTimeBucketUpdate();
                }
            });

    public InteractionCounter(BinderEnity parent) {
        super(parent);
    }

    @Override
    public void onCreateScene() {
        engine.registerUpdateHandler(timeBucketHandler);
    }

    @Override
    public void onDestroy() {
        engine.unregisterUpdateHandler(timeBucketHandler);
    }

    public void onInteraction(int interactionWeight) {
        interactionsOfCurrentTimeBucket += interactionWeight;
    }

    private void onTimeBucketUpdate() {
        EventBus.get().sendEvent(
                GameEvent.INTERACTION_SCORE_CHANGED,
                new InteractionScoreEventPayload(interactionsOfCurrentTimeBucket));
        interactionsOfCurrentTimeBucket = 0;
    }
}
