package com.stupidfungames.pop.gameiconstray;

import com.stupidfungames.pop.R;
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
        getDimenPx(R.dimen.game_icons_tray_padding_horizontal),
        getDimenPx(R.dimen.game_icons_tray_padding_vertical),
        getDimenPx(R.dimen.game_icons_tray_padding_between_icons),
        getDimenPx(R.dimen.game_icons_tray_icons_size));
  }

  @Override
  protected AndengineColor getTrayBackgroundColor() {
    return AndengineColor.TRANSPARENT;
  }
}
