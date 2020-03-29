package com.wack.pop2.settingstray;

import android.content.Context;

import com.wack.pop2.binder.Binder;
import com.wack.pop2.binder.BinderEnity;
import com.wack.pop2.resources.textures.TextureId;
import com.wack.pop2.tray.HostTrayCallback;
import com.wack.pop2.tray.TrayOpenCloseButtonBaseEntity;

import static com.wack.pop2.resources.textures.TextureId.QUICK_SETTINGS_BTN;

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
