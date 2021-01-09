package com.stupidfungames.pop.bubblespawn.bombbubbles;

import static com.stupidfungames.pop.utils.ScreenUtils.PERCENT_SPRITE_IN_SCREEN;

import com.stupidfungames.pop.utils.GeometryUtils;
import com.stupidfungames.pop.utils.ScreenUtils;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.text.Text;
import org.andengine.util.color.AndengineColor;

public class BombTimerEntityModifier extends TimerHandler implements ITimerCallback {

  public static final int RED_TIMER_THRESHOLD_SECONDS = 3;
  private static final float TIMER_INCREMENT_SECONDS = 1;

  private final Text timerText;
  private final IAreaShape bombBubble;
  private final BombBubbleExpiredListenerEntity expiredListenerEntity;
  private int timer;

  public BombTimerEntityModifier(
      Text timerText,
      float durationSeconds,
      BombBubbleExpiredListenerEntity expiredListenerEntity) {
    super(TIMER_INCREMENT_SECONDS, true, null);
    setTimerCallback(this);
    this.timerText = timerText;
    this.bombBubble = (IAreaShape) timerText.getParent();
    this.expiredListenerEntity = expiredListenerEntity;

    timer = (int) (durationSeconds / TIMER_INCREMENT_SECONDS);
    timerText.setText(Integer.toString(timer));
  }

  @Override
  public void onTimePassed(TimerHandler pTimerHandler) {
    if (ScreenUtils.isInScreen(bombBubble, PERCENT_SPRITE_IN_SCREEN)) {
      timer--;
      if (timer == 0) {
        // bomb exploded
        expiredListenerEntity.onBombBubbleExpired(bombBubble);
      } else if (timer > 0) {
        timerText.setText(Integer.toString(timer));
        timerText.setColor(
            timer <= RED_TIMER_THRESHOLD_SECONDS ? AndengineColor.RED : AndengineColor.WHITE);
        GeometryUtils.centerInHorizontal((IAreaShape) timerText.getParent(), timerText);
      }
    }
  }
}