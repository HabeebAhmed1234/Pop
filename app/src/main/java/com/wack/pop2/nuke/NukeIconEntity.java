package com.wack.pop2.nuke;

import com.wack.pop2.GameAreaTouchListenerEntity;
import com.wack.pop2.GameIconsTrayEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.fixturedefdata.BaseEntityUserData;
import com.wack.pop2.fixturedefdata.NukeIconEntityUserData;
import com.wack.pop2.icons.BaseIconEntity;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.resources.textures.TextureId;

import org.andengine.entity.scene.ITouchArea;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.color.AndengineColor;

import static com.wack.pop2.GameIconsTrayEntity.ICON_ID.NUKE_ICON;
import static com.wack.pop2.nuke.NukeConstants.NUKE_UNLOCK_THRESHOLD;

public class NukeIconEntity extends BaseIconEntity {

    private NukerEntity nukerEntity;

    public NukeIconEntity(
            NukerEntity nukerEntity,
            GameIconsTrayEntity gameIconsTrayEntity,
            GameTexturesManager gameTexturesManager,
            GameAreaTouchListenerEntity touchListenerEntity,
            GameResources gameResources) {
        super(gameIconsTrayEntity, gameTexturesManager, touchListenerEntity, gameResources);
        this.nukerEntity = nukerEntity;
    }

    @Override
    protected TextureId getIconTextureId() {
        return TextureId.NUKE_ICON;
    }

    @Override
    protected Class<? extends BaseEntityUserData> getIconUserDataType() {
        return NukeIconEntityUserData.class;
    }

    @Override
    protected BaseEntityUserData getUserData() {
        return new NukeIconEntityUserData();
    }

    @Override
    protected GameIconsTrayEntity.ICON_ID getIconId() {
        return NUKE_ICON;
    }

    @Override
    protected int getDifficultyUnlockThreshold() {
        return NUKE_UNLOCK_THRESHOLD;
    }

    @Override
    protected void onIconUnlocked() {

    }

    @Override
    protected AndengineColor getUnlockedColor() {
        return AndengineColor.GREEN;
    }

    @Override
    public boolean onTouch(TouchEvent pSceneTouchEvent, ITouchArea pTouchArea, float pTouchAreaLocalX, float pTouchAreaLocalY) {
        nukerEntity.startNuke();
        return true;
    }
}
