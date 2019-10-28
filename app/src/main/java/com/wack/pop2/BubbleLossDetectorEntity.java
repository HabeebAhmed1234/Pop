package com.wack.pop2;

import android.opengl.GLES20;

import com.wack.pop2.eventbus.DecrementScoreEventPayload;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.GameEvent;
import com.wack.pop2.fixturedefdata.FixtureDefDataUtil;
import com.wack.pop2.physics.PhysicsFactory;
import com.wack.pop2.resources.fonts.FontId;
import com.wack.pop2.resources.fonts.GameFontsManager;

import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.text.Text;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.contacts.Contact;

/**
 * This entity detects whether or not any bubbles have fallen below the screen. Thus
 * resulting in points lost.
 */
public class BubbleLossDetectorEntity extends BaseEntity {

    private static final int SCORE_DECREMENT_AMOUNT = 5;

    private final int levelWidthPx;
    private final int levelHeightPx;

    private final GameFontsManager fontsManager;

    public BubbleLossDetectorEntity(GameFontsManager fontsManager, GameResources gameResources) {
        super(gameResources);
        ScreenUtils.ScreenSize size = ScreenUtils.getSreenSize();
        this.levelWidthPx = size.width;
        this.levelHeightPx = size.height;
        this.fontsManager = fontsManager;
    }

    @Override
    public void onCreateScene() {
        final Rectangle floorDetector = new Rectangle(0, levelHeightPx, levelWidthPx, 2, vertexBufferObjectManager);
        floorDetector.setAlpha(0);
        final FixtureDef sensorFixtureDef = PhysicsFactory.createFixtureDef(0, 0, 0, true);
        PhysicsFactory.createBoxBody(physicsWorld, floorDetector, BodyType.STATIC, sensorFixtureDef);

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
            return contact.m_fixtureB
        } else {
            throw new IllegalStateException("neither of the fixtures are bubbles!");
        }
    }


    private void processBubbleFellBelowScreen(Fixture bubbleFixture) {
        scene.attachChild(createScoreLossText(bubbleFixture.getBody().getPosition().x,levelHeightPx-50));
        EventBus.get().sendEvent(GameEvent.DECREMENT_SCORE, new DecrementScoreEventPayload(SCORE_DECREMENT_AMOUNT));
        removeFromScene(bubbleFixture.getBody());
    }



    private Text createScoreLossText(float x, float y) {
        final Text scoreminus5Text = new Text(x, y, fontsManager.getFont(FontId.SCORE_TICKER_FONT), "-5", vertexBufferObjectManager);
        scoreminus5Text.registerEntityModifier(
                new SequenceEntityModifier(
                        new ParallelEntityModifier(
                                new ScaleModifier(1.2f, 0.1f, 1.5f),
                                new AlphaModifier(1.5f, 1f, 0f)
                        )
                )
        );
        scoreminus5Text.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        scoreminus5Text.setColor(1, 0, 0);
        return scoreminus5Text;
    }
}
