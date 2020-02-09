package com.wack.pop2.difficulty;

import android.util.Log;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.eventbus.DifficultyChangedEventPayload;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.GameEvent;
import com.wack.pop2.hudentities.ScoreHudEntity;

import static com.wack.pop2.difficulty.DifficultyConstants.BASE_SPAWN_INTERVAL_DIFF;
import static com.wack.pop2.difficulty.DifficultyConstants.MIN_UPDATE_INTERVAL;

/**
 * Controls the game difficulty
 */
public class GameDifficultyEntity extends BaseEntity implements ScoreAccelerationTrackerEntity.OnScoreAccelerationErrorListener {

    private float currentSpawnInterval = 5;

    private ScoreAccelerationTrackerEntity scoreAccelerationTrackerEntity;

    public GameDifficultyEntity(ScoreHudEntity scoreHudEntity, GameResources gameResources) {
        super(gameResources);
        scoreAccelerationTrackerEntity = new ScoreAccelerationTrackerEntity(scoreHudEntity, this, gameResources);
    }
    @Override
    public void onAccelerationErrorChanged(float accelerationError) {
        Log.d("sate", "onAccelerationErrorChanged = " + accelerationError);
        currentSpawnInterval += BASE_SPAWN_INTERVAL_DIFF * accelerationError;
        if (currentSpawnInterval < MIN_UPDATE_INTERVAL) {
            currentSpawnInterval = MIN_UPDATE_INTERVAL;
        }
        EventBus.get().sendEvent(GameEvent.DIFFICULTY_CHANGE, new DifficultyChangedEventPayload(currentSpawnInterval));
    }
}
