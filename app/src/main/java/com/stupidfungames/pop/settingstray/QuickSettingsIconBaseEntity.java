package com.stupidfungames.pop.settingstray;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.resources.textures.GameTexturesManager;
import com.stupidfungames.pop.resources.textures.TextureId;

import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.color.AndengineColor;

/**
 * The base class for all unlockable tool icons in the game.
 */
public abstract class QuickSettingsIconBaseEntity extends BaseEntity {

    private Sprite iconSprite;

    public QuickSettingsIconBaseEntity(BinderEnity parent) {
        super(parent);
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
                get(GameTexturesManager.class).getTextureRegion(getIconTextureId()),
                vertexBufferObjectManager);
        setIconColor(getInitialIconColor());
        get(GameQuickSettingsHostTrayBaseEntity.class).addIcon(getIconId(), iconSprite, getTouchListener());
    }

    protected Sprite getIconSprite () {
        return iconSprite;
    }

    protected void setIconColor(AndengineColor color) {
        iconSprite.setColor(color);
    }

    protected abstract TextureId getIconTextureId();

    protected abstract GameQuickSettingsHostTrayBaseEntity.IconId getIconId();

    protected abstract AndengineColor getInitialIconColor();

    protected abstract IOnAreaTouchListener getTouchListener();
}
