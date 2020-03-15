package com.wack.pop2.tray;

import com.wack.pop2.GameAreaTouchListenerEntity;
import com.wack.pop2.resources.textures.GameTexturesManager;

import org.andengine.entity.primitive.Rectangle;

public interface HostTrayCallback {

    void openTray();
    void closeTray();

    int[] getAnchorPx();

    GameAreaTouchListenerEntity getAreaTouchListener();
    TrayStateMachine getStateMachine();
    GameTexturesManager getTextureManager();
    Rectangle getTrayIconsHolderRectangle();
}
