package com.wack.pop2.difficulty;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.eventbus.DifficultyChangedEventPayload;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.EventPayload;
import com.wack.pop2.eventbus.GameDifficultyAccelerationErrorPayload;
import com.wack.pop2.eventbus.GameEvent;
import com.wack.pop2.hudentities.ScoreHudEntity;

import static com.wack.pop2.difficulty.DifficultyConstants.BASE_SPAWN_INTERVAL_DIFF;

/**
 * Controls the game difficulty
 */
public class GameDifficultyEntity extends BaseEntity implements EventBus.Subscriber {

    private float currentSpawnInterval = 5;

    private ScoreAccelerationTrackerEntity scoreAccelerationTrackerEntity;
    private ScoreErrorSignalCalculatorEntity scoreErrorSignalCalculatorEntity;

    public GameDifficultyEntity(ScoreHudEntity scoreHudEntity, GameResources gameResources) {
        super(gameResources);
        scoreAccelerationTrackerEntity = new ScoreAccelerationTrackerEntity(scoreHudEntity, gameResources);
        scoreErrorSignalCalculatorEntity = new ScoreErrorSignalCalculatorEntity(gameResources);
    }

    @Override
    public void onCreateScene() {
        EventBus.get().subscribe(GameEvent.GAME_DIFFICULTY_ERROR_SIGNAL_CHANGED, this, true);
    }

    @Override
    public void onDestroy() {
        EventBus.get().unSubscribe(GameEvent.GAME_DIFFICULTY_ERROR_SIGNAL_CHANGED, this);
    }

    @Override
    public void onEvent(GameEvent event, EventPayload payload) {
        if (event == GameEvent.GAME_DIFFICULTY_ERROR_SIGNAL_CHANGED) {
            GameDifficultyAccelerationErrorPayload errorPayload = (GameDifficultyAccelerationErrorPayload) payload;
            onGameScoreAccelerationErrorChanged(errorPayload.accelerationError);
        }
    }

    private void onGameScoreAccelerationErrorChanged(float accelerationError) {
        currentSpawnInterval += BASE_SPAWN_INTERVAL_DIFF * accelerationError;
        EventBus.get().sendEvent(GameEvent.DIFFICULTY_CHANGE, new DifficultyChangedEventPayload(currentSpawnInterval));
    }
}
