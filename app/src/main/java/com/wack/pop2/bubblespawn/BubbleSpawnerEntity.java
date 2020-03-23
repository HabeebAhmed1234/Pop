package com.wack.pop2.bubblespawn;

import android.hardware.SensorManager;
import android.util.Log;
import android.util.Pair;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameFixtureDefs;
import com.wack.pop2.GameResources;
import com.wack.pop2.TouchPopperFactoryEntity;
import com.wack.pop2.collision.CollisionFilters;
import com.wack.pop2.entitymatchers.BubblesEntityMatcher;
import com.wack.pop2.eventbus.BubbleSpawnedEventPayload;
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
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.color.AndengineColor;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.wack.pop2.GameConstants.MAX_BUBBLES_ON_SCREEN;
import static com.wack.pop2.GameConstants.MAX_BUBBLES_PER_SPAWN;

public class BubbleSpawnerEntity extends BaseEntity implements EventBus.Subscriber {

    private static final float BUBBLE_GRAVITY_SCALE = 0.5f;

    public enum BubbleType {
        RED,
        GREEN,
        BLUE;

        private static final List<BubbleType> VALUES =
                Collections.unmodifiableList(Arrays.asList(values()));
        private static final int SIZE = VALUES.size();
        private static final Random RANDOM = new Random();

        public static BubbleType random()  {
            return VALUES.get(RANDOM.nextInt(SIZE));
        }
    }

    public enum BubbleSize {
        LARGE(160),
        MEDIUM(120),
        SMALL(80);

        public final float sizeDp;

        BubbleSize(final float dp) {
            this.sizeDp = dp;
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

    private TouchPopperFactoryEntity touchPopperFactory;
    private GameTexturesManager texturesManager;
    private float bubbleSpawnInterval = 5;
    private TimerHandler bubbleSpawnTimerHandler = new TimerHandler(
            bubbleSpawnInterval,
            false,
            new ITimerCallback() {
                @Override
                public void onTimePassed(TimerHandler pTimerHandler) {
                    if (!isBubbleLimitReached()) {
                        int numBubbles = (int) (Math.random() * MAX_BUBBLES_PER_SPAWN);
                        List<Pair<Float, Float>> startingBubblePositions = BubblePacker.getSpawnBubblesLocations(
                                numBubbles,
                                ScreenUtils.dpToPx(BubbleSize.LARGE.sizeDp, hostActivity.getActivityContext()));
                        for (Pair<Float, Float> position : startingBubblePositions) {
                            spawnStartingBubble(position.first, position.second);
                        }
                    }
                    engine.registerUpdateHandler(new TimerHandler(bubbleSpawnInterval, false, this));
                }
            });

    public BubbleSpawnerEntity(
            TouchPopperFactoryEntity touchPopperFactory,
            GameTexturesManager texturesManager,
            GameResources gameResources) {
        super(gameResources);
        this.touchPopperFactory = touchPopperFactory;
        this.texturesManager = texturesManager;
    }

    @Override
    public void onCreateScene() {
        EventBus.get().subscribe(GameEvent.SPAWN_INTERVAL_CHANGED, this, true);
        engine.registerUpdateHandler(bubbleSpawnTimerHandler);
    }

    @Override
    public void onDestroy() {
        engine.unregisterUpdateHandler(bubbleSpawnTimerHandler);
        EventBus.get().unSubscribe(GameEvent.SPAWN_INTERVAL_CHANGED, this);
    }

    private boolean isBubbleLimitReached() {
        final int numBubbles = scene.query(new BubblesEntityMatcher(false, true)).size();
        Log.d("BubbleSpawnerEntity", "Num bubbles = " + numBubbles);
        return numBubbles > MAX_BUBBLES_ON_SCREEN;
    }

    private void spawnStartingBubble(float x, float y) {
        BubbleType bubbleType = BubbleType.random();
        Body body = spawnBubble(bubbleType, x,y, BubbleSize.LARGE);
        EventBus.get().sendEvent(GameEvent.STARTING_BUBBLE_SPAWNED, new StartingBubbleSpawnedEventPayload(bubbleType));
        BubblePhysicsUtil.applyVelocity(body, 0f, (float) (SensorManager.GRAVITY_EARTH * 0.3 * 2));
    }

    /**
     * Creates a bubble in the scene and returns the corresponding physics connector.
     *
     * returns the Body of the spawned bubble
     */
    public Body spawnBubble(final BubbleType bubbleType, final float x, final float y, BubbleSize bubbleSize) {
        //add object
        final Sprite bubbleSprite = new Sprite(
                x,
                y,
                getBubbleTexture(bubbleType),
                vertexBufferObjectManager);
        colorBubble(bubbleType, bubbleSprite);
        final BaseEntityUserData userData = getBubbleUserData(bubbleSprite, bubbleType, bubbleSize);
        bubbleSprite.setUserData(userData);
        float bubbleSizePx = ScreenUtils.dpToPx(bubbleSize.sizeDp, hostActivity.getActivityContext());
        bubbleSprite.setWidth(bubbleSizePx);
        bubbleSprite.setHeight(bubbleSizePx);

        // If the bubble's right or left edge will clip outside of the screen horizontal bounds
        // We need to adjust the bubble position
        clipBubblePosition(bubbleSprite);

        final FixtureDef bubbleFixtureDef = GameFixtureDefs.BASE_BUBBLE_FIXTURE_DEF;
        bubbleFixtureDef.setFilter(CollisionFilters.BUBBLE_FILTER);
        bubbleFixtureDef.setUserData(userData);
        final Body body = PhysicsFactory.createCircleBody(physicsWorld, bubbleSprite, BodyType.DYNAMIC, bubbleFixtureDef);
        body.setGravityScale(BUBBLE_GRAVITY_SCALE);
        addToSceneWithTouch(bubbleSprite, body, touchPopperFactory.getNewTouchBubblePopper());
        notifyBubbleSpawned(bubbleSprite);
        return body;
    }

    private void notifyBubbleSpawned(Sprite bubbleSprite) {
        EventBus.get().sendEvent(GameEvent.BUBBLE_SPAWNED, new BubbleSpawnedEventPayload(bubbleSprite));
    }

    private void clipBubblePosition(Sprite bubbleSprite) {
        if (bubbleSprite.getX() < 0) {
            bubbleSprite.setX(0);
        }
        float bubbleWidth = bubbleSprite.getWidthScaled();
        if (bubbleSprite.getX() + bubbleWidth > ScreenUtils.getSreenSize().widthPx) {
            bubbleSprite.setX(ScreenUtils.getSreenSize().widthPx - bubbleWidth);
        }
    }

    private ITextureRegion getBubbleTexture(BubbleType bubbleType) {
        switch(bubbleType) {
            case RED:
            case GREEN:
            case BLUE:
                return texturesManager.getTextureRegion(TextureId.BALL);
            default:
                throw new IllegalStateException("there is no bubble texture for bubbleType = " + bubbleType);

        }
    }

    private void colorBubble(BubbleType type, Sprite bubble) {
        AndengineColor color = AndengineColor.WHITE;
        switch(type) {
            case RED:
                color = AndengineColor.RED;
                break;
            case GREEN:
                color = AndengineColor.GREEN;
                break;
            case BLUE:
                color = AndengineColor.BLUE;
                break;
            default:
                throw new IllegalStateException("there is no bubble color for bubbleType = " + type);
        }
        if (color != null) {
            bubble.setColor(color);
        }
    }

    private BaseEntityUserData getBubbleUserData(Sprite bubbleSprite, BubbleType bubbleType, BubbleSize bubbleSize) {
        switch(bubbleType) {
            case RED:
            case GREEN:
            case BLUE:
                return new BubbleEntityUserData(true, bubbleSize, bubbleType, bubbleSprite);
        }
        throw new IllegalStateException("there is no bubble user data for bubbleType = " + bubbleType);
    }

    @Override
    public void onEvent(GameEvent event, EventPayload payload) {
        if (event == GameEvent.SPAWN_INTERVAL_CHANGED) {
            DifficultyChangedEventPayload difficultyChangedEventPayload = (DifficultyChangedEventPayload) payload;
            bubbleSpawnInterval = difficultyChangedEventPayload.newSpawnInterval;
        }
    }
}
