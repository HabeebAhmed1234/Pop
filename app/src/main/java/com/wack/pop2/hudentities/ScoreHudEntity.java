package com.wack.pop2.hudentities;

import com.wack.pop2.binder.BinderEnity;
import com.wack.pop2.eventbus.DecrementScoreEventPayload;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.EventPayload;
import com.wack.pop2.eventbus.GameEvent;
import com.wack.pop2.eventbus.IncrementScoreEventPayload;
import com.wack.pop2.eventbus.ScoreChangeEventPayload;
import com.wack.pop2.savegame.SaveGame;

import org.andengine.util.color.AndengineColor;

/**
 * Entity that contains score hud panel
 */
public class ScoreHudEntity extends HudTextBaseEntity implements EventBus.Subscriber {

    private static final String SCORE_TEXT_PREFIX = "Score: ";

    private int scoreValue = 0;

    public ScoreHudEntity(BinderEnity parent) {
        super(parent);
    }

    @Override
    public void onCreateScene() {
        super.onCreateScene();
        EventBus.get()
                .subscribe(GameEvent.INCREMENT_SCORE, this)
                .subscribe(GameEvent.DECREMENT_SCORE, this);
    }

    @Override
    public void onLoadGame(SaveGame saveGame) {
        super.onLoadGame(saveGame);
        setScoreOnLoadGame(saveGame.score);
    }

    @Override
    String getInitialText() {
        return SCORE_TEXT_PREFIX + "-------";
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.get().unSubscribe(GameEvent.INCREMENT_SCORE, this).unSubscribe(GameEvent.DECREMENT_SCORE, this);
    }

    @Override
    int[] getTextPosition() {
        return new int[] {20, 20};
    }

    @Override
    int getMaxStringLength() {
        return 30;
    }

    @Override
    AndengineColor getTextColor() {
        return AndengineColor.GREEN;
    }

    public int getScore() {
        return scoreValue;
    }

    @Override
    public void onEvent(GameEvent event, EventPayload payload) {
        switch (event) {
            case DECREMENT_SCORE:
                decrementScore((DecrementScoreEventPayload) payload);
                break;
            case INCREMENT_SCORE:
                incrementScore((IncrementScoreEventPayload) payload);
                break;
        }
    }

    private void setScoreOnLoadGame(int loadedScore) {
        scoreValue = loadedScore;
        updateScoreText();

        EventBus.get().sendEvent(GameEvent.SCORE_CHANGED, new ScoreChangeEventPayload(scoreValue));
    }

    private void incrementScore(IncrementScoreEventPayload payload) {
        scoreValue += payload.incrementAmmount;
        updateScoreText();

        EventBus.get().sendEvent(GameEvent.SCORE_CHANGED, new ScoreChangeEventPayload(scoreValue));
    }

    private void decrementScore(DecrementScoreEventPayload payload) {
        scoreValue -= payload.decrementAmmount;
        updateScoreText();
    }

    private void updateScoreText() {
        updateText(SCORE_TEXT_PREFIX + scoreValue);
    }
}