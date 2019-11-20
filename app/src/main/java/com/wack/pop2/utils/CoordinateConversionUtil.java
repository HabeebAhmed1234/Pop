package com.wack.pop2.utils;

import com.wack.pop2.physics.util.constants.PhysicsConstants;

import org.jbox2d.common.Vec2;

public class CoordinateConversionUtil {

    public static Vec2 physicsWorldToScene(Vec2 vec2) {
        return new Vec2(vec2.x * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, vec2.y * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
    }

    public static Vec2 sceneToPhysicsWorld(Vec2 vec2) {
        return new Vec2(vec2.x / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, vec2.y / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
    }
}
