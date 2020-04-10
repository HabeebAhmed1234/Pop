package com.stupidfungames.pop.settingstray;

import android.content.Context;

import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.resources.textures.TextureId;
import com.stupidfungames.pop.tray.HostTrayCallback;
import com.stupidfungames.pop.tray.TrayOpenCloseButtonBaseEntity;

import static com.stupidfungames.pop.resources.textures.TextureId.QUICK_SETTINGS_BTN;

public class GameQuickSettingsTrayOpenCloseButton extends TrayOpenCloseButtonBaseEntity {

    public GameQuickSettingsTrayOpenCloseButton(HostTrayCallback hostTrayCallback, BinderEnity parent) {
        super(hostTrayCallback, parent);
    }

    @Override
    protected ButtonSpec getButtonSpec() {
        return new ButtonSpec(
                get(Context.class),
                32,
                4);
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
