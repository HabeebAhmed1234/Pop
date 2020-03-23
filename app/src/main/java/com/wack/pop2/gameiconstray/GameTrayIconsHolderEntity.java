package com.wack.pop2.gameiconstray;

import com.wack.pop2.GameResources;
import com.wack.pop2.tray.BaseTrayIconsHolderEntity;
import com.wack.pop2.tray.HostTrayCallback;

import org.andengine.util.color.AndengineColor;

public class GameTrayIconsHolderEntity<IconIdType> extends BaseTrayIconsHolderEntity<IconIdType> {

    public GameTrayIconsHolderEntity(
            HostTrayCallback hostTrayCallback,
            GameResources gameResources) {
        super(hostTrayCallback, gameResources);
    }

    @Override
    protected Spec getSpec() {
        return new Spec(
                hostActivity.getActivityContext(),
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
