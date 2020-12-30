package com.stupidfungames.pop.bubblespawn.bombbubbles;

import com.stupidfungames.pop.utils.ScreenUtils;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.shape.IShape;
import org.andengine.entity.text.Text;
import org.andengine.util.color.AndengineColor;
import org.andengine.util.modifier.BaseDurationModifier;

public class BombTimerEntityModifier extends BaseDurationModifier<IEntity> implements
    IEntityModifier {

  public static final int RED_TIMER_THRESHOLD_SECONDS = 3;

  public BombTimerEntityModifier(float pDuration) {
    super(pDuration);
  }

  public BombTimerEntityModifier(BombTimerEntityModifier other) {
    super(other.mDuration);
  }

  @Override
  protected void onManagedUpdate(float pSecondsElapsed, IEntity iEntity) {
    if (ScreenUtils.isInScreen((IShape) iEntity)) {
      int secondsLeft = (int) (getDuration() - getSecondsElapsed());
      int currentText = Integer.parseInt((String) ((Text) iEntity).getText());
      Text text = ((Text) iEntity);
      if (currentText != secondsLeft) {
        text.setText(Integer.toString(secondsLeft));
      }
      text.setColor(
          secondsLeft <= RED_TIMER_THRESHOLD_SECONDS ? AndengineColor.RED : AndengineColor.WHITE);
    }
  }

  @Override
  protected void onManagedInitialize(IEntity text) {
    ((Text) text).setText(Float.toString(getDuration()));
  }

  @Override
  public IEntityModifier deepCopy() throws DeepCopyNotSupportedException {
    return new BombTimerEntityModifier(this);
  }
}
