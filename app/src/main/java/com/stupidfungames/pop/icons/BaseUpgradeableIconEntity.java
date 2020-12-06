package com.stupidfungames.pop.icons;

import static com.stupidfungames.pop.eventbus.GameEvent.ICON_UNLOCKED;
import static com.stupidfungames.pop.eventbus.GameEvent.NO_UPGRADES_AVAILABLE;
import static com.stupidfungames.pop.eventbus.GameEvent.UPGRADES_AVAILABLE;
import static com.stupidfungames.pop.eventbus.GameEvent.UPGRADE_CONSUMED;

import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.eventbus.EventPayload;
import com.stupidfungames.pop.eventbus.GameEvent;
import com.stupidfungames.pop.eventbus.IconUnlockedEventPayload;
import com.stupidfungames.pop.touchlisteners.ButtonUpTouchListener;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.color.AndengineColor;

public abstract class BaseUpgradeableIconEntity extends BaseIconEntity {

  /**
   * Number of upgrades this icon has consumed.
   */
  private int upgradeLevel = 0;
  private boolean isInUpgradeState = false;

  public BaseUpgradeableIconEntity(BinderEnity parent) {
    super(parent);
  }

  @Override
  public void onCreateScene() {
    super.onCreateScene();
    EventBus.get()
        .subscribe(UPGRADES_AVAILABLE, this)
        .subscribe(NO_UPGRADES_AVAILABLE, this);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    EventBus.get()
        .unSubscribe(UPGRADES_AVAILABLE, this)
        .unSubscribe(NO_UPGRADES_AVAILABLE, this);
  }

  @Override
  protected void onIconUnlocked() {
    EventBus.get()
        .sendEvent(ICON_UNLOCKED, new IconUnlockedEventPayload(getIconUpgradesQuantity()));
  }

  @Override
  public void onEvent(GameEvent event, EventPayload payload) {
    super.onEvent(event, payload);
    switch (event) {
      case UPGRADES_AVAILABLE:
        onUpgradesAvailable();
        break;
      case NO_UPGRADES_AVAILABLE:
        onNoUpgradesAvailable();
        break;
    }
  }

  private boolean canTakeMoreUpgrades() {
    return upgradeLevel < getIconUpgradesQuantity();
  }

  private void onUpgradesAvailable() {
    if (canTakeMoreUpgrades()) {
      enterUpgradeState();
    }
  }

  private void onNoUpgradesAvailable() {
    exitUpgradeState();
  }

  private void enterUpgradeState() {
    if (!isInUpgradeState) {
      isInUpgradeState = true;
      setIconColor(AndengineColor.CYAN);
      enableOverrideTouchListener(true);
    }

  }

  private void exitUpgradeState() {
    if (isInUpgradeState) {
      isInUpgradeState = false;
      setIconColor(getUnlockedIconColor());
      enableOverrideTouchListener(false);
    }
  }

  private void upgrade() {
    if (isInUpgradeState) {
      if (canTakeMoreUpgrades()) {
        EventBus.get().sendEvent(UPGRADE_CONSUMED);
        upgradeLevel++;
        onUpgraded(upgradeLevel);
      }
    }
    if (!canTakeMoreUpgrades()) {
      exitUpgradeState();
    }
  }

  protected abstract void onUpgraded(int upgradeLevel);

  /**
   * Number of upgrades this icon can take.
   */
  protected abstract int getIconUpgradesQuantity();

  @Override
  protected IOnAreaTouchListener getOverrideTouchListener() {
    return new ButtonUpTouchListener() {
      @Override
      protected boolean onButtonPressed(TouchEvent pSceneTouchEvent, ITouchArea pTouchArea,
          float pTouchAreaLocalX, float pTouchAreaLocalY) {
        if (isInUpgradeState) {
          upgrade();
          return true;
        } else {
          return false;
        }
      }
    };
  }
}
