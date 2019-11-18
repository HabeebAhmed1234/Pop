package com.wack.pop2;

import android.util.Log;

import com.wack.pop2.eventbus.DifficultyChangedEventPayload;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.GameEvent;
import com.wack.pop2.eventbus.ScoreChangeEventPayload;

/**
 * Controls the game difficulty
 */
public class GameDifficultyEntity extends BaseEntity implements EventBus.Subscriber {

    private static final int MAX_DIFFICULTY = 5;
    private static final int DIFFICULTY_SCORE_INCREMENT_INTERVAL = 100;

    private int scoreWhenLastDifficultyUpdated = 0;
    private int currentDifficulty = 1;

    public GameDifficultyEntity(GameResources gameResources) {
        super(gameResources);
    }

    @Override
    public void onCreateScene() {
        EventBus.get().subscribe(GameEvent.SCORE_CHANGED, this);
        fireDifficultyUpdatedEvent();
    }

    @Override
    public void onDestroy() {
        EventBus.get().unSubscribe(GameEvent.SCORE_CHANGED, this);
    }

    @Override
    public void onEvent(GameEvent event, EventPayload payload) {
        if (event == GameEvent.SCORE_CHANGED) {
            ScoreChangeEventPayload scoreChangeEventPayload = (ScoreChangeEventPayload) payload;
            processScore(scoreChangeEventPayload.score);
        }
    }

    private void processScore(int newScore) {
        if ((newScore - scoreWhenLastDifficultyUpdated) >= DIFFICULTY_SCORE_INCREMENT_INTERVAL) {
            currentDifficulty++;
            fireDifficultyUpdatedEvent();
            scoreWhenLastDifficultyUpdated = newScore;
        }
    }

    private void fireDifficultyUpdatedEvent() {
        if (currentDifficulty <= MAX_DIFFICULTY) {
            EventBus.get().sendEvent(GameEvent.DIFFICULTY_CHANGE, new DifficultyChangedEventPayload(currentDifficulty));
        }
    }
}
