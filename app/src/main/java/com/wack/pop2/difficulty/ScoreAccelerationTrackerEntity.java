package com.wack.pop2.difficulty;

import com.google.common.collect.EvictingQueue;
import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.hudentities.ScoreHudEntity;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;

import java.util.Queue;

import static com.wack.pop2.difficulty.DifficultyConstants.ACCELERATION_UPDATE_INTERVAL_SECONDS;
import static com.wack.pop2.difficulty.DifficultyConstants.NUM_DATA_POINTS_FOR_SMA;
import static com.wack.pop2.difficulty.DifficultyConstants.TARGET_SCORE_ACCELERATION;

/**
 * Listens to score changed events and calculates the acceleration of score change over some
 * moving average of data points.
 *
 * - Tracks a simple moving average of score changed events
 * - on every event calculates the speed of changed between the last 3
 */
public class ScoreAccelerationTrackerEntity extends BaseEntity {

    public interface OnScoreAccelerationErrorListener {
        void onAccelerationErrorChanged(float accelerationError);
    }

    private final ScoreHudEntity scoreHudEntity;
    private final OnScoreAccelerationErrorListener onScoreAccelerationErrorListener;

    private Queue<Integer> smaDataPoints = EvictingQueue.create(NUM_DATA_POINTS_FOR_SMA);
    private Queue<Float> speedDataPoints = EvictingQueue.create(3);
    private float[] speeds = new float[]{0,0};

    private TimerHandler scoreAccelerationCalculatorUpdate =
            new TimerHandler(ACCELERATION_UPDATE_INTERVAL_SECONDS, true, new ITimerCallback() {
                @Override
                public void onTimePassed(TimerHandler pTimerHandler) {
                    update();
                }
            });

    public ScoreAccelerationTrackerEntity(
            ScoreHudEntity scoreHudEntity,
            OnScoreAccelerationErrorListener onScoreAccelerationErrorListener,
            GameResources gameResources) {
        super(gameResources);
        this.scoreHudEntity = scoreHudEntity;
        this.onScoreAccelerationErrorListener = onScoreAccelerationErrorListener;
    }

    @Override
    public void onCreateScene() {
        engine.registerUpdateHandler(scoreAccelerationCalculatorUpdate);
    }

    @Override
    public void onDestroy() {
        engine.unregisterUpdateHandler(scoreAccelerationCalculatorUpdate);
    }

    private void update() {
        smaDataPoints.add(scoreHudEntity.getScore());
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
            speeds[0] = speedDataPointsArray[2] - speedDataPointsArray[1];
            speeds[1] = speedDataPointsArray[1] - speedDataPointsArray[0];
        }
    }

    private void updateAcceleration() {
        float currentAcceleration = speeds[0] - speeds[1];
        onScoreAccelerationErrorListener.onAccelerationErrorChanged(-1 * (TARGET_SCORE_ACCELERATION - currentAcceleration));
    }

    private float average(Queue<Integer> queue) {
        int sum = 0;
        for (Integer integer : queue) {
            sum += integer;
        }
        return (float) sum / queue.size();
    }
}
