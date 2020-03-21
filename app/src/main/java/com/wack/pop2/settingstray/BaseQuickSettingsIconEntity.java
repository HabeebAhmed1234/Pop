package com.wack.pop2.settingstray;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.resources.textures.TextureId;

import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.color.AndengineColor;

/**
 * The base class for all unlockable tool icons in the game.
 */
public abstract class BaseQuickSettingsIconEntity extends BaseEntity implements IOnAreaTouchListener {

    private Sprite iconSprite;

    private GameQuickSettingsHostTrayEntity quickSettingsTrayEntity;
    private GameTexturesManager gameTexturesManager;

    public BaseQuickSettingsIconEntity(
            GameQuickSettingsHostTrayEntity quickSettingsTrayEntity,
            GameTexturesManager gameTexturesManager,
            GameResources gameResources) {
        super(gameResources);
        this.quickSettingsTrayEntity = quickSettingsTrayEntity;
        this.gameTexturesManager = gameTexturesManager;
    }

    @Override
    public void onCreateScene() {
        createIcon();
    }

    @Override
    public void onDestroy() {
        iconSprite.removeOnAreaTouchListener();
    }

    private void createIcon() {
        iconSprite = new Sprite(
                0,
                0,
                gameTexturesManager.getTextureRegion(getIconTextureId()),
                vertexBufferObjectManager);
        setIconColor(getInitialIconColor());
        quickSettingsTrayEntity.addIcon(getIconId(), iconSprite, this);
    }

    protected Sprite getIconSprite () {
        return iconSprite;
    }

    protected void setIconColor(AndengineColor color) {
        iconSprite.setColor(color);
    }

    protected abstract TextureId getIconTextureId();

    protected abstract GameQuickSettingsHostTrayEntity.IconId getIconId();

    protected abstract AndengineColor getInitialIconColor();
}
