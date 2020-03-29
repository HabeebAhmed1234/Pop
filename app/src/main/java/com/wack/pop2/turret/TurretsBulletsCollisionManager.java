package com.wack.pop2.turret;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GamePhysicsContactsEntity;
import com.wack.pop2.binder.Binder;
import com.wack.pop2.binder.BinderEnity;
import com.wack.pop2.bubblepopper.BubblePopperEntity;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.TurretBulletPoppedBubbleEventPayload;
import com.wack.pop2.fixturedefdata.BubbleEntityUserData;
import com.wack.pop2.fixturedefdata.FixtureDefDataUtil;
import com.wack.pop2.fixturedefdata.TurretBulletUserData;

import org.jbox2d.dynamics.Fixture;

import static com.wack.pop2.eventbus.GameEvent.TURRET_BULLET_POPPED_BUBBLE;

public class TurretsBulletsCollisionManager extends BaseEntity implements GamePhysicsContactsEntity.GameContactListener {

    public TurretsBulletsCollisionManager(BinderEnity parent) {
        super(parent);
        addContactListener();
    }

    @Override
    protected void createBindings(Binder binder) {

    }

    @Override
    public void onCreateScene() {
        addContactListener();
    }

    @Override
    public void onDestroy() {
        get(GamePhysicsContactsEntity.class).removeContactListener(TurretBulletUserData.class, BubbleEntityUserData.class, this);
    }

    @Override
    public void onBeginContact(Fixture fixture1, Fixture fixture2) {
        Fixture bubbleFixture = FixtureDefDataUtil.getBubbleFixture(fixture1, fixture2);
        BubbleEntityUserData bubbleEntityUserData = (BubbleEntityUserData) bubbleFixture.getUserData();
        if (!bubbleEntityUserData.isPoppable()) {
            return;
        }
        get(BubblePopperEntity.class).popBubble(
                bubbleEntityUserData.bubbleSprite,
                bubbleEntityUserData.size,
                bubbleEntityUserData.bubbleType);

        Fixture bulletFixture = FixtureDefDataUtil.getBulletFixture(fixture1, fixture2);
        TurretBulletUserData bulletUserData = (TurretBulletUserData) bulletFixture.getUserData();
        EventBus.get().sendEvent(TURRET_BULLET_POPPED_BUBBLE, new TurretBulletPoppedBubbleEventPayload(bulletUserData.getId()));
    }

    @Override
    public void onEndContact(Fixture fixture1, Fixture fixture2) {}

    private void addContactListener() {
        GamePhysicsContactsEntity gamePhysicsContactsEntity = get(GamePhysicsContactsEntity.class);
        if (!gamePhysicsContactsEntity.containsContactListener(TurretBulletUserData.class, BubbleEntityUserData.class, this)) {
            gamePhysicsContactsEntity.addContactListener(TurretBulletUserData.class, BubbleEntityUserData.class, this);
        }
    }
}
