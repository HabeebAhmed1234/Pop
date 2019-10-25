package com.wack.pop2;

import com.wack.pop2.physics.PhysicsConnector;
import com.wack.pop2.physics.PhysicsFactory;
import com.wack.pop2.physics.util.Vec2Pool;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.sprite.Sprite;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

public class BubbleSpawnerEntity extends BaseEntity {

    private static final float BUBBLE_SPAWN_INTERVAL = 5f;

    private int bubbleSpawnDifficultyMultiplier = 1;

    public BubbleSpawnerEntity(GameResources gameResources) {
        super(gameResources);
    }

    @Override
    public void onCreateScene() {
        engine.registerUpdateHandler(
                new TimerHandler(
                        BUBBLE_SPAWN_INTERVAL,
                    true,
                    new ITimerCallback() {
                        @Override
                        public void onTimePassed(TimerHandler pTimerHandler) {
                            spawnBubble(bubbleSpawnDifficultyMultiplier);
                        }
                    }));
    }

    private void spawnBubble(int bubbleQuantity) {
        int screenWidth = ScreenUtils.getSreenSize().width;
        for (int x=0; x<bubbleQuantity; x++)
        {
            addFace((int)(Math.random() * screenWidth),-200 * (x+1));
            spawnFaceInitialDirection((Sprite) scene.getLastChild(), 2f);
        }
    }

    private void addFace(final float pX, final float pY) {
        final Sprite face;

        final Body body;
        final FixtureDef objectFixtureDef = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);

        //add object
        face = new Sprite(pX, pY, whichface(), vertexBufferObjectManager);
        face.setScale((float) (Math.random()*2+1));
        body = PhysicsFactory.createCircleBody(physicsWorld, face, BodyType.DYNAMIC, objectFixtureDef);
        physicsWorld.registerPhysicsConnector(new PhysicsConnector(face, body, true, true));
        face.setUserData(body);
        scene.registerTouchArea(face);
        faces.addLast(face);
        scene.attachChild(face);
    }


    private void spawnFaceInitialDirection(final Sprite face,float speed) {
        final Body faceBody = (Body)face.getUserData();
        final Vec2 velocity = Vec2Pool.obtain((float) (this.mGravityX*(Math.random()*2-1)), (float) (this.mGravityY *0.3*speed));
        faceBody.setLinearVelocity(velocity);
        Vec2Pool.recycle(velocity);
    }
}
