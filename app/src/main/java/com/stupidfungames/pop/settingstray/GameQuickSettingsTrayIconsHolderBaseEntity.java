package com.stupidfungames.pop.settingstray;

import com.stupidfungames.pop.R;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.tray.HostTrayCallback;
import com.stupidfungames.pop.tray.TrayIconsHolderBaseEntity;
import org.andengine.util.color.AndengineColor;

public class GameQuickSettingsTrayIconsHolderBaseEntity<IconIdType> extends
    TrayIconsHolderBaseEntity<IconIdType> {

  public GameQuickSettingsTrayIconsHolderBaseEntity(
      HostTrayCallback hostTrayCallback,
      BinderEnity parent) {
    super(hostTrayCallback, parent);
  }

  @Override
  protected Spec getSpec() {
    return new Spec(
        LayoutOrientation.HORIZONTAL,
        getDimenPx(R.dimen.quick_settings_padding_horizontal),
        getDimenPx(R.dimen.quick_settings_padding_vertical),
        getDimenPx(R.dimen.quick_settings_padding_between_icons),
        getDimenPx(R.dimen.quick_settings_icon_size));
  }

  @Override
  protected AndengineColor getTrayBackgroundColor() {
    return AndengineColor.TRANSPARENT;
  }
}
