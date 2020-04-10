package com.stupidfungames.pop.utils;

import com.stupidfungames.pop.physics.util.constants.PhysicsConstants;

import org.jbox2d.common.Vec2;

public class CoordinateConversionUtil {

    public static Vec2 physicsWorldToScene(Vec2 vec2) {
        return new Vec2(physicsWorldToScene(vec2.x), physicsWorldToScene(vec2.y));
    }

    public static Vec2 sceneToPhysicsWorld(Vec2 vec2) {
        return new Vec2(sceneToPhysicsWorld(vec2.x), sceneToPhysicsWorld(vec2.y));
    }

    public static float physicsWorldToScene(float length) {
        return length * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
    }

    public static float sceneToPhysicsWorld(float length) {
        return length / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
    }
}
