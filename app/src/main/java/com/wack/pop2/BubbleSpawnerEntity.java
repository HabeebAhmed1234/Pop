package com.wack.pop2;

import android.hardware.SensorManager;

import com.wack.pop2.eventbus.DifficultyChangedEventPayload;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.EventPayload;
import com.wack.pop2.eventbus.GameEvent;
import com.wack.pop2.eventbus.StartingBubbleSpawnedEventPayload;
import com.wack.pop2.fixturedefdata.BaseEntityUserData;
import com.wack.pop2.fixturedefdata.BubbleEntityUserData;
import com.wack.pop2.physics.PhysicsFactory;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.resources.textures.TextureId;
import com.wack.pop2.utils.BubblePhysicsUtil;
import com.wack.pop2.utils.ScreenUtils;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.shape.IShape;
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

    private static final float BUBBLE_GRAVITY_SCALE = 0.5f;

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

    public enum BubbleSize {
        LARGE(2f),
        MEDIUM(1f),
        SMALL(0.5f);

        public final float scale;

        BubbleSize(final float scale) {
            this.scale = scale;
        }

        public BubbleSize nextPoppedSize() {
            int nextIndex = ordinal() + 1;
            if (nextIndex >= BubbleSize.values().length) {
                return values()[BubbleSize.values().length - 1];
            }
            return values()[nextIndex];
        }

        public boolean isSmallestBubble() {
            return ordinal() >= BubbleSize.values().length - 1;
        }
    }

    private static final float BUBBLE_SPAWN_INTERVAL = 5f;
    private GameTexturesManager texturesManager;
    private int bubbleSpawnMultiplier = 1;
    private TimerHandler bubbleSpawnTimerHandler = new TimerHandler(
            BUBBLE_SPAWN_INTERVAL,
            true,
            new ITimerCallback() {
                @Override
                public void onTimePassed(TimerHandler pTimerHandler) {
                    spawnStartingBubbles(bubbleSpawnMultiplier);
                }
            });

    public BubbleSpawnerEntity(GameTexturesManager texturesManager, GameResources gameResources) {
        super(gameResources);
        this.texturesManager = texturesManager;
    }

    @Override
    public void onCreateScene() {
        EventBus.get().subscribe(GameEvent.DIFFICULTY_CHANGE, this);
        engine.registerUpdateHandler(bubbleSpawnTimerHandler);
    }

    @Override
    public void onDestroy() {
        engine.unregisterUpdateHandler(bubbleSpawnTimerHandler);
        EventBus.get().unSubscribe(GameEvent.DIFFICULTY_CHANGE, this);
    }

    private void spawnStartingBubbles(int bubbleQuantity) {
        int screenWidth = ScreenUtils.getSreenSize().width;
        for (int i = 0; i < bubbleQuantity; i++) {
            BubbleType bubbleType = BubbleType.random();
            Body body = spawnBubble(bubbleType, (int)(Math.random() * screenWidth),-200 * ( i + 1 ), BubbleSize.LARGE);
            BubblePhysicsUtil.applyVelocity(body, 0f, (float) (SensorManager.GRAVITY_EARTH * 0.3 * 2));
            EventBus.get().sendEvent(GameEvent.STARTING_BUBBLE_SPAWNED, new StartingBubbleSpawnedEventPayload(bubbleType));
        }
    }

    /**
     * Creates a bubble in the scene and returns the corresponding physics connector.
     *
     * returns the Body of the spawned bubble
     */
    public Body spawnBubble(final BubbleType bubbleType, final float xScene, final float yScene, BubbleSize bubbleSize) {
        //add object
        final Sprite bubbleSprite = new Sprite(
                xScene,
                yScene,
                getBubbleTexture(bubbleType),
                vertexBufferObjectManager);
        final BaseEntityUserData userData = getBubbleUserData(bubbleSprite, bubbleType, bubbleSize);
        bubbleSprite.setUserData(userData);
        bubbleSprite.setScale(bubbleSize.scale);

        final FixtureDef bubbleFixtureDef = GameFixtureDefs.BASE_BUBBLE_FIXTURE_DEF;
        bubbleFixtureDef.setUserData(userData);
        final Body body = PhysicsFactory.createCircleBody(physicsWorld, bubbleSprite, BodyType.DYNAMIC, bubbleFixtureDef);
        body.setGravityScale(BUBBLE_GRAVITY_SCALE);
        scene.registerTouchArea(bubbleSprite);
        addToScene(bubbleSprite, body);
        return body;
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

    private BaseEntityUserData getBubbleUserData(IShape bubbleSprite, BubbleType bubbleType, BubbleSize bubbleSize) {
        switch(bubbleType) {
            case RED:
            case GREEN:
            case BLUE:
                return new BubbleEntityUserData(true, false, bubbleSize, bubbleType, bubbleSprite);
            case SKULL:
                return new BubbleEntityUserData(false, true, bubbleSize, bubbleType, bubbleSprite);
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