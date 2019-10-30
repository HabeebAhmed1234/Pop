package com.wack.pop2;

import android.opengl.GLES20;
import android.util.Log;

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
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.contacts.Contact;

import static com.wack.pop2.GameFixtureDefs.FLOOR_SENSOR_FIXTURE_DEF;

/**
 * This entity detects whether or not any bubbles have fallen below the screen. Thus
 * resulting in points lost.
 */
public class BubbleLossDetectorEntity extends BaseEntity {

    private static final int SCORE_DECREMENT_AMOUNT = 5;

    private final GameFontsManager fontsManager;
    private final GameAnimationManager animationManager;

    public BubbleLossDetectorEntity(GameFontsManager fontsManager, GameAnimationManager animationManager, GameResources gameResources) {
        super(gameResources);
        this.fontsManager = fontsManager;
        this.animationManager = animationManager;
    }

    @Override
    public void onCreateScene() {
        final Rectangle floorDetector = new Rectangle(0, levelHeight, levelWidth, 10, vertexBufferObjectManager);
        floorDetector.setAlpha(0);
        FixtureDef floorFixtureDef = FLOOR_SENSOR_FIXTURE_DEF;
        floorFixtureDef.setUserData(new FloorEntityUserData());
        PhysicsFactory.createBoxBody(physicsWorld, floorDetector, BodyType.STATIC, floorFixtureDef);
        physicsWorld.setContactListener(getContactListener());

    }

    private ContactListener getContactListener() {
        return new ContactListener() {
            @Override
            public void beginContact(Contact contact) {}

            @Override
            public void endContact(Contact contact) {
                if (isBubbleAndFloorContact(contact)) {
                    processBubbleFellBelowScreen(getBubbleFixture(contact));
                }
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {}
            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {}
        };
    }

    private boolean isBubbleAndFloorContact(Contact contact) {
        return (FixtureDefDataUtil.isBubbleFixtureDefData(contact.m_fixtureA) || FixtureDefDataUtil.isBubbleFixtureDefData(contact.m_fixtureB))
           &&  (FixtureDefDataUtil.isFloorFixtureDefData(contact.m_fixtureA) || FixtureDefDataUtil.isFloorFixtureDefData(contact.m_fixtureB));
    }

    private Fixture getBubbleFixture(Contact contact) {
        if (FixtureDefDataUtil.isBubbleFixtureDefData(contact.m_fixtureA)) {
            return contact.m_fixtureA;
        } else if (FixtureDefDataUtil.isBubbleFixtureDefData(contact.m_fixtureB)) {
            return contact.m_fixtureB;
        } else {
            throw new IllegalStateException("neither of the fixtures are bubbles!");
        }
    }

    private void processBubbleFellBelowScreen(Fixture bubbleFixture) {
        Log.d("asdasd", "bubble fell below screen " + ((BubbleEntityUserData) bubbleFixture.m_userData).getId());
        createScoreLossText(
                getShapeFromBody(bubbleFixture.getBody()).getX(),
                levelHeight - 50);
        EventBus.get().sendEvent(GameEvent.DECREMENT_SCORE, new DecrementScoreEventPayload(SCORE_DECREMENT_AMOUNT));
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
