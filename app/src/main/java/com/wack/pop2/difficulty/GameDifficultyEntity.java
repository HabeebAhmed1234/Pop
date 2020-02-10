package com.wack.pop2.difficulty;

import android.util.Log;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.eventbus.DifficultyChangedEventPayload;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.GameEvent;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;

import static com.wack.pop2.difficulty.DifficultyConstants.MIN_UPDATE_INTERVAL;
import static com.wack.pop2.difficulty.DifficultyConstants.SPAWN_INTERVAL_DECREASE_SPEED;
import static com.wack.pop2.difficulty.DifficultyConstants.SPAWN_INTERVAL_UPDATE_SECONDS;
import static com.wack.pop2.difficulty.DifficultyConstants.STARTING_SPAWN_INTERVAL;

/**
 * Controls the game difficulty
 */
public class GameDifficultyEntity extends BaseEntity {

    private float currentSpawnInterval = STARTING_SPAWN_INTERVAL;


    private TimerHandler updateIntervalUpdater =
            new TimerHandler(SPAWN_INTERVAL_UPDATE_SECONDS, true, new ITimerCallback() {
                @Override
                public void onTimePassed(TimerHandler pTimerHandler) {
                    updateInterval();
                }
            });

    public GameDifficultyEntity(GameResources gameResources) {
        super(gameResources);
    }

    @Override
    public void onCreateScene() {
        super.onCreateScene();
        engine.registerUpdateHandler(updateIntervalUpdater);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        engine.unregisterUpdateHandler(updateIntervalUpdater);
    }

    public void updateInterval() {
        currentSpawnInterval -= SPAWN_INTERVAL_DECREASE_SPEED;
        if (currentSpawnInterval < MIN_UPDATE_INTERVAL) {
            currentSpawnInterval = MIN_UPDATE_INTERVAL;
        }
        Log.d("sate", "currentSpawnInterval = " + currentSpawnInterval);
        EventBus.get().sendEvent(GameEvent.DIFFICULTY_CHANGE, new DifficultyChangedEventPayload(currentSpawnInterval));
    }
}
