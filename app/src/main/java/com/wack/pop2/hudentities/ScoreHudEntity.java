package com.wack.pop2.hudentities;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.bubblepopper.BubblePopperEntity;
import com.wack.pop2.eventbus.EventPayload;
import com.wack.pop2.GameResources;
import com.wack.pop2.utils.ScreenUtils;
import com.wack.pop2.eventbus.DecrementScoreEventPayload;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.GameEvent;
import com.wack.pop2.eventbus.IncrementScoreEventPayload;
import com.wack.pop2.eventbus.ScoreChangeEventPayload;
import com.wack.pop2.eventbus.StartingBubbleSpawnedEventPayload;
import com.wack.pop2.resources.fonts.FontId;
import com.wack.pop2.resources.fonts.GameFontsManager;

import org.andengine.entity.text.Text;

/**
 * Entity that contains score hud panel
 */
public class ScoreHudEntity extends BaseEntity implements EventBus.Subscriber {

    private GameFontsManager fontsManager;

    private int scoreValue = 0;
    private int maxScoreValue = 0;

    private Text scoreText;

    public ScoreHudEntity(GameFontsManager fontsManager, GameResources gameResources) {
        super(gameResources);
        this.fontsManager = fontsManager;
    }

    @Override
    public void onCreateScene() {
        EventBus.get()
                .subscribe(GameEvent.INCREMENT_SCORE, this)
                .subscribe(GameEvent.DECREMENT_SCORE, this)
                .subscribe(GameEvent.STARTING_BUBBLE_SPAWNED, this);
        ScreenUtils.ScreenSize screenSize = ScreenUtils.getSreenSize();
        scoreText = new Text(50, screenSize.height - 150, fontsManager.getFont(FontId.SCORE_TICKER_FONT), "Score: - - - - - - - - - -", "Score: X X X X X X X X X X".length(), vertexBufferObjectManager);
        scoreText.setColor(0,1,0);
        scene.attachChild(scoreText);
    }

    @Override
    public void onDestroy() {
        EventBus.get().unSubscribe(GameEvent.INCREMENT_SCORE, this).unSubscribe(GameEvent.DECREMENT_SCORE, this);
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
        scoreText.setText("Score: " + scoreValue + " - " + maxScoreValue);
    }
}