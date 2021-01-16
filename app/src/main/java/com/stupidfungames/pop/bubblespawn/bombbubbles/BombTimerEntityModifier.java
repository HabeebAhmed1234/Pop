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

  private final Text timerText;
  private final IAreaShape bombBubble;
  private final BombBubbleExpiredListenerEntity expiredListenerEntity;
  private int timer;

  public BombTimerEntityModifier(
      Text timerText,
      int durationSeconds,
      BombBubbleExpiredListenerEntity expiredListenerEntity) {
    super(1, true, null);
    setTimerCallback(this);
    this.timerText = timerText;
    this.bombBubble = (IAreaShape) timerText.getParent();
    this.expiredListenerEntity = expiredListenerEntity;

    timer = durationSeconds;
    onTimerUpdated();
  }

  @Override
  public void onTimePassed(TimerHandler pTimerHandler) {
    if (ScreenUtils.isInScreen(bombBubble, PERCENT_SPRITE_IN_SCREEN)) {
      timer--;
      if (timer <= -1) {
        // bomb exploded
        expiredListenerEntity.onBombBubbleExpired(bombBubble);
      } else {
        onTimerUpdated();
      }
    }
  }

  private void onTimerUpdated() {
    timerText.setText(Integer.toString(timer));
    timerText.setColor(
        timer <= RED_TIMER_THRESHOLD_SECONDS ? AndengineColor.RED : AndengineColor.WHITE);
    GeometryUtils.centerInHorizontal((IAreaShape) timerText.getParent(), timerText);
  }
}
