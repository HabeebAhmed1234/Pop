package com.wack.pop2.hudentities;

import com.wack.pop2.GameResources;
import com.wack.pop2.bubblepopper.BubblePopperEntity;
import com.wack.pop2.eventbus.DecrementScoreEventPayload;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.EventPayload;
import com.wack.pop2.eventbus.GameEvent;
import com.wack.pop2.eventbus.IncrementScoreEventPayload;
import com.wack.pop2.eventbus.ScoreChangeEventPayload;
import com.wack.pop2.eventbus.StartingBubbleSpawnedEventPayload;
import com.wack.pop2.resources.fonts.GameFontsManager;
import com.wack.pop2.utils.ScreenUtils;

import org.andengine.util.color.AndengineColor;

/**
 * Entity that contains score hud panel
 */
public class ScoreHudEntity extends BaseHudTextEntity implements EventBus.Subscriber {

    private int scoreValue = 0;
    private int maxScoreValue = 0;

    public ScoreHudEntity(GameFontsManager fontsManager, GameResources gameResources) {
        super(fontsManager, gameResources);
    }

    @Override
    public void onCreateScene() {
        super.onCreateScene();
        EventBus.get()
                .subscribe(GameEvent.INCREMENT_SCORE, this)
                .subscribe(GameEvent.DECREMENT_SCORE, this)
                .subscribe(GameEvent.STARTING_BUBBLE_SPAWNED, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.get().unSubscribe(GameEvent.INCREMENT_SCORE, this).unSubscribe(GameEvent.DECREMENT_SCORE, this);
    }

    @Override
    int[] getTextPosition() {
        return new int[] {50, ScreenUtils.getSreenSize().height - 150};
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
            case STARTING_BUBBLE_SPAWNED:
                incrementMaxScore((StartingBubbleSpawnedEventPayload) payload);
                break;
        }
    }

    private void incrementScore(IncrementScoreEventPayload payload) {
        scoreValue += payload.incrementAmmount;
        updateScoreText();

        EventBus.get().sendEvent(GameEvent.SCORE_CHANGED, new ScoreChangeEventPayload(scoreValue));
    }

    private void incrementMaxScore(StartingBubbleSpawnedEventPayload payload) {
        maxScoreValue += BubblePopperEntity.MAX_SCORE_INCREASE_PER_NEW_SPAWNED_BUBBLE;
    }

    private void decrementScore(DecrementScoreEventPayload payload) {
        scoreValue -= payload.decrementAmmount;
        updateScoreText();
    }

    private void updateScoreText() {
        updateText("Score: " + scoreValue + " - " + maxScoreValue);
    }
}