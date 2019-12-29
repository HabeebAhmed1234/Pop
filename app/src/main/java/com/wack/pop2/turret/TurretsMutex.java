package com.wack.pop2.turret;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;

public class TurretsMutex extends BaseEntity {

    private boolean isDragging;

    public TurretsMutex(GameResources gameResources) {
        super(gameResources);
    }

    public synchronized boolean isDragging() {
        return isDragging;
    }

    public synchronized void setIsDragging(boolean isDragging) {
        this.isDragging = isDragging;
    }
}
