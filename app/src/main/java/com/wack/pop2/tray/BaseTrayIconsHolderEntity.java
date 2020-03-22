package com.wack.pop2.tray;

import android.content.Context;
import android.util.Pair;

import androidx.annotation.Nullable;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.utils.ScreenUtils;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.color.AndengineColor;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseTrayIconsHolderEntity<IconIdType> extends BaseEntity {

    private final HostTrayCallback hostTrayCallback;

    public static class Spec {

        public final int paddingHorizontalPx;
        public final int paddingVerticalPx;
        public final int paddingBetweenIconsPx;
        public final int iconsSizePx;

        public Spec(Context context,
                        int paddingHorizontalDp,
                        int paddingVerticalDp,
                        int paddingBetweenIconsDp,
                        int iconsSizeDp) {
            paddingHorizontalPx = ScreenUtils.dpToPx(paddingHorizontalDp, context);
            paddingVerticalPx = ScreenUtils.dpToPx(paddingVerticalDp, context);
            paddingBetweenIconsPx = ScreenUtils.dpToPx(paddingBetweenIconsDp, context);
            iconsSizePx = ScreenUtils.dpToPx(iconsSizeDp, context);
        }
    }

    private Spec spec;
    private Rectangle iconsTray;
    private final List<Pair<IconIdType, Sprite>> icons = new ArrayList<>();

    public BaseTrayIconsHolderEntity(
            HostTrayCallback hostTrayCallback,
            GameResources gameResources) {
        super(gameResources);
        this.hostTrayCallback = hostTrayCallback;
    }

    protected abstract Spec getSpec();
    protected abstract AndengineColor getTrayBackgroundColor();

    private Spec getSpecInternal() {
        if (spec == null) {
            spec = getSpec();
        }
        return spec;
    }

    public Rectangle getTrayIconsHolderRectangle() {
        return iconsTray;
    }

    @Override
    public void onCreateScene() {
        super.onCreateScene();
        iconsTray = new Rectangle(0,0, 0, 0, vertexBufferObjectManager);
        iconsTray.setColor(getTrayBackgroundColor());
        scene.attachChild(iconsTray);

        refreshDimensions(null);
        refreshTrayPositionOnAnchor();
        hostTrayCallback.onIconsTrayInitialized();
    }

    public void addIcon(IconIdType iconId, Sprite iconSprite, IOnAreaTouchListener areaTouchListener) {
        addToSceneWithTouch(iconsTray, iconSprite, areaTouchListener);
        icons.add(Pair.create(iconId, iconSprite));
        refreshDimensions(iconSprite);
    }

    @Nullable
    public Sprite getIcon(IconIdType id) {
        for (Pair<IconIdType, Sprite> pair : icons) {
            if (pair.first == id) {
                return pair.second;
            }
        }
        return null;
    }

    public void refreshDimensions(@Nullable Sprite newIcon){
        if (newIcon != null) {
            applyIconSize(newIcon);
        }
        refreshTrayDimensions();
        refreshTrayPositionOnAnchor();
        refreshIconPostitions();
        hostTrayCallback.onIconsTrayDimensionsChanged();
    }

    /**
     * The position of this tray's left anchor has changed. Update the rectangle that holds the icons
     * to this new position.
     */
    public void refreshTrayPositionOnAnchor() {
        int[] anchor = hostTrayCallback.getAnchorPx();
        TrayStateMachine.State currentState = hostTrayCallback.getStateMachine().getCurrentState();
        if (iconsTray != null) {
            int xPx = 0;
            if (currentState == TrayStateMachine.State.CLOSED || currentState == TrayStateMachine.State.EMPTY) {
                xPx = anchor[0];
            } else if (currentState == TrayStateMachine.State.EXPANDED) {
                xPx = anchor[0] - getTrayWidthPx();
            }
            if (xPx != 0) {
                iconsTray.setX(xPx);
            }
            iconsTray.setY(anchor[1] - getTrayHeightPx() / 2);
        }
    }

    /**
     * Calculates the size of the tray given the icons in it and sets the dimensiosn
     */
    private void refreshTrayDimensions() {
        iconsTray.setWidth(getTrayWidthPx());
        iconsTray.setHeight(getTrayHeightPx());
    }

    private void refreshIconPostitions() {
        for (int i = 0 ; i < icons.size() ; i++) {
            Pair<IconIdType, Sprite> iconPair = icons.get(i);
            Sprite icon = iconPair.second;

            // Set the posistion of this icon
            icon.setX(getPaddingHorizontalPx());
            icon.setY(
                    getPaddingVerticalPx()
                            + (i * getIconSizePx())
                            + (i * getPaddingBetweenPx()));
        }
    }

    /**
     * Sets the given icon sprite to the correct widthPx and heightPx
     * @param iconSprite
     */
    private void applyIconSize(Sprite iconSprite) {
        int iconSizePx = getSpecInternal().iconsSizePx;
        iconSprite.setWidth(iconSizePx);
        iconSprite.setHeight(iconSizePx);
    }

    /**
     * Returns the widthPx of the tray including internal padding
     */
    public int getTrayWidthPx() {
        return getPaddingHorizontalPx() * 2 + getIconSizePx();
    }

    public int getTrayHeightPx() {
        return getPaddingVerticalPx() * 2
                + getIconSizePx() * icons.size()
                + getPaddingBetweenPx() * (icons.size() -1);
    }

    private int getIconSizePx() {
        return getSpecInternal().iconsSizePx;
    }

    private int getPaddingHorizontalPx() {
        return getSpecInternal().paddingHorizontalPx;
    }

    private int getPaddingVerticalPx() {
        return getSpecInternal().paddingVerticalPx;
    }

    private int getPaddingBetweenPx() {
        return getSpecInternal().paddingBetweenIconsPx;
    }
}
