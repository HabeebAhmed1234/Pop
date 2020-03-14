package com.wack.pop2.tray;

import com.wack.pop2.GameAreaTouchListenerEntity;
import com.wack.pop2.resources.textures.GameTexturesManager;

public interface HostTrayCallback {

    void openTray();
    void closeTray();

    int[] getOpenPositionPx();
    int[] getClosedPositionPx();

    GameAreaTouchListenerEntity getAreaTouchListener();
    TrayStateMachine getStateMachine();
    GameTexturesManager getTextureManager();
}
