package com.stupidfungames.pop.eventbus;

import com.stupidfungames.pop.gameiconstray.GameIconsHostTrayEntity.IconId;

public class IconUnlockedEventPayload implements EventPayload {

  public final IconId unlockedIconId;

  public IconUnlockedEventPayload(IconId unlockedIconId) {
    this.unlockedIconId = unlockedIconId;
  }
}
