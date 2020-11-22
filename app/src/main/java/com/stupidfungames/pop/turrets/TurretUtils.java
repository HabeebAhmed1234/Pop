package com.stupidfungames.pop.turrets;

import com.stupidfungames.pop.entitymatchers.BubblesEntityMatcher;
import com.stupidfungames.pop.comparators.ClosestDistanceComparator;

import org.andengine.entity.IEntity;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;

import java.util.Collections;
import java.util.List;

public class TurretUtils {

    public static Sprite getClosestPoppableBubble(Scene scene, IEntity center) {
        List<IEntity> allBubbles = TurretUtils.getAllUntargetedPoppableBubbles(scene);
        if (allBubbles == null || allBubbles.isEmpty()) {
            return null;
        }
        Collections.sort(allBubbles, new ClosestDistanceComparator(center));
        return (Sprite) allBubbles.get(0);
    }

    /**
     * Returns all the bubbles present in the scene
     */
    public static List<IEntity> getAllUntargetedPoppableBubbles(Scene scene) {
        return scene.query(new BubblesEntityMatcher(true, true));
    }
}
