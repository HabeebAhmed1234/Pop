package com.wack.pop2.difficulty;

import android.util.Log;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.binder.BinderEnity;
import com.wack.pop2.eventbus.DifficultyChangedEventPayload;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.GameEvent;
import com.wack.pop2.eventbus.GameProgressEventPayload;

import com.wack.pop2.savegame.SaveGame;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;

import static com.wack.pop2.GameConstants.MAX_SPAWN_INTERVAL;
import static com.wack.pop2.GameConstants.MIN_SPAWN_INTERVAL;
import static com.wack.pop2.difficulty.DifficultyConstants.SPAWN_INTERVAL_DECREASE_SPEED;
import static com.wack.pop2.difficulty.DifficultyConstants.SPAWN_INTERVAL_UPDATE_SECONDS;

/**
 * Controls the game difficulty
 */
public class GameDifficultyEntity extends BaseEntity {

    private float currentSpawnInterval = MAX_SPAWN_INTERVAL;


    private TimerHandler updateIntervalUpdater =
            new TimerHandler(SPAWN_INTERVAL_UPDATE_SECONDS, true, new ITimerCallback() {
                @Override
                public void onTimePassed(TimerHandler pTimerHandler) {
                    updateInterval();
                }
            });

    public GameDifficultyEntity(BinderEnity parent) {
        super(parent);
    }

    @Override
    public void onCreateScene() {
        super.onCreateScene();
        initUpdateInterval();
        engine.registerUpdateHandler(updateIntervalUpdater);
    }

    @Override
    public void onSaveGame(SaveGame saveGame) {
        super.onSaveGame(saveGame);
        saveGame.spawnInterval = currentSpawnInterval;
    }

    @Override
    public void onLoadGame(SaveGame saveGame) {
        super.onLoadGame(saveGame);
        setCurrentSpawnInterval(saveGame.spawnInterval);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        engine.unregisterUpdateHandler(updateIntervalUpdater);
    }

    public void initUpdateInterval() {
        EventBus.get().sendEvent(GameEvent.SPAWN_INTERVAL_CHANGED, new DifficultyChangedEventPayload(0));
        EventBus.get().sendEvent(GameEvent.GAME_PROGRESS_CHANGED, new GameProgressEventPayload(0));
    }

    public void updateInterval() {
        setCurrentSpawnInterval(currentSpawnInterval - SPAWN_INTERVAL_DECREASE_SPEED);
    }

    private void setCurrentSpawnInterval(float newInterval) {
        currentSpawnInterval = newInterval;
        if (currentSpawnInterval < MIN_SPAWN_INTERVAL) {
            currentSpawnInterval = MIN_SPAWN_INTERVAL;
        }
        Log.d("GameDifficultyEntity", "currentSpawnInterval = " + currentSpawnInterval);
        EventBus.get().sendEvent(GameEvent.SPAWN_INTERVAL_CHANGED, new DifficultyChangedEventPayload(currentSpawnInterval));
        EventBus.get().sendEvent(GameEvent.GAME_PROGRESS_CHANGED, new GameProgressEventPayload(getGameProgress()));
    }

    /**
     * Returns the game progress as the currentSpawnInterval percentage between min and max spawn
     * intervals
     */
    private float getGameProgress() {
        return 1 - (currentSpawnInterval - MIN_SPAWN_INTERVAL) / (MAX_SPAWN_INTERVAL - MIN_SPAWN_INTERVAL);
    }
}
