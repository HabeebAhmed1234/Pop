package com.stupidfungames.pop.eventbus;

public class UpgradeableIconLoadedEventPayload implements EventPayload {

  public final int upgradeLevel;

  public UpgradeableIconLoadedEventPayload(int upgradeLevel) {
    this.upgradeLevel = upgradeLevel;
  }
}
