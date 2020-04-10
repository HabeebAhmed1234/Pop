package com.stupidfungames.pop;

import android.opengl.GLES20;

import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.collision.CollisionFilters;
import com.stupidfungames.pop.eventbus.DecrementScoreEventPayload;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.eventbus.GameEvent;
import com.stupidfungames.pop.fixturedefdata.BubbleEntityUserData;
import com.stupidfungames.pop.fixturedefdata.FixtureDefDataUtil;
import com.stupidfungames.pop.fixturedefdata.FloorEntityUserData;
import com.stupidfungames.pop.physics.PhysicsFactory;
import com.stupidfungames.pop.resources.fonts.FontId;
import com.stupidfungames.pop.resources.fonts.GameFontsManager;

import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.text.Text;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;

import static com.stupidfungames.pop.GameFixtureDefs.FLOOR_SENSOR_FIXTURE_DEF;

/**
 * This entity detects whether or not any bubbles have fallen below the screen. Thus
 * resulting in points lost.
 */
public class BubbleLossDetectorBaseEntity extends BaseEntity {

    private static final int SCORE_DECREMENT_AMOUNT = 5;

    private final GamePhysicsContactsEntity.GameContactListener contactListener = new GamePhysicsContactsEntity.GameContactListener() {
        @Override
        public void onBeginContact(Fixture fixture1, Fixture fixture2) {}

        @Override
        public void onEndContact(Fixture fixture1, Fixture fixture2) {
            processBubbleFellBelowScreen(FixtureDefDataUtil.getBubbleFixture(fixture1, fixture2));
        }
    };

    public BubbleLossDetectorBaseEntity(BinderEnity parent) {
        super(parent);
    }

    @Override
    public void onCreateScene() {
        final Rectangle floorDetector = new Rectangle(0, levelHeight, levelWidth, 10, vertexBufferObjectManager);
        floorDetector.setAlpha(0);
        FixtureDef floorFixtureDef = FLOOR_SENSOR_FIXTURE_DEF;
        floorFixtureDef.setUserData(new FloorEntityUserData());
        floorFixtureDef.setFilter(CollisionFilters.WALL_FILTER);
        PhysicsFactory.createBoxBody(physicsWorld, floorDetector, BodyType.STATIC, floorFixtureDef);
        get(GamePhysicsContactsEntity.class).addContactListener(BubbleEntityUserData.class, FloorEntityUserData.class, contactListener);
    }

    @Override
    public void onDestroy() {
        get(GamePhysicsContactsEntity.class).removeContactListener(BubbleEntityUserData.class, FloorEntityUserData.class, contactListener);
    }

    private void processBubbleFellBelowScreen(Fixture bubbleFixture) {
        BubbleEntityUserData data = (BubbleEntityUserData)bubbleFixture.getUserData();
        if (data.isScoreLossBubble) {
            createScoreLossText(
                    data.bubbleSprite.getX(),
                    levelHeight - 50);
            EventBus.get().sendEvent(GameEvent.DECREMENT_SCORE, new DecrementScoreEventPayload(SCORE_DECREMENT_AMOUNT));
        }
        removeFromScene(bubbleFixture.getBody());
    }

    private void createScoreLossText(float x, float y) {
        final Text scoreminus5Text = new Text(x, y, get(GameFontsManager.class).getFont(FontId.SCORE_TICKER_FONT), "-5", vertexBufferObjectManager);
        scoreminus5Text.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        scoreminus5Text.setColor(1, 0, 0);
        addToScene(scoreminus5Text);
        get(GameAnimationManager.class).startModifier(scoreminus5Text, new ParallelEntityModifier(
                new ScaleModifier(1.2f, 0.1f, 1.5f),
                new AlphaModifier(1.5f, 1f, 0f)));
    }
}
