package com.wack.pop2.gameiconstray;

import android.content.Context;

import com.wack.pop2.binder.Binder;
import com.wack.pop2.binder.BinderEnity;
import com.wack.pop2.tray.HostTrayCallback;
import com.wack.pop2.tray.TrayIconsHolderBaseEntity;

import org.andengine.util.color.AndengineColor;

public class GameTrayIconsHolderBaseEntity<IconIdType> extends TrayIconsHolderBaseEntity<IconIdType> {

    public GameTrayIconsHolderBaseEntity(
            HostTrayCallback hostTrayCallback,
            BinderEnity parent) {
        super(hostTrayCallback, parent);
    }

    @Override
    protected Spec getSpec() {
        return new Spec(
                get(Context.class),
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
