package com.wack.pop2.tray;

import com.wack.pop2.resources.textures.GameTexturesManager;

import org.andengine.entity.primitive.Rectangle;

public interface HostTrayCallback {

    void openTray();
    void closeTray();

    int[] getAnchorPx();
    int[] getOpenPosition();
    int[] getClosedPosition();

    TrayStateMachine getStateMachine();
    GameTexturesManager getTextureManager();
    Rectangle getTrayIconsHolderRectangle();
    void onIconsTrayInitialized();
    void onIconsTrayDimensionsChanged();
}
