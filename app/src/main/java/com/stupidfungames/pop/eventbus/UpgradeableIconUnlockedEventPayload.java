package com.stupidfungames.pop.eventbus;

public class UpgradeableIconUnlockedEventPayload implements EventPayload {

  public final int iconUpgradesQuantity;

  public UpgradeableIconUnlockedEventPayload(int iconUpgradesQuantity) {
    this.iconUpgradesQuantity = iconUpgradesQuantity;
  }
}
