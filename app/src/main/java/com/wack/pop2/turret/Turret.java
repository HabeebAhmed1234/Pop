package com.wack.pop2.turret;

import android.util.Log;

import org.andengine.entity.sprite.Sprite;

/**
 * Contains the components of the Turret. Can be used to set the position and rotation of the
 * turret.
 * Each turret contains a state machine
 */
public class Turret {

    private TurretStateMachine stateMachine;
    private Sprite turretBodySprite;
    private Sprite turretCannonSprite;

    Turret(Sprite turretBodySprite,
           Sprite turretCannonSprite) {
        stateMachine = new TurretStateMachine();
        this.turretBodySprite = turretBodySprite;
        this.turretCannonSprite = turretCannonSprite;
    }

    public void setCannonAngle(float angle) {
        turretCannonSprite.setRotation(angle);
    }

    public float getCannonAngle() {
        return turretCannonSprite.getRotation();
    }

    public void setAimAt(float x, float y) {
        float turretCenterX = turretBodySprite.getX() + turretBodySprite.getWidth() / 2;
        float turretCenterY = turretBodySprite.getY() + turretBodySprite.getHeight() / 2;
        float angle = getAngle(turretCenterX, turretCenterY, x, y);
        setCannonAngle(angle);
    }


    private static float getAngle(float x1, float y1, float x2, float y2) {
        double theta = Math.atan2(y2 - y1, x2 - x1);
        double angle = Math.toDegrees(theta);
        if (angle < 0) {
            angle += 360;
        }

        return (float) angle;
    }
}
