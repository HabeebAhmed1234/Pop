package com.wack.pop2.turret;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.BubblePopperEntity;
import com.wack.pop2.GamePhysicsContactsEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.TurretBulletPoppedBubbleEventPayload;
import com.wack.pop2.fixturedefdata.BubbleEntityUserData;
import com.wack.pop2.fixturedefdata.FixtureDefDataUtil;
import com.wack.pop2.fixturedefdata.TurretBulletUserData;
import com.wack.pop2.utils.CoordinateConversionUtil;

import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;

import static com.wack.pop2.eventbus.GameEvent.TURRET_BULLET_POPPED_BUBBLE;

public class TurretsBulletsCollisionManager extends BaseEntity implements GamePhysicsContactsEntity.GameContactListener {

    private BubblePopperEntity bubblePopperEntity;
    private GamePhysicsContactsEntity gamePhysicsContactsEntity;

    public TurretsBulletsCollisionManager(
            BubblePopperEntity bubblePopperEntity,
            GamePhysicsContactsEntity gamePhysicsContactsEntity,
            GameResources gameResources) {
        super(gameResources);
        this.bubblePopperEntity = bubblePopperEntity;
        this.gamePhysicsContactsEntity = gamePhysicsContactsEntity;
        addContactListener();
    }

    @Override
    public void onCreateScene() {
        addContactListener();
    }

    @Override
    public void onDestroy() {
        gamePhysicsContactsEntity.removeContactListener(TurretBulletUserData.class, BubbleEntityUserData.class, this);
    }

    @Override
    public void onBeginContact(Fixture fixture1, Fixture fixture2) {
        Fixture bubbleFixture = FixtureDefDataUtil.getBubbleFixture(fixture1, fixture2);
        BubbleEntityUserData bubbleEntityUserData = (BubbleEntityUserData) bubbleFixture.getUserData();
        if (!bubbleEntityUserData.isPoppable()) {
            return;
        }
        Body bubbleBody  = bubbleFixture.getBody();
        bubblePopperEntity.popBubble(
                bubbleEntityUserData.bubbleSprite,
                bubbleEntityUserData.size,
                CoordinateConversionUtil.physicsWorldToScene(bubbleBody.getPosition()),
                bubbleEntityUserData.bubbleType);

        Fixture bulletFixture = FixtureDefDataUtil.getBulletFixture(fixture1, fixture2);
        TurretBulletUserData bulletUserData = (TurretBulletUserData) bulletFixture.getUserData();
        EventBus.get().sendEvent(TURRET_BULLET_POPPED_BUBBLE, new TurretBulletPoppedBubbleEventPayload(bulletUserData.getId()));
    }

    @Override
    public void onEndContact(Fixture fixture1, Fixture fixture2) {}

    private void addContactListener() {
        if (!gamePhysicsContactsEntity.containsContactListener(TurretBulletUserData.class, BubbleEntityUserData.class, this)) {
            gamePhysicsContactsEntity.addContactListener(TurretBulletUserData.class, BubbleEntityUserData.class, this);
        }
    }
}
