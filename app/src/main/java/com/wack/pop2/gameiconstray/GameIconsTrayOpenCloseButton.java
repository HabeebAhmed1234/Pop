package com.wack.pop2.gameiconstray;

import com.wack.pop2.GameResources;
import com.wack.pop2.fixturedefdata.BaseEntityUserData;
import com.wack.pop2.fixturedefdata.GameTrayCloseBtnUserData;
import com.wack.pop2.fixturedefdata.GameTrayOpenBtnUserData;
import com.wack.pop2.resources.textures.TextureId;
import com.wack.pop2.tray.BaseTrayOpenCloseButtonEntity;
import com.wack.pop2.tray.HostTrayCallback;

class GameIconsTrayOpenCloseButton extends BaseTrayOpenCloseButtonEntity {

    public GameIconsTrayOpenCloseButton(
            HostTrayCallback trayOpenCloseControlCallback,
            GameResources gameResources) {
        super(trayOpenCloseControlCallback, gameResources);
    }

    @Override
    protected ButtonSpec getButtonSpec() {
        return new ButtonSpec(
                hostActivity.getActivityContext(),
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

    @Override
    protected BaseEntityUserData getOpenButtonUserData() {
        return new GameTrayOpenBtnUserData();
    }

    @Override
    protected BaseEntityUserData getCloseButtonUserData() {
        return new GameTrayCloseBtnUserData();
    }
}
