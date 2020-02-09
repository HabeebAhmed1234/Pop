package com.wack.pop2.difficulty;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.EventPayload;
import com.wack.pop2.eventbus.GameDifficultyAccelerationErrorPayload;
import com.wack.pop2.eventbus.GameEvent;
import com.wack.pop2.eventbus.GameScoreAccelerationChangedPayload;

import static com.wack.pop2.difficulty.DifficultyConstants.TARGET_SCORE_ACCELERATION;

public class ScoreErrorSignalCalculatorEntity extends BaseEntity implements EventBus.Subscriber {

    public ScoreErrorSignalCalculatorEntity(GameResources gameResources) {
        super(gameResources);
    }

    @Override
    public void onCreateScene() {
        EventBus.get().subscribe(GameEvent.GAME_SCORE_ACCELERATION_CHANGED, this);
    }

    @Override
    public void onDestroy() {
        EventBus.get().unSubscribe(GameEvent.GAME_SCORE_ACCELERATION_CHANGED, this);
    }

    @Override
    public void onEvent(GameEvent event, EventPayload payload) {
        if (event == GameEvent.GAME_SCORE_ACCELERATION_CHANGED) {
            GameScoreAccelerationChangedPayload accelerationChangedPayload = (GameScoreAccelerationChangedPayload) payload;
            EventBus.get().sendEvent(
                    GameEvent.GAME_DIFFICULTY_ERROR_SIGNAL_CHANGED,
                    new GameDifficultyAccelerationErrorPayload(
                            TARGET_SCORE_ACCELERATION - accelerationChangedPayload.scoreAcceleration));
        }
    }
}
