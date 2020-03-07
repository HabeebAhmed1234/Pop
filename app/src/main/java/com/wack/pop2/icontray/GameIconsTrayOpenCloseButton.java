package com.wack.pop2.icontray;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameAreaTouchListenerEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.fixturedefdata.TrayCloseBtnUserData;
import com.wack.pop2.fixturedefdata.TrayOpenBtnUserData;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.resources.textures.TextureId;
import com.wack.pop2.statemachine.BaseStateMachine;
import com.wack.pop2.utils.ScreenUtils;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;

class GameIconsTrayOpenCloseButton extends BaseEntity implements BaseStateMachine.Listener<GameIconsTrayStateMachine.State> {

    private static final float ICON_SIZE_AS_PERCENT_OF_SCREEN_WIDTH = 0.15f;
    private static final int ICON_RIGHT_MARGIN_DP = 4;

    private Sprite iconSpriteOpen;
    private Sprite iconSpriteClose;
    private TrayCallback trayOpenCloseControlCallback;
    private GameAreaTouchListenerEntity touchListenerEntity;
    private GameTexturesManager texturesManager;
    private GameIconsTrayStateMachine stateMachine;

    private GameAreaTouchListenerEntity.AreaTouchListener openBtnTouchListener =
            new GameAreaTouchListenerEntity.AreaTouchListener () {
        @Override
        public boolean onTouch(TouchEvent pSceneTouchEvent, ITouchArea pTouchArea, float pTouchAreaLocalX, float pTouchAreaLocalY) {
            if (pSceneTouchEvent.isActionUp() && canOpen()) {
                onOpenBtnTouch();
                return true;
            }
            return false;
        }
    };

    private GameAreaTouchListenerEntity.AreaTouchListener closeBtnTouchListener =
            new GameAreaTouchListenerEntity.AreaTouchListener () {
                @Override
                public boolean onTouch(TouchEvent pSceneTouchEvent, ITouchArea pTouchArea, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                    if (pSceneTouchEvent.isActionUp() && canClose()) {
                        onCloseBtnTouch();
                        return true;
                    }
                    return false;
                }
            };

    public GameIconsTrayOpenCloseButton(
            TrayCallback trayOpenCloseControlCallback,
            GameAreaTouchListenerEntity touchListenerEntity,
            GameIconsTrayStateMachine stateMachine,
            GameTexturesManager texturesManager,
            GameResources gameResources) {
        super(gameResources);
        this.stateMachine = stateMachine;
        this.trayOpenCloseControlCallback = trayOpenCloseControlCallback;
        this.touchListenerEntity = touchListenerEntity;
        this.texturesManager = texturesManager;
    }

    @Override
    public void onCreateScene() {
        stateMachine.addAllStateTransitionListener(this);
    }

    @Override
    public void onDestroy() {
        stateMachine.removeAllStateTransitionListener(this);

        touchListenerEntity.removeAreaTouchListener(TrayOpenBtnUserData.class, openBtnTouchListener);
        touchListenerEntity.removeAreaTouchListener(TrayCloseBtnUserData.class, closeBtnTouchListener);
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

        addToSceneWithTouch(traySprite, iconSpriteOpen);
        addToSceneWithTouch(traySprite, iconSpriteClose);

        onIconsTrayPositionChanged(traySprite);
    }

    public void onIconsTrayPositionChanged(Rectangle traySprite) {
        if (iconSpriteOpen == null || iconSpriteClose == null) return;


        int iconX = -getIconSizePx() - getIconRightMarginPx();
        int iconY = (int) (traySprite.getHeightScaled() / 2 - getIconSizePx() / 2);
        setIconsPosition(iconX, iconY);
    }

    private boolean canOpen() {
        return stateMachine.getCurrentState() == GameIconsTrayStateMachine.State.CLOSING || stateMachine.getCurrentState() == GameIconsTrayStateMachine.State.CLOSED;
    }

    private boolean canClose() {
        return stateMachine.getCurrentState() == GameIconsTrayStateMachine.State.EXPANDING || stateMachine.getCurrentState() == GameIconsTrayStateMachine.State.EXPANDED;

    }

    private boolean isIconCreated() {
        return iconSpriteOpen != null && iconSpriteClose != null;
    }

    private void createIconSprite() {
        iconSpriteOpen = new Sprite(0,0,texturesManager.getTextureRegion(TextureId.OPEN_BTN), vertexBufferObjectManager);
        iconSpriteOpen.setWidth(getIconSizePx());
        iconSpriteOpen.setHeight(getIconSizePx());
        iconSpriteOpen.setUserData(new TrayOpenBtnUserData());
        iconSpriteOpen.setVisible(false);
        touchListenerEntity.addAreaTouchListener(TrayOpenBtnUserData.class, openBtnTouchListener);

        iconSpriteClose = new Sprite(0,0,texturesManager.getTextureRegion(TextureId.CLOSE_BTN), vertexBufferObjectManager);
        iconSpriteClose.setWidth(getIconSizePx());
        iconSpriteClose.setHeight(getIconSizePx());
        iconSpriteClose.setUserData(new TrayCloseBtnUserData());
        iconSpriteClose.setVisible(false);
        touchListenerEntity.addAreaTouchListener(TrayCloseBtnUserData.class, closeBtnTouchListener);
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
        if (newState == GameIconsTrayStateMachine.State.EXPANDED || newState == GameIconsTrayStateMachine.State.EXPANDING) {
            iconSpriteOpen.setVisible(false);
            iconSpriteClose.setVisible(true);
        } else if (newState == GameIconsTrayStateMachine.State.CLOSED || newState == GameIconsTrayStateMachine.State.CLOSING) {
            iconSpriteOpen.setVisible(true);
            iconSpriteClose.setVisible(false);

        }
    }

    private void onOpenBtnTouch() {
        trayOpenCloseControlCallback.openTray();
    }

    private void onCloseBtnTouch() {
        trayOpenCloseControlCallback.closeTray();
    }
}
