package com.wack.pop2.settingstray;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.areatouch.GameAreaTouchListenerEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.fixturedefdata.BaseEntityUserData;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.resources.textures.TextureId;

import org.andengine.entity.sprite.Sprite;
import org.andengine.util.color.AndengineColor;

/**
 * The base class for all unlockable tool icons in the game.
 */
public abstract class BaseQuickSettingsIconEntity extends BaseEntity implements GameAreaTouchListenerEntity.AreaTouchListener {

    private Sprite iconSprite;
    private BaseEntityUserData iconUserData;

    private GameQuickSettingsHostTrayEntity quickSettingsTrayEntity;
    private GameTexturesManager gameTexturesManager;
    private GameAreaTouchListenerEntity touchListenerEntity;

    public BaseQuickSettingsIconEntity(
            GameQuickSettingsHostTrayEntity quickSettingsTrayEntity,
            GameTexturesManager gameTexturesManager,
            GameAreaTouchListenerEntity touchListenerEntity,
            GameResources gameResources) {
        super(gameResources);
        this.quickSettingsTrayEntity = quickSettingsTrayEntity;
        this.gameTexturesManager = gameTexturesManager;
        this.touchListenerEntity = touchListenerEntity;
    }

    @Override
    public void onCreateScene() {
        createIcon();
        touchListenerEntity.addAreaTouchListener(getIconUserDataType(), this);
    }

    @Override
    public void onDestroy() {
        touchListenerEntity.removeAreaTouchListener(getIconUserDataType(), this);
    }

    private void createIcon() {
        iconSprite = new Sprite(
                0,
                0,
                gameTexturesManager.getTextureRegion(getIconTextureId()),
                vertexBufferObjectManager);
        iconSprite.setUserData(getUserDataInternal());
        setIconColor(getInitialIconColor());
        quickSettingsTrayEntity.addIcon(getIconId(), iconSprite);
    }

    private BaseEntityUserData getUserDataInternal() {
        if (iconUserData == null) {
            iconUserData = getUserData();
        }
        return iconUserData;
    }

    protected Sprite getIconSprite () {
        return iconSprite;
    }

    protected void setIconColor(AndengineColor color) {
        iconSprite.setColor(color);
    }

    protected abstract TextureId getIconTextureId();

    protected abstract Class<? extends BaseEntityUserData> getIconUserDataType();

    protected abstract BaseEntityUserData getUserData();

    protected abstract GameQuickSettingsHostTrayEntity.IconId getIconId();

    protected abstract AndengineColor getInitialIconColor();
}
