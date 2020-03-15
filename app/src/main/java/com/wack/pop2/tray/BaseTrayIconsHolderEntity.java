package com.wack.pop2.tray;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;

/**
 * Holds the icons that are part of the tray. In charge of sizing, layout, orientation ect.
 */
public class BaseTrayIconsHolderEntity extends BaseEntity {

    private HostTrayCallback hostTrayCallback;

    public BaseTrayIconsHolderEntity(HostTrayCallback hostTrayCallback, GameResources gameResources) {
        super(gameResources);
        this.hostTrayCallback = hostTrayCallback;
    }
}
