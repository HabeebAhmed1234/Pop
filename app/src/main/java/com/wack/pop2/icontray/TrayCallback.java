package com.wack.pop2.icontray;

interface TrayCallback {

    void openTray();
    void closeTray();

    int[] getOpenPositionPx();
    int[] getClosedPositionPx();
}
