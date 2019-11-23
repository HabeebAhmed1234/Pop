package com.wack.pop2.ballandchain;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.BubblePopperEntity;
import com.wack.pop2.GamePhysicsContactsEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.physics.util.Vec2Pool;
import com.wack.pop2.physics.util.constants.PhysicsConstants;
import com.wack.pop2.resources.textures.GameTexturesManager;

import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.joints.MouseJoint;

/**
 * Manages the ball and chain tool that
 * gcan be used to pop bubbles by the user swinging around a
 * spike ball on a chain.
 */
public class BallAndChainManagerEntity extends BaseEntity implements IOnSceneTouchListener {

    private BallAndChainCreatorEntity ballAndChainCreatorEntity;
    private BallAndChainCollisionManagerEntity ballAndChainCollisionManagerEntity;

    private MouseJoint mouseJoint;

    public BallAndChainManagerEntity(
            GameTexturesManager texturesManager,
            GamePhysicsContactsEntity gamePhysicsContactsEntity,
            BubblePopperEntity bubblePopperEntity,
            GameResources gameResources) {
        super(gameResources);
        this.ballAndChainCreatorEntity = new BallAndChainCreatorEntity(texturesManager, gameResources);
        this.ballAndChainCollisionManagerEntity = new BallAndChainCollisionManagerEntity(bubblePopperEntity, gamePhysicsContactsEntity, gameResources);
    }

    @Override
    public void onCreateScene() {
        mouseJoint = ballAndChainCreatorEntity.createBallAndChain();
        scene.setOnSceneTouchListener(this);
    }


    @Override
    public boolean onSceneTouchEvent(Scene pScene, TouchEvent touchEvent) {
        switch(touchEvent.getAction()) {
            case TouchEvent.ACTION_UP:
                return false;
            case TouchEvent.ACTION_DOWN:
            case TouchEvent.ACTION_MOVE:
                setTarget(touchEvent);
                return true;
        }
        return false;
    }

    private void setTarget(TouchEvent touchEvent) {
        if (mouseJoint == null) return;
        final Vec2 vec = Vec2Pool.obtain(touchEvent.getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, touchEvent.getY() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
        mouseJoint.setTarget(vec);
    }
}
