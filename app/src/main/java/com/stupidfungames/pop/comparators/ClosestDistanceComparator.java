package com.stupidfungames.pop.comparators;

import org.andengine.entity.IEntity;

import java.util.Comparator;

import static com.stupidfungames.pop.utils.GeometryUtils.distanceBetween;

/**
 * Sorts IEntities in order of their increasing distance from the passed in IEntity
 */
public class ClosestDistanceComparator implements Comparator<IEntity> {

    private IEntity centerEntity;

    public ClosestDistanceComparator(IEntity centerEntity) {
        this.centerEntity = centerEntity;
    }

    @Override
    public int compare(IEntity e1, IEntity e2) {
        float e1Distance = distanceBetween(centerEntity, e1);
        float e2Distance = distanceBetween(centerEntity, e2);
        return e1Distance == e2Distance ? 0 : e1Distance < e2Distance ? -1 : 1;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IEntity)) {
            return false;
        }
        return ((IEntity) obj).equals(this);
    }
}