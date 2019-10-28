package com.wack.pop2;

import android.hardware.SensorManager;

import com.wack.pop2.eventbus.DifficultyChangedEventPayload;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.GameEvent;
import com.wack.pop2.fixturedefdata.BaseEntityUserData;
import com.wack.pop2.fixturedefdata.BubbleEntityUserData;
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

public class BubbleSpawnerEntity extends BaseEntity implements EventBus.Subscriber {

    private static final float BUBBLE_SPAWN_INTERVAL = 5f;

    private GameTexturesManager texturesManager;

    private int bubbleSpawnMultiplier = 1;

    public BubbleSpawnerEntity(GameTexturesManager texturesManager, GameResources gameResources) {
        super(gameResources);
        this.texturesManager = texturesManager;
    }

    @Override
    public void onCreateScene() {
        EventBus.get().subscribe(GameEvent.DIFFICULTY_CHANGE, this);
        engine.registerUpdateHandler(
                new TimerHandler(
                        BUBBLE_SPAWN_INTERVAL,
                    true,
                    new ITimerCallback() {
                        @Override
                        public void onTimePassed(TimerHandler pTimerHandler) {
                            spawnBubbles(bubbleSpawnMultiplier);
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
        int bubbleIndex = bubbleNumber % 4;
        final BaseEntityUserData userData = getBubbleUserData(bubbleIndex);
        //add object
        final Sprite bubbleSprite = new Sprite(
                pX,
                pY,
                getNextBubbleTextureAndSetFixtureDefData(bubbleIndex),
                vertexBufferObjectManager);
        bubbleSprite.setUserData(userData);
        bubbleSprite.setScale((float) (Math.random()*2+1));

        final FixtureDef bubbleFixtureDef = GameFixtureDefs.BASE_BUBBLE_FIXTURE_DEF;
        bubbleFixtureDef.setUserData(userData);
        final Body body = PhysicsFactory.createCircleBody(physicsWorld, bubbleSprite, BodyType.DYNAMIC, bubbleFixtureDef);
        physicsWorld.registerPhysicsConnector(new PhysicsConnector(bubbleSprite, body, true, true));
        scene.registerTouchArea(bubbleSprite);
        addToScene(bubbleSprite);
    }


    private void setBubbleInitialVelocity(final Sprite face, float speed) {
        final Body faceBody = (Body)face.getUserData();
        final Vec2 velocity = Vec2Pool.obtain(0f, (float) (SensorManager.GRAVITY_EARTH *0.3*speed));
        faceBody.setLinearVelocity(velocity);
        Vec2Pool.recycle(velocity);
    }

    private ITextureRegion getNextBubbleTextureAndSetFixtureDefData(int bubbleIndex) {
        switch(bubbleIndex) {
            case 0:
                return texturesManager.getTextureRegion(TextureId.BALL_1);
            case 1:
                return texturesManager.getTextureRegion(TextureId.BALL_2);
            case 2:
                return texturesManager.getTextureRegion(TextureId.BALL_3);
            case 3:
                return texturesManager.getTextureRegion(TextureId.SKULL_BALL);

        }
        throw new IllegalStateException("there is no bubble texture for bubbleIndex = " + bubbleIndex);
    }

    private BaseEntityUserData getBubbleUserData(int bubbleIndex) {
        switch(bubbleIndex) {
            case 0:
            case 1:
            case 2:
                return new BubbleEntityUserData(true, false);
            case 3:
                return new BubbleEntityUserData(false, true);
        }
        throw new IllegalStateException("there is no bubble user data for bubbleIndex = " + bubbleIndex);
    }

    @Override
    public void onEvent(GameEvent event, EventPayload payload) {
        if (event == GameEvent.DIFFICULTY_CHANGE) {
            DifficultyChangedEventPayload difficultyChangedEventPayload = (DifficultyChangedEventPayload) payload;
            bubbleSpawnMultiplier = difficultyChangedEventPayload.newDifficulty;
        }
    }
}
