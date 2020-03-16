package com.wack.pop2.settingstray;

import com.wack.pop2.GameResources;
import com.wack.pop2.tray.BaseTrayIconsHolderEntity;
import com.wack.pop2.tray.HostTrayCallback;

import org.andengine.util.color.AndengineColor;

public class GameQuickSettingsTrayIconsHolderEntity<IconIdType> extends BaseTrayIconsHolderEntity<IconIdType> {

    public GameQuickSettingsTrayIconsHolderEntity(
            HostTrayCallback hostTrayCallback,
            GameResources gameResources) {
        super(hostTrayCallback, gameResources);
    }

    @Override
    protected Spec getSpec() {
        return new Spec(
                hostActivity.getActivityContext(),
                8,
                0,
                8,
                32);
    }

    @Override
    protected AndengineColor getTrayBackgroundColor() {
        return AndengineColor.TRANSPARENT;
    }
}
