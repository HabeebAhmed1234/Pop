package com.stupidfungames.pop.settingstray;

import static com.stupidfungames.pop.resources.textures.TextureId.QUICK_SETTINGS_BTN;

import com.stupidfungames.pop.R;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.resources.textures.TextureId;
import com.stupidfungames.pop.tray.HostTrayCallback;
import com.stupidfungames.pop.tray.TrayOpenCloseButtonBaseEntity;

public class GameQuickSettingsTrayOpenCloseButton extends TrayOpenCloseButtonBaseEntity {

  public GameQuickSettingsTrayOpenCloseButton(HostTrayCallback hostTrayCallback,
      BinderEnity parent) {
    super(hostTrayCallback, parent);
  }

  @Override
  protected ButtonSpec getButtonSpec() {
    return new ButtonSpec(
        (int) getDimenPx(R.dimen.quick_settings_open_close_btn_size),
        (int) getDimenPx(R.dimen.quick_settings_open_close_btn_right_margin));
  }

  @Override
  protected TextureId getOpenButtonTextureId() {
    return QUICK_SETTINGS_BTN;
  }

  @Override
  protected TextureId getCloseButtonTextureId() {
    return QUICK_SETTINGS_BTN;
  }
}
