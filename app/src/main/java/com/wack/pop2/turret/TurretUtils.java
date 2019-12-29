package com.wack.pop2.turret;

import com.wack.pop2.BubblesEntityMatcher;
import com.wack.pop2.comparators.ClosestDistanceComparator;

import org.andengine.entity.IEntity;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;

import java.util.Collections;
import java.util.List;

public class TurretUtils {

    public static Sprite getClosestPoppableBubble(Scene scene, IEntity center) {
        List<IEntity> allBubbles = TurretUtils.getAllPoppableBubbles(scene);
        if (allBubbles == null || allBubbles.isEmpty()) {
            return null;
        }
        Collections.sort(allBubbles, new ClosestDistanceComparator(center));
        return (Sprite) allBubbles.get(0);
    }

    /**
     * Returns all the bubbles present in the scene
     */
    public static List<IEntity> getAllPoppableBubbles(Scene scene) {
        return scene.query(new BubblesEntityMatcher(true));
    }
}