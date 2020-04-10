package com.stupidfungames.pop.gameiconstray;

import android.content.Context;

import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.resources.textures.TextureId;
import com.stupidfungames.pop.tray.HostTrayCallback;
import com.stupidfungames.pop.tray.TrayOpenCloseButtonBaseEntity;

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
