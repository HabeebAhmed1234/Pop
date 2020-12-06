package com.stupidfungames.pop.upgrades;

import static com.stupidfungames.pop.eventbus.GameEvent.NO_UPGRADES_AVAILABLE;
import static com.stupidfungames.pop.eventbus.GameEvent.UPGRADES_AVAILABLE;
import static com.stupidfungames.pop.eventbus.GameEvent.UPGRADE_ACQUIRED;
import static com.stupidfungames.pop.eventbus.GameEvent.UPGRADE_CONSUMED;

import android.util.Log;
import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.eventbus.EventBus.Subscriber;
import com.stupidfungames.pop.eventbus.EventPayload;
import com.stupidfungames.pop.eventbus.GameEvent;

/**
 * Maintains a set of upgrades that are ready to be applied to tools. Fires off upgrades available
 * and unavailable events to icons
 */
public class UpgradeInventory extends BaseEntity implements Subscriber {

  private int numAvailableUpgrades = 0;

  public UpgradeInventory(BinderEnity parent) {
    super(parent);
  }

  @Override
  public void onCreateScene() {
    super.onCreateScene();
    EventBus.get()
        .subscribe(UPGRADE_ACQUIRED, this)
        .subscribe(UPGRADE_CONSUMED, this);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    EventBus.get()
        .unSubscribe(UPGRADE_ACQUIRED, this)
        .unSubscribe(UPGRADE_CONSUMED, this);
  }

  @Override
  public void onEvent(GameEvent event, EventPayload payload) {
    switch (event) {
      case UPGRADE_ACQUIRED:
        onUpgradeAcquired();
        break;
      case UPGRADE_CONSUMED:
        onUpgradeConsumed();
        break;
    }
  }

  private void onUpgradeAcquired() {
    numAvailableUpgrades++;
    onUpgradesInventoryChanged();
  }

  private void onUpgradeConsumed() {
    numAvailableUpgrades--;
    onUpgradesInventoryChanged();

  }

  private void onUpgradesInventoryChanged() {
    if (numAvailableUpgrades > 0) {
      Log.d("asdasd", numAvailableUpgrades + " UPGRADES_AVAILABLE");
      EventBus.get().sendEvent(UPGRADES_AVAILABLE);
    } else {
      Log.d("asdasd", "NO_UPGRADES_AVAILABLE");
      EventBus.get().sendEvent(NO_UPGRADES_AVAILABLE);
    }
  }
}
