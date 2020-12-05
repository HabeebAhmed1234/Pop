package com.stupidfungames.pop.icons;

import static com.stupidfungames.pop.eventbus.GameEvent.ICON_UNLOCKED;
import static com.stupidfungames.pop.eventbus.GameEvent.NO_UPGRADES_AVAILABLE;
import static com.stupidfungames.pop.eventbus.GameEvent.UPGRADES_AVAILABLE;

import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.eventbus.EventPayload;
import com.stupidfungames.pop.eventbus.GameEvent;
import com.stupidfungames.pop.eventbus.IconUnlockedEventPayload;

public abstract class BaseUpgradeableIconEntity extends BaseIconEntity {

  /**
   * Number of upgrades this icon has consumed.
   */
  private int upgradeLevel = 0;

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
        break;
      case NO_UPGRADES_AVAILABLE:
        break;
    }
  }

  /**
   * Number of upgrades this icon can take.
   */
  protected abstract int getIconUpgradesQuantity();
}
