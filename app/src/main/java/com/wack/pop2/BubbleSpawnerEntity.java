package com.wack.pop2;

import android.hardware.SensorManager;

import com.wack.pop2.eventbus.DifficultyChangedEventPayload;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.GameEvent;
import com.wack.pop2.fixturedefdata.BaseEntityUserData;
import com.wack.pop2.fixturedefdata.BubbleEntityUserData;
import com.wack.pop2.physics.PhysicsConnector;
import com.wack.pop2.physics.PhysicsFactory;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.resources.textures.TextureId;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BubbleSpawnerEntity extends BaseEntity implements EventBus.Subscriber {

    public enum BubbleType {
        RED,
        GREEN,
        BLUE,
        SKULL;

        private static final List<BubbleType> VALUES =
                Collections.unmodifiableList(Arrays.asList(values()));
        private static final int SIZE = VALUES.size();
        private static final Random RANDOM = new Random();

        public static BubbleType random()  {
            return VALUES.get(RANDOM.nextInt(SIZE));
        }
    }

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
        for (int i = 0; i < bubbleQuantity; i++) {
            PhysicsConnector physicsConnector = spawnBubble(BubbleType.random(), (int)(Math.random() * screenWidth),-200 * ( i + 1 ), (float) (Math.random()*2+1));
            BubblePhysicsUtil.applyVelocity(physicsConnector, 0f, (float) (SensorManager.GRAVITY_EARTH * 0.3 * 2));
        }
    }

    /**
     * Creates a bubble in the scene and returns the corresponding physics connector.
     */
    public PhysicsConnector spawnBubble(final BubbleType bubbleType, final float x, final float y, float scale) {
        final BaseEntityUserData userData = getBubbleUserData(bubbleType);
        //add object
        final Sprite bubbleSprite = new Sprite(
                x,
                y,
                getBubbleTexture(bubbleType),
                vertexBufferObjectManager);
        bubbleSprite.setUserData(userData);
        bubbleSprite.setScale(scale);

        final FixtureDef bubbleFixtureDef = GameFixtureDefs.BASE_BUBBLE_FIXTURE_DEF;
        bubbleFixtureDef.setUserData(userData);
        final Body body = PhysicsFactory.createCircleBody(physicsWorld, bubbleSprite, BodyType.DYNAMIC, bubbleFixtureDef);
        final PhysicsConnector physicsConnector = new PhysicsConnector(bubbleSprite, body, true, true);
        physicsWorld.registerPhysicsConnector(physicsConnector);
        scene.registerTouchArea(bubbleSprite);
        addToScene(bubbleSprite);
        return physicsConnector;
    }

    private ITextureRegion getBubbleTexture(BubbleType bubbleType) {
        switch(bubbleType) {
            case RED:
                return texturesManager.getTextureRegion(TextureId.RED_BUBBLE);
            case GREEN:
                return texturesManager.getTextureRegion(TextureId.GREEN_BUBBLE);
            case BLUE:
                return texturesManager.getTextureRegion(TextureId.BLUE_BUBBLE);
            case SKULL:
                return texturesManager.getTextureRegion(TextureId.SKULL_BALL);

        }
        throw new IllegalStateException("there is no bubble texture for bubbleType = " + bubbleType);
    }

    private BaseEntityUserData getBubbleUserData(BubbleType bubbleType) {
        switch(bubbleType) {
            case RED:
            case GREEN:
            case BLUE:
                return new BubbleEntityUserData(true, false, bubbleType);
            case SKULL:
                return new BubbleEntityUserData(false, true, bubbleType);
        }
        throw new IllegalStateException("there is no bubble user data for bubbleType = " + bubbleType);
    }

    @Override
    public void onEvent(GameEvent event, EventPayload payload) {
        if (event == GameEvent.DIFFICULTY_CHANGE) {
            DifficultyChangedEventPayload difficultyChangedEventPayload = (DifficultyChangedEventPayload) payload;
            bubbleSpawnMultiplier = difficultyChangedEventPayload.newDifficulty;
        }
    }
}
