package com.wack.pop2.settingstray;

import com.wack.pop2.GameResources;
import com.wack.pop2.fixturedefdata.BaseEntityUserData;
import com.wack.pop2.fixturedefdata.GameQuickSettingsCloseBtnUserData;
import com.wack.pop2.fixturedefdata.GameQuickSettingsOpenBtnUserData;
import com.wack.pop2.resources.textures.TextureId;
import com.wack.pop2.tray.BaseTrayOpenCloseButtonEntity;
import com.wack.pop2.tray.HostTrayCallback;

import static com.wack.pop2.resources.textures.TextureId.QUICK_SETTINGS_BTN;

public class GameQuickSettingsTrayOpenCloseButton extends BaseTrayOpenCloseButtonEntity {

    public GameQuickSettingsTrayOpenCloseButton(HostTrayCallback hostTrayCallback, GameResources gameResources) {
        super(hostTrayCallback, gameResources);
    }

    @Override
    protected ButtonSpec getButtonSpec() {
        return new ButtonSpec(
                hostActivity.getActivityContext(),
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

    @Override
    protected BaseEntityUserData getOpenButtonUserData() {
        return new GameQuickSettingsOpenBtnUserData();
    }

    @Override
    protected BaseEntityUserData getCloseButtonUserData() {
        return new GameQuickSettingsCloseBtnUserData();
    }
}
