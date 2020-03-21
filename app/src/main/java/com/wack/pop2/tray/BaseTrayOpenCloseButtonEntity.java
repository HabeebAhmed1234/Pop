package com.wack.pop2.tray;

import android.content.Context;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.fixturedefdata.BaseEntityUserData;
import com.wack.pop2.resources.textures.TextureId;
import com.wack.pop2.statemachine.BaseStateMachine;
import com.wack.pop2.touchlisteners.ButtonUpTouchListener;
import com.wack.pop2.utils.ScreenUtils;

import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.color.AndengineColor;

public abstract class BaseTrayOpenCloseButtonEntity extends BaseEntity implements BaseStateMachine.Listener<TrayStateMachine.State> {

    public static class ButtonSpec {

        public final int iconSizePx;
        public final int iconRightMarginPx;

        public ButtonSpec(Context context,
                          int iconSizeDp,
                          int iconRightMarginDp) {
            iconSizePx = ScreenUtils.dpToPx(iconSizeDp, context);
            iconRightMarginPx = ScreenUtils.dpToPx(iconRightMarginDp, context);
        }
    }

    private ButtonSpec buttonSpec;

    private Sprite iconSpriteOpen;
    private Sprite iconSpriteClose;
    private HostTrayCallback hostTrayCallback;

    private IOnAreaTouchListener toggleBtnTouchListener =
            new ButtonUpTouchListener() {
                @Override
                protected boolean onButtonPressed(TouchEvent pSceneTouchEvent, ITouchArea pTouchArea, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                    if (canOpen()) {
                        onOpenBtnTouch();
                        return true;
                    } else if (canClose()) {
                        onCloseBtnTouch();
                        return true;
                    }
                    return false;
                }

                @Override
                protected void onPressedStateChanged(boolean isPressed) {
                    setIconSpriteColor(isPressed ? AndengineColor.GREEN : AndengineColor.WHITE);
                }
            };

    public BaseTrayOpenCloseButtonEntity(
            HostTrayCallback hostTrayCallback,
            GameResources gameResources) {
        super(gameResources);
        this.hostTrayCallback = hostTrayCallback;
    }

    protected abstract ButtonSpec getButtonSpec();
    protected abstract TextureId getOpenButtonTextureId();
    protected abstract TextureId getCloseButtonTextureId();
    protected abstract BaseEntityUserData getOpenButtonUserData();
    protected abstract BaseEntityUserData getCloseButtonUserData();

    private ButtonSpec getButtonSpecInternal() {
        if (buttonSpec == null) {
            buttonSpec = getButtonSpec();
        }
        return buttonSpec;
    }

    @Override
    public void onCreateScene() {
        hostTrayCallback.getStateMachine().addAllStateTransitionListener(this);
    }

    @Override
    public void onDestroy() {
        hostTrayCallback.getStateMachine().removeAllStateTransitionListener(this);

        iconSpriteOpen.removeOnAreaTouchListener();
        iconSpriteClose.removeOnAreaTouchListener();
    }

    @Override
    public void onEnterState(TrayStateMachine.State newState) {
        if (isIconCreated()) {
            updateIconTexture(newState);
        }
    }

    public void onIconsTrayInitialized() {
        if (!(isIconCreated())) {
            createIconSprite();
        }

        // we only need one of the icons to be touchable
        addToSceneWithTouch(hostTrayCallback.getTrayIconsHolderRectangle(), iconSpriteOpen, toggleBtnTouchListener);
        addToScene(hostTrayCallback.getTrayIconsHolderRectangle(), iconSpriteClose);

        refreshDimensions();
    }

    public void onIconsTrayDimensionsChanged() {
        refreshDimensions();
    }

    public void refreshDimensions() {
        if (iconSpriteOpen == null || iconSpriteClose == null) return;


        int iconX = -getButtonSpecInternal().iconSizePx - getButtonSpecInternal().iconRightMarginPx;
        int iconY = (int) (hostTrayCallback.getTrayIconsHolderRectangle().getHeightScaled() / 2 - getButtonSpecInternal().iconSizePx / 2);
        setIconPosition(iconX, iconY);
    }

    private boolean canOpen() {
        return hostTrayCallback.getStateMachine().getCurrentState() == TrayStateMachine.State.CLOSING
                || hostTrayCallback.getStateMachine().getCurrentState() == TrayStateMachine.State.CLOSED;
    }

    private boolean canClose() {
        return hostTrayCallback.getStateMachine().getCurrentState() == TrayStateMachine.State.EXPANDING
                || hostTrayCallback.getStateMachine().getCurrentState() == TrayStateMachine.State.EXPANDED;

    }

    private boolean isIconCreated() {
        return iconSpriteOpen != null && iconSpriteClose != null;
    }

    private void createIconSprite() {
        iconSpriteOpen = new Sprite(
                0,
                0,
                hostTrayCallback.getTextureManager().getTextureRegion(getOpenButtonTextureId()),
                vertexBufferObjectManager);
        iconSpriteOpen.setWidth(getButtonSpecInternal().iconSizePx);
        iconSpriteOpen.setHeight(getButtonSpecInternal().iconSizePx);
        iconSpriteOpen.setUserData(getOpenButtonUserData());
        iconSpriteOpen.setVisible(false);

        iconSpriteClose = new Sprite(
                0,
                0,
                hostTrayCallback.getTextureManager().getTextureRegion(getCloseButtonTextureId()),
                vertexBufferObjectManager);
        iconSpriteClose.setWidth(getButtonSpecInternal().iconSizePx);
        iconSpriteClose.setHeight(getButtonSpecInternal().iconSizePx);
        iconSpriteClose.setUserData(getCloseButtonUserData());
        iconSpriteClose.setVisible(false);
    }

    private void setIconSpriteColor(AndengineColor color) {
        if (iconSpriteOpen != null && iconSpriteClose != null) {
            iconSpriteOpen.setColor(color);
            iconSpriteClose.setColor(color);
        }
    }

    private void setIconPosition(float x, float y) {
        iconSpriteOpen.setX(x);
        iconSpriteClose.setX(x);

        iconSpriteOpen.setY(y);
        iconSpriteClose.setY(y);
    }

    private void updateIconTexture(TrayStateMachine.State newState) {
        if (newState == TrayStateMachine.State.EXPANDED || newState == TrayStateMachine.State.EXPANDING) {
            iconSpriteOpen.setVisible(false);
            iconSpriteClose.setVisible(true);
        } else if (newState == TrayStateMachine.State.CLOSED || newState == TrayStateMachine.State.CLOSING) {
            iconSpriteOpen.setVisible(true);
            iconSpriteClose.setVisible(false);

        }
    }

    private void onOpenBtnTouch() {
        hostTrayCallback.openTray();
    }

    private void onCloseBtnTouch() {
        hostTrayCallback.closeTray();
    }
}
