package com.stupidfungames.pop.sword;

import static com.stupidfungames.pop.sword.SwordStateMachine.State.CHARGED;
import static com.stupidfungames.pop.sword.SwordStateMachine.State.UNCHARGED;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.BinderEnity;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;

/**
 * Sets and manages the swords current charge.
 */
public class SwordChargeManager extends BaseEntity {

  private static final int BASE_SWORD_COOLDOWN_SECONDS = 15;
  private static final int SWORD_COOLDOWN_SECONDS_DECREASE_PER_LEVEL = 3;

  private static final int BASE_SWORD_CHARGE = 5;
  private static final int SWORD_ADDITIONAL_CHARGE_PER_LEVEL = 5;

  private TimerHandler rechargerHandler = null;

  private int swordCooldownSeconds = BASE_SWORD_COOLDOWN_SECONDS;
  private int currentMaxCharge = BASE_SWORD_CHARGE;

  private int currentSwordCharge = 0;

  public SwordChargeManager(BinderEnity parent) {
    super(parent);
  }

  public void onUpgrade(int newUpgradeLevel) {
    swordCooldownSeconds = BASE_SWORD_COOLDOWN_SECONDS - newUpgradeLevel * SWORD_COOLDOWN_SECONDS_DECREASE_PER_LEVEL;
    currentMaxCharge = BASE_SWORD_CHARGE + newUpgradeLevel * SWORD_ADDITIONAL_CHARGE_PER_LEVEL;

    setCharge(currentMaxCharge);
  }

  public void decrementCharge() {
    setCharge(currentSwordCharge - 1);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    rechargerHandler.reset();
    rechargerHandler.pause();
  }

  private void setCharge(int newCharge) {
    currentSwordCharge = newCharge;
    if (currentSwordCharge == 0) {
      get(SwordStateMachine.class).transitionState(UNCHARGED);
      scheduleRecharge();
    } else if (currentSwordCharge == currentMaxCharge) {
      get(SwordStateMachine.class).transitionState(CHARGED);
    }
  }

  private void scheduleRecharge() {
    if (rechargerHandler != null) {
      rechargerHandler.pause();
      rechargerHandler.setTimerSeconds(swordCooldownSeconds);
      rechargerHandler.reset();
    } else {
      rechargerHandler = new TimerHandler(swordCooldownSeconds, new ITimerCallback() {
        @Override
        public void onTimePassed(TimerHandler pTimerHandler) {
          setCharge(currentMaxCharge);
        }
      });
      engine.registerUpdateHandler(rechargerHandler);
    }
  }
}
