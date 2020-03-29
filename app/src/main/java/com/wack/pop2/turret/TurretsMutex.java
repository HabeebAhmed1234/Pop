package com.wack.pop2.turret;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.binder.Binder;
import com.wack.pop2.binder.BinderEnity;

public class TurretsMutex extends BaseEntity {

    private boolean isDragging;

    public TurretsMutex(BinderEnity parent) {
        super(parent);
    }

    public synchronized boolean isDragging() {
        return isDragging;
    }

    public synchronized void setIsDragging(boolean isDragging) {
        this.isDragging = isDragging;
    }

}
