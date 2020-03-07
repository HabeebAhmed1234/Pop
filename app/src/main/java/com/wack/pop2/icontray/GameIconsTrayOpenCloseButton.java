package com.wack.pop2.icontray;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.resources.textures.TextureId;
import com.wack.pop2.statemachine.BaseStateMachine;
import com.wack.pop2.utils.ScreenUtils;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;

class GameIconsTrayOpenCloseButton extends BaseEntity implements BaseStateMachine.Listener<GameIconsTrayStateMachine.State> {

    private static final float ICON_SIZE_AS_PERCENT_OF_SCREEN_WIDTH = 0.15f;
    private static final int ICON_RIGHT_MARGIN_DP = 16;

    private Sprite iconSpriteOpen;
    private Sprite iconSpriteClose;
    private GameTexturesManager texturesManager;
    private GameIconsTrayStateMachine stateMachine;

    public GameIconsTrayOpenCloseButton(GameIconsTrayStateMachine stateMachine, GameTexturesManager texturesManager, GameResources gameResources) {
        super(gameResources);
        this.stateMachine = stateMachine;
        this.texturesManager = texturesManager;
    }

    @Override
    public void onCreateScene() {
        stateMachine.addAllStateTransitionListener(this);
    }

    @Override
    public void onDestroy() {
        stateMachine.removeAllStateTransitionListener(this);
    }

    @Override
    public void onEnterState(GameIconsTrayStateMachine.State newState) {
        if (isIconCreated()) {
            updateIconTexture(newState);
        }
    }

    public void onIconsTrayCreated(Rectangle traySprite) {
        if (!(isIconCreated())) {
            createIconSprite();
        }

        addToScene(traySprite, iconSpriteOpen);
        addToScene(traySprite, iconSpriteClose);

        onIconsTrayPositionChanged(traySprite);
    }

    public void onIconsTrayPositionChanged(Rectangle traySprite) {
        if (iconSpriteOpen == null || iconSpriteClose == null) return;


        int iconX = -getIconSizePx() - getIconRightMarginPx();
        int iconY = (int) (traySprite.getHeightScaled() / 2 - getIconSizePx() / 2);
        setIconsPosition(iconX, iconY);
    }

    private boolean isIconCreated() {
        return iconSpriteOpen != null && iconSpriteClose != null;
    }

    private void createIconSprite() {
        iconSpriteOpen = new Sprite(0,0,texturesManager.getTextureRegion(TextureId.OPEN_BTN), vertexBufferObjectManager);
        iconSpriteOpen.setWidth(getIconSizePx());
        iconSpriteOpen.setHeight(getIconSizePx());
        iconSpriteOpen.setVisible(false);

        iconSpriteClose = new Sprite(0,0,texturesManager.getTextureRegion(TextureId.CLOSE_BTN), vertexBufferObjectManager);
        iconSpriteClose.setWidth(getIconSizePx());
        iconSpriteClose.setHeight(getIconSizePx());
        iconSpriteOpen.setVisible(false);
    }


    private void setIconsPosition(float x, float y) {
        iconSpriteOpen.setX(x);
        iconSpriteClose.setX(x);

        iconSpriteOpen.setY(y);
        iconSpriteClose.setY(y);
    }

    private int getIconSizePx() {
        return (int)(ScreenUtils.getSreenSize().width * ICON_SIZE_AS_PERCENT_OF_SCREEN_WIDTH);
    }

    private int getIconRightMarginPx() {
        return ScreenUtils.dpToPx(ICON_RIGHT_MARGIN_DP, hostActivity.getActivityContext());
    }

    private void updateIconTexture(GameIconsTrayStateMachine.State newState) {
        if (newState == GameIconsTrayStateMachine.State.EXPANDED) {
            iconSpriteOpen.setVisible(false);
            iconSpriteClose.setVisible(true);
        } else if (newState == GameIconsTrayStateMachine.State.CLOSED) {
            iconSpriteOpen.setVisible(true);
            iconSpriteClose.setVisible(false);

        }
    }
}
