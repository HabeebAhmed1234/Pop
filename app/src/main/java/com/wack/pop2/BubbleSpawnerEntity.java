package com.wack.pop2;

import android.hardware.SensorManager;

import com.wack.pop2.fixturedefdata.BubbleFixtureDefData;
import com.wack.pop2.physics.PhysicsConnector;
import com.wack.pop2.physics.PhysicsFactory;
import com.wack.pop2.physics.util.Vec2Pool;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.resources.textures.TextureId;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

public class BubbleSpawnerEntity extends BaseEntity {

    private static final float BUBBLE_SPAWN_INTERVAL = 5f;

    private GameTexturesManager texturesManager;

    private int bubbleSpawnDifficultyMultiplier = 1;

    public BubbleSpawnerEntity(GameTexturesManager texturesManager, GameResources gameResources) {
        super(gameResources);
        this.texturesManager = texturesManager;
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
                            spawnBubbles(bubbleSpawnDifficultyMultiplier);
                        }
                    }));
    }

    private void spawnBubbles(int bubbleQuantity) {
        int screenWidth = ScreenUtils.getSreenSize().width;
        for (int i = 0; i < bubbleQuantity; i++)
        {
            spawnBubble(i, (int)(Math.random() * screenWidth),-200 * (i+1));
            setBubbleInitialVelocity((Sprite) scene.getLastChild(), 2f);
        }
    }

    private void spawnBubble(final int bubbleNumber, final float pX, final float pY) {
        final Sprite bubbleSprite;

        final Body body;
        final FixtureDef bubbleFixtureDef = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);

        //add object
        bubbleSprite = new Sprite(
                pX,
                pY,
                getNextBubbleTextureAndSetFixtureDefData(bubbleFixtureDef, bubbleNumber),
                vertexBufferObjectManager);
        bubbleSprite.setScale((float) (Math.random()*2+1));
        body = PhysicsFactory.createCircleBody(physicsWorld, bubbleSprite, BodyType.DYNAMIC, bubbleFixtureDef);
        physicsWorld.registerPhysicsConnector(new PhysicsConnector(bubbleSprite, body, true, true));
        bubbleSprite.setUserData(body);
        scene.registerTouchArea(bubbleSprite);
        //faces.addLast(face);
        scene.attachChild(bubbleSprite);
    }


    private void setBubbleInitialVelocity(final Sprite face, float speed) {
        final Body faceBody = (Body)face.getUserData();
        final Vec2 velocity = Vec2Pool.obtain(0f, (float) (SensorManager.GRAVITY_EARTH *0.3*speed));
        faceBody.setLinearVelocity(velocity);
        Vec2Pool.recycle(velocity);
    }

    private ITextureRegion getNextBubbleTextureAndSetFixtureDefData(FixtureDef fixtureDef, int bubbleNumber) {
        int bubbleIndex = bubbleNumber % 4;

        switch(bubbleIndex) {
            case 0:
                fixtureDef.setUserData(new BubbleFixtureDefData(true));
                return texturesManager.getTextureRegion(TextureId.BALL_1);
            case 1:
                fixtureDef.setUserData(new BubbleFixtureDefData(true));
                return texturesManager.getTextureRegion(TextureId.BALL_2);
            case 2:
                fixtureDef.setUserData(new BubbleFixtureDefData(true));
                return texturesManager.getTextureRegion(TextureId.BALL_3);
            case 3:
                fixtureDef.setUserData(new BubbleFixtureDefData(false));
                return texturesManager.getTextureRegion(TextureId.SKULL_BALL);

        }
        throw new IllegalStateException("there is no bubble texture for bubbleIndex = " + bubbleIndex);
    }
}
