package com.wack.pop2.difficulty;

import com.google.common.collect.EvictingQueue;
import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.EventPayload;
import com.wack.pop2.eventbus.GameEvent;
import com.wack.pop2.eventbus.ScoreChangeEventPayload;

import java.util.Queue;

import static com.wack.pop2.eventbus.GameEvent.SCORE_CHANGED;

/**
 * Listens to score changed events and calculates the acceleration of score change over some
 * moving average of data points.
 *
 * - Tracks a simple moving average of score changed events
 * - on every event calculates the speed of changed between the last 3
 */
public class ScoreAccelerationTrackerEntity extends BaseEntity implements EventBus.Subscriber {

    private static final int NUM_DATA_POINTS_FOR_SMA = 10;

    private Queue<Integer> smaDataPoints = new EvictingQueue<>(NUM_DATA_POINTS_FOR_SMA);
    private Queue<Float> speedDataPoints = new EvictingQueue<>(3);
    private float[] speeds = new float[]{0,0};

    private float currentAccelleration = 0;



    public ScoreAccelerationTrackerEntity(GameResources gameResources) {
        super(gameResources);
    }

    @Override
    public void onCreateScene() {
        EventBus.get().subscribe(SCORE_CHANGED, this);

    }

    @Override
    public void onDestroy() {
        EventBus.get().unSubscribe(SCORE_CHANGED, this);
    }

    @Override
    public void onEvent(GameEvent event, EventPayload payload) {
        if (event == SCORE_CHANGED) {
            ScoreChangeEventPayload scoreChangeEventPayload = (ScoreChangeEventPayload) payload;
            smaDataPoints.add(scoreChangeEventPayload.score);

            update();
        }
    }

    private void update() {
        float sma = average(smaDataPoints);
        speedDataPoints.add(sma);
        updateSpeeds();
        updateAcceleration();
    }

    /**
     * Iterates over the speedDataPoints and adds the results to speeds
     */
    private void updateSpeeds() {
        if (speedDataPoints.size() == 3) {
            Float[] speedDataPointsArray = speedDataPoints.toArray(new Float[speedDataPoints.size()]);
            speeds[0] = speedDataPointsArray[0] - speedDataPointsArray[1];
            speeds[1] = speedDataPointsArray[1] - speedDataPointsArray[2];
        }
    }

    private void updateAcceleration() {
        currentAccelleration = speeds[0] - speeds[1];
    }

    private float average(Queue<Integer> queue) {
        int sum = 0;
        for (Integer integer : queue) {
            sum += integer;
        }
        return (float) sum / queue.size();
    }
}
