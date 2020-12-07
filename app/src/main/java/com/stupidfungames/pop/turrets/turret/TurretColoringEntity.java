package com.stupidfungames.pop.turrets.turret;

import static com.stupidfungames.pop.turrets.turret.TurretFiringEntity.TURRETS_FIRING_DELAY_SECONDS;
import static com.stupidfungames.pop.turrets.turret.TurretTargetingEntity.TARGETING_TIMER_UPDATE_INTERVAL_SECONDS;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.statemachine.BaseStateMachine.Listener;
import com.stupidfungames.pop.turrets.turret.TurretStateMachine.State;
import java.util.concurrent.TimeUnit;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.color.AndengineColor;

public class TurretColoringEntity extends BaseEntity implements Listener<State> {

  private Sprite turretBodySprite;
  private Sprite turretCannonSprite;
  private long lastTimeFiredMillis = -1;

  private TimerHandler targetingColorUpdateHandler = new TimerHandler(
      TARGETING_TIMER_UPDATE_INTERVAL_SECONDS,
      true,
      new ITimerCallback() {
        @Override
        public void onTimePassed(TimerHandler pTimerHandler) {
          setTargetingColor();
        }
      });

  public TurretColoringEntity(BinderEnity parent) {
    super(parent);
    get(TurretStateMachine.class).addAllStateTransitionListener(this);
  }

  public void setSprites(Sprite turretBodySprite, Sprite turretCannonSprite) {
    this.turretBodySprite = turretBodySprite;
    this.turretCannonSprite = turretCannonSprite;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    turretBodySprite = null;
    turretCannonSprite = null;
    get(TurretStateMachine.class).removeAllStateTransitionListener(this);
  }

  @Override
  public void onEnterState(State newState) {
    AndengineColor color = AndengineColor.WHITE;
    switch (newState) {
      case DRAGGING:
        color = AndengineColor.YELLOW;
        break;
      case FIRING:
        color = AndengineColor.RED;
        lastTimeFiredMillis = System.currentTimeMillis();
        break;
      case DOCKED:
        color = AndengineColor.TRANSPARENT;
        break;
      case TARGETING:
        color = null;
        if (!engine.containsUpdateHandler(targetingColorUpdateHandler)) {
          engine.registerUpdateHandler(targetingColorUpdateHandler);
        }
        break;
    }
    if (newState != State.TARGETING && engine.containsUpdateHandler(targetingColorUpdateHandler)) {
      engine.unregisterUpdateHandler(targetingColorUpdateHandler);
    }

    if (color != null) {
      if (turretBodySprite != null) {
        turretBodySprite.setColor(color);
      }
      if (turretCannonSprite != null) {
        turretCannonSprite.setColor(color);
      }
    }
  }

  private void setTargetingColor() {
    AndengineColor color = getTargetingColor();

    if (turretBodySprite != null) {
      turretBodySprite.setColor(color);
    }
    if (turretCannonSprite != null) {
      turretCannonSprite.setColor(color);
    }
  }

  private AndengineColor turretColor = AndengineColor.GREEN;

  private AndengineColor getTargetingColor() {
    if (lastTimeFiredMillis == -1) {
      return AndengineColor.GREEN;
    }
    float p = (float) (System.currentTimeMillis() - lastTimeFiredMillis) / ((float) TimeUnit.SECONDS
        .toMillis(TURRETS_FIRING_DELAY_SECONDS));
    if (p > 1) {
      p = 1;
    }
    AndengineColor from = AndengineColor.GREEN;
    AndengineColor to = AndengineColor.RED;
    turretColor.setRed(from.getRed() + (to.getRed() - from.getRed()) * p);
    turretColor.setGreen(from.getGreen() + (to.getGreen() - from.getGreen()) * p);
    turretColor.setBlue(from.getBlue() + (to.getBlue() - from.getBlue()) * p);

    return turretColor;
  }
}
