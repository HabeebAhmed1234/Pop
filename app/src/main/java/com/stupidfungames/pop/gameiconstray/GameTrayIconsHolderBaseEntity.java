package com.stupidfungames.pop.gameiconstray;

import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.tray.HostTrayCallback;
import com.stupidfungames.pop.tray.TrayIconsHolderBaseEntity;
import org.andengine.util.color.AndengineColor;

public class GameTrayIconsHolderBaseEntity<IconIdType> extends
    TrayIconsHolderBaseEntity<IconIdType> {

  public GameTrayIconsHolderBaseEntity(
      HostTrayCallback hostTrayCallback,
      BinderEnity parent) {
    super(hostTrayCallback, parent);
  }

  @Override
  protected Spec getSpec() {
    return new Spec(
        LayoutOrientation.VERTICAL,
        0,
        8,
        16,
        64);
  }

  @Override
  protected AndengineColor getTrayBackgroundColor() {
    return AndengineColor.TRANSPARENT;
  }
}
