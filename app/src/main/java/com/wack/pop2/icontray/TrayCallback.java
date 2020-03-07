package com.wack.pop2.icontray;

public interface TrayCallback {

    void openTray();
    void closeTray();

    int[] getOpenPositionPx();
    int[] getClosedPositionPx();
}
