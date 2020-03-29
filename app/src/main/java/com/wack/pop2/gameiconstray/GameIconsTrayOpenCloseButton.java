package com.wack.pop2.gameiconstray;

import android.content.Context;

import com.wack.pop2.binder.Binder;
import com.wack.pop2.binder.BinderEnity;
import com.wack.pop2.resources.textures.TextureId;
import com.wack.pop2.tray.HostTrayCallback;
import com.wack.pop2.tray.TrayOpenCloseButtonBaseEntity;

class GameIconsTrayOpenCloseButton extends TrayOpenCloseButtonBaseEntity {

    public GameIconsTrayOpenCloseButton(
            HostTrayCallback trayOpenCloseControlCallback,
            BinderEnity parent) {
        super(trayOpenCloseControlCallback, parent);
    }

    @Override
    protected ButtonSpec getButtonSpec() {
        return new ButtonSpec(
                get(Context.class),
                64,
                4);
    }

    @Override
    protected TextureId getOpenButtonTextureId() {
        return TextureId.OPEN_BTN;
    }

    @Override
    protected TextureId getCloseButtonTextureId() {
        return TextureId.X_BTN;
    }
}
