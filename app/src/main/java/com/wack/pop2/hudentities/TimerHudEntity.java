package com.wack.pop2.hudentities;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.binder.Binder;
import com.wack.pop2.binder.BinderEnity;
import com.wack.pop2.resources.fonts.FontId;
import com.wack.pop2.resources.fonts.GameFontsManager;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.text.Text;
import org.andengine.util.color.AndengineColor;

/**
 * Entity that contains score hud panel
 */
public class TimerHudEntity extends BaseEntity {

    private static final int TIMER_STARTING_VALUE_SECONDS = 0;
    private static final int TIMER_INCREMENT_INTERVAL_SECONDS = 1;

    private static final int MAX_TIMER_TEXT_LENGTH = 20;

    private TimerHandler timerHandler;

    private Text timerText;
    private int timerValue = TIMER_STARTING_VALUE_SECONDS;

    public TimerHudEntity(BinderEnity parent) {
        super(parent);
    }

    @Override
    public void onCreateScene() {
        registerTimerUpdater();
        timerText = new Text(20,20, get(GameFontsManager.class).getFont(FontId.SCORE_TICKER_FONT), getFormattedTimerText(), MAX_TIMER_TEXT_LENGTH, vertexBufferObjectManager);
        timerText.setColor(AndengineColor.TRANSPARENT);
        scene.attachChild(timerText);
    }

    @Override
    public void onDestroy() {
        engine.unregisterUpdateHandler(timerHandler);
    }

    private String getFormattedTimerText() {
        return "Timer: " + timerValue;
    }

    private void registerTimerUpdater() {
        timerHandler = new TimerHandler(TIMER_INCREMENT_INTERVAL_SECONDS, true, new ITimerCallback() {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler) {
                // Update timer text
                timerText.setText(getFormattedTimerText());
                // Decrement the timer
                timerValue += TIMER_INCREMENT_INTERVAL_SECONDS;
            }
        });
        engine.registerUpdateHandler(timerHandler);
    }
}