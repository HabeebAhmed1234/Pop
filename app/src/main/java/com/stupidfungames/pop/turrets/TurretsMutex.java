package com.stupidfungames.pop.turrets;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.BinderEnity;

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
