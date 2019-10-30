package com.wack.pop2.hudentities;

import android.opengl.GLES20;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameAnimationManager;
import com.wack.pop2.GameResources;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.GameEvent;
import com.wack.pop2.resources.fonts.FontId;
import com.wack.pop2.resources.fonts.GameFontsManager;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.resources.textures.TextureId;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;

/**
 * Entity that contains score hud panel
 */
public class TimerHudEntity extends BaseEntity {

    private static final int TIMER_STARTING_VALUE_SECONDS = 120;
    private static final int TIMER_DECREMENT_INTERVAL_SECONDS = 1;
    private static final int COUNT_DOWN_THRESHOLD_SECONDS = 10;

    private static final int MAX_TIMER_TEXT_LENGTH = 20;

    private GameFontsManager fontsManager;
    private GameTexturesManager texturesManager;
    private GameAnimationManager gameAnimationManager;


    private Text timerText;
    private int timerValue = TIMER_STARTING_VALUE_SECONDS;

    public TimerHudEntity(
            GameFontsManager fontsManager,
            GameTexturesManager texturesManager,
            GameAnimationManager gameAnimationManager,
            GameResources gameResources) {
        super(gameResources);
        this.fontsManager = fontsManager;
        this.texturesManager = texturesManager;
        this.gameAnimationManager = gameAnimationManager;
        registerTimerUpdater();
    }

    @Override
    public void onCreateScene() {
        timerText = new Text(20,20, fontsManager.getFont(FontId.SCORE_TICKER_FONT), getFormattedTimerText(), MAX_TIMER_TEXT_LENGTH, vertexBufferObjectManager);
        Sprite background = new Sprite(0, 0, texturesManager.getTextureRegion(TextureId.SCORE_BACKGROUND), vertexBufferObjectManager);
        background.setHeight(timerText.getHeight()+40f);
        background.setWidth(timerText.getWidth()+40f);
        background.setY(0);
        timerText.setY(background.getY()+20);
        timerText.setColor(1,0,0);

        scene.attachChild(background);
        scene.attachChild(timerText);
    }

    private String getFormattedTimerText() {
        return "Timer: " + timerValue;
    }

    private void registerTimerUpdater() {
        engine.registerUpdateHandler(new TimerHandler(TIMER_DECREMENT_INTERVAL_SECONDS, true, new ITimerCallback() {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler) {
                // Update timer text
                timerText.setText(getFormattedTimerText());
                // Maybe fire event for game over if timer ran out
                maybeFireEventForGameTimedOut();
                // Maybe show countdown text for the last COUNT_DOWN_THRESHOLD_SECONDS of the game
                maybeShowCountDownText();
                // Decrement the timer
                timerValue -= TIMER_DECREMENT_INTERVAL_SECONDS;
            }
        }));
    }

    private void maybeFireEventForGameTimedOut() {
        if (timerValue <= 0) {
            EventBus.get().sendEvent(GameEvent.GAME_TIMEOUT_EVENT);
        }
    }

    private void maybeShowCountDownText() {
        if (timerValue <= COUNT_DOWN_THRESHOLD_SECONDS) {
            addToScene(createCountDownText());
        }
    }

    private Text createCountDownText() {
        final Text countDownText =
                new Text(
                        0,
                        levelHeight / 3,
                        fontsManager.getFont(FontId.COUNT_DOWN_FONT),
                        Integer.toString(timerValue),
                        vertexBufferObjectManager);
        float newscale = levelWidth / countDownText.getWidth();

        gameAnimationManager.startModifier(
                countDownText,
                new ParallelEntityModifier(
                        new ScaleModifier(1.0f, newscale, 0f),
                        new AlphaModifier(1.0f, 0.0f, 1f)));

        countDownText.setX(levelWidth / 2 - countDownText.getWidth() / 2);
        countDownText.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        countDownText.setColor(1, 0, 0);
        return countDownText;
    }
}