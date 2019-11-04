package com.wack.pop2;

import android.opengl.GLES20;

import com.wack.pop2.eventbus.DecrementScoreEventPayload;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.GameEvent;
import com.wack.pop2.fixturedefdata.BubbleEntityUserData;
import com.wack.pop2.fixturedefdata.FixtureDefDataUtil;
import com.wack.pop2.fixturedefdata.FloorEntityUserData;
import com.wack.pop2.physics.PhysicsFactory;
import com.wack.pop2.resources.fonts.FontId;
import com.wack.pop2.resources.fonts.GameFontsManager;

import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.text.Text;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;

import static com.wack.pop2.GameFixtureDefs.FLOOR_SENSOR_FIXTURE_DEF;

/**
 * This entity detects whether or not any bubbles have fallen below the screen. Thus
 * resulting in points lost.
 */
public class BubbleLossDetectorEntity extends BaseEntity {

    private static final int SCORE_DECREMENT_AMOUNT = 5;

    private final GameFontsManager fontsManager;
    private final GameAnimationManager animationManager;
    private final GamePhysicsContactsEntity physicsContactsEntity;

    private final GamePhysicsContactsEntity.GameContactListener contactListener = new GamePhysicsContactsEntity.GameContactListener() {
        @Override
        public void onBeginContact(Fixture fixture1, Fixture fixture2) {}

        @Override
        public void onEndContact(Fixture fixture1, Fixture fixture2) {
            processBubbleFellBelowScreen(getBubbleFixture(fixture1, fixture2));
        }
    };

    public BubbleLossDetectorEntity(
            GameFontsManager fontsManager,
            GameAnimationManager animationManager,
            GamePhysicsContactsEntity physicsContactsEntity,
            GameResources gameResources) {
        super(gameResources);
        this.fontsManager = fontsManager;
        this.animationManager = animationManager;
        this.physicsContactsEntity = physicsContactsEntity;
    }

    @Override
    public void onCreateScene() {
        final Rectangle floorDetector = new Rectangle(0, levelHeight, levelWidth, 10, vertexBufferObjectManager);
        floorDetector.setAlpha(0);
        FixtureDef floorFixtureDef = FLOOR_SENSOR_FIXTURE_DEF;
        floorFixtureDef.setUserData(new FloorEntityUserData());
        PhysicsFactory.createBoxBody(physicsWorld, floorDetector, BodyType.STATIC, floorFixtureDef);
        physicsContactsEntity.addContactListener(BubbleEntityUserData.class, FloorEntityUserData.class, contactListener);
    }

    @Override
    public void onDestroy() {
        physicsContactsEntity.removeContactListener(BubbleEntityUserData.class, FloorEntityUserData.class, contactListener);
    }

    private Fixture getBubbleFixture(Fixture f1, Fixture f2) {
        if (FixtureDefDataUtil.isBubbleFixtureDefData(f1)) {
            return f1;
        } else if (FixtureDefDataUtil.isBubbleFixtureDefData(f2)) {
            return f2;
        } else {
            throw new IllegalStateException("neither of the fixtures are bubbles!");
        }
    }

    private void processBubbleFellBelowScreen(Fixture bubbleFixture) {
        BubbleEntityUserData data = (BubbleEntityUserData)bubbleFixture.getUserData();
        if (data.isScoreLossBubble) {
            createScoreLossText(
                    getShapeFromBody(bubbleFixture.getBody()).getX(),
                    levelHeight - 50);
            EventBus.get().sendEvent(GameEvent.DECREMENT_SCORE, new DecrementScoreEventPayload(SCORE_DECREMENT_AMOUNT));
        }
        removeFromSceneAndCleanupPhysics(bubbleFixture.getBody());
    }

    private void createScoreLossText(float x, float y) {
        final Text scoreminus5Text = new Text(x, y, fontsManager.getFont(FontId.SCORE_TICKER_FONT), "-5", vertexBufferObjectManager);
        scoreminus5Text.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        scoreminus5Text.setColor(1, 0, 0);
        addToScene(scoreminus5Text);
        animationManager.startModifier(scoreminus5Text, new ParallelEntityModifier(
                new ScaleModifier(1.2f, 0.1f, 1.5f),
                new AlphaModifier(1.5f, 1f, 0f)));
    }
}
