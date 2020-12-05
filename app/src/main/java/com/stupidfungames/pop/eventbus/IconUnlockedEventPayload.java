package com.stupidfungames.pop.eventbus;

public class IconUnlockedEventPayload implements EventPayload{

  public final int iconUpgradesQuantity;

  public IconUnlockedEventPayload(int iconUpgradesQuantity) {
    this.iconUpgradesQuantity = iconUpgradesQuantity;
  }
}
