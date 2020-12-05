package com.stupidfungames.pop.tray;

import android.content.Context;
import android.util.Pair;

import androidx.annotation.Nullable;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.GameFixtureDefs;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.collision.CollisionFilters;
import com.stupidfungames.pop.physics.PhysicsFactory;
import com.stupidfungames.pop.tray.TrayStateMachine.State;
import com.stupidfungames.pop.utils.ScreenUtils;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.color.AndengineColor;

import java.util.ArrayList;
import java.util.List;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

public abstract class TrayIconsHolderBaseEntity<IconIdType> extends BaseEntity {

    public static class Spec {

        public final LayoutOrientation layoutOrientation;
        public final int paddingHorizontalPx;
        public final int paddingVerticalPx;
        public final int paddingBetweenIconsPx;
        public final int iconsSizePx;

        public Spec(Context context,
                        LayoutOrientation layoutOrientation,
                        int paddingHorizontalDp,
                        int paddingVerticalDp,
                        int paddingBetweenIconsDp,
                        int iconsSizeDp) {
            this.layoutOrientation = layoutOrientation;
            paddingHorizontalPx = ScreenUtils.dpToPx(paddingHorizontalDp, context);
            paddingVerticalPx = ScreenUtils.dpToPx(paddingVerticalDp, context);
            paddingBetweenIconsPx = ScreenUtils.dpToPx(paddingBetweenIconsDp, context);
            iconsSizePx = ScreenUtils.dpToPx(iconsSizeDp, context);
        }
    }

    public enum LayoutOrientation {
        HORIZONTAL, // Icons are arranged from left to right
        VERTICAL // Icons are arranged from top to bottom
    }

    private final HostTrayCallback hostTrayCallback;
    private Spec spec;
    private Rectangle iconsTray;
    private final List<Pair<IconIdType, Sprite>> icons = new ArrayList<>();

    public TrayIconsHolderBaseEntity(
            HostTrayCallback hostTrayCallback,
            BinderEnity parent) {
        super(parent);
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

    public int[] getOpenPosition() {
        int[] anchor = hostTrayCallback.getAnchorPx();
        return new int[]{ anchor[0] - getTrayWidthPx(), anchor[1] - getTrayHeightPx() / 2 };
    }

    public int[] getClosedPosition() {
        int[] anchor = hostTrayCallback.getAnchorPx();
        return new int[]{ anchor[0], anchor[1] - getTrayHeightPx() / 2 };
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
            int[] position = null;
            if (currentState == TrayStateMachine.State.CLOSED || currentState == TrayStateMachine.State.EMPTY) {
                position = getClosedPosition();
            } else if (currentState == State.EXPANDED) {
                position = getOpenPosition();
            }
            if (position != null) {
                iconsTray.setX(position[0]);
                iconsTray.setY(position[1]);
            }
        }
    }

    /**
     * Calculates the size of the tray given the icons in it and sets the dimensiosn
     */
    private void refreshTrayDimensions() {
        iconsTray.setWidth(getTrayWidthPx());
        iconsTray.setHeight(getTrayHeightPx());
        updatePhysics();
    }

    private void updatePhysics() {
        final FixtureDef iconFixtureDef = GameFixtureDefs.ICON_BOX_FIXTURE_DEF;
        iconFixtureDef.setFilter(CollisionFilters.ICON_FILTER);

        final Body body = PhysicsFactory
            .createBoxBody(physicsWorld, iconsTray,  BodyType.STATIC, iconFixtureDef);

        if (hasPhysics(iconsTray)) {
            removePhysics(iconsTray);
        }
        linkReversePhysics(iconsTray, body);
    }

    private void refreshIconPostitions() {
        for (int i = 0 ; i < icons.size() ; i++) {
            Pair<IconIdType, Sprite> iconPair = icons.get(i);
            Sprite icon = iconPair.second;

            // Set the position of this icon
            if (getSpecInternal().layoutOrientation == LayoutOrientation.VERTICAL) {
                icon.setX(getPaddingHorizontalPx());
                icon.setY(
                        getPaddingVerticalPx()
                                + (i * getIconSizePx())
                                + (i * getPaddingBetweenPx()));
            } else if (getSpecInternal().layoutOrientation == LayoutOrientation.HORIZONTAL) {
                icon.setX(getPaddingHorizontalPx()
                        + (i * getIconSizePx())
                        + (i * getPaddingBetweenPx()));
                icon.setY(getPaddingVerticalPx());
            }
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
        switch (getSpecInternal().layoutOrientation) {
            case HORIZONTAL:
                return getPaddingHorizontalPx() * 2
                        + getIconSizePx() * icons.size()
                        + getPaddingBetweenPx() * (icons.size() -1);
            case VERTICAL:
                return getPaddingHorizontalPx() * 2 + getIconSizePx();
        }
        throw new IllegalArgumentException("No width for layout orientation " + getSpecInternal().layoutOrientation);
    }

    public int getTrayHeightPx() {
        switch (getSpecInternal().layoutOrientation) {
            case HORIZONTAL:
                return getPaddingHorizontalPx() * 2 + getIconSizePx();
            case VERTICAL:
                return getPaddingVerticalPx() * 2
                        + getIconSizePx() * icons.size()
                        + getPaddingBetweenPx() * (icons.size() -1);
        }
        throw new IllegalArgumentException("No height for layout orientation " + getSpecInternal().layoutOrientation);
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
