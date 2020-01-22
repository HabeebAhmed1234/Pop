package com.wack.pop2;

import android.util.Log;

import com.wack.pop2.fixturedefdata.BaseEntityUserData;
import com.wack.pop2.physics.PhysicsWorld;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import androidx.annotation.Nullable;

/**
 * Enables other entities to listen to contacts that happen in the physics world involving one or two
 * bodies with extensions of {@link BaseEntityUserData}s.
 *
 * Ensures that callbacks on contact get called after a physics step has been completed
 */
public class GamePhysicsContactsEntity extends BaseEntity implements ContactListener, PhysicsWorld.OnUpdateListener {

    private static final int MAX_NOTIFICATIONS_PER_PHYSICS_UPDATE = 1;

    @Override
    public void onUpdateCompleted() {
        if (!pendingBeginContacts.isEmpty()) {
            Iterator<Contact> it = pendingBeginContacts.iterator();
            int numUpdated = 0;
            while (numUpdated < MAX_NOTIFICATIONS_PER_PHYSICS_UPDATE && it.hasNext()) {
                Contact contact = it.next();
                @Nullable Set<GameContactListener> listeners = getListenersFromContact(contact);
                if (listeners == null) {
                    return;
                }
                notifyBeginContact(listeners, contact.m_fixtureA, contact.m_fixtureB);
                numUpdated++;
            }
            pendingBeginContacts.clear();
        }
        if (!pendingEndContacts.isEmpty()) {
            Iterator<Contact> it = pendingEndContacts.iterator();
            int numUpdated = 0;
            while (numUpdated < MAX_NOTIFICATIONS_PER_PHYSICS_UPDATE && it.hasNext()){
                Contact contact = it.next();
                @Nullable Set<GameContactListener> listeners = getListenersFromContact(contact);
                if (listeners == null) {
                    return;
                }
                notifyEndContact(listeners, contact.m_fixtureA, contact.m_fixtureB);
                numUpdated++;
            }
            pendingEndContacts.clear();
        }
    }

    public interface GameContactListener {
        void onBeginContact(Fixture fixture1, Fixture fixture2);
        void onEndContact(Fixture fixture1, Fixture fixture2);
    }

    private Map<Set<Class<? extends BaseEntityUserData>>, Set<GameContactListener>> gameContactListenerMap = new HashMap<>();
    private List<Contact> pendingBeginContacts = new ArrayList<>();
    private List<Contact> pendingEndContacts = new ArrayList<>();

    public GamePhysicsContactsEntity(GameResources gameResources) {
        super(gameResources);
    }

    @Override
    public void onCreateScene() {
        physicsWorld.addOnUpdateListener(this);
        physicsWorld.setContactListener(this);
    }

    @Override
    public void onDestroy() {
        physicsWorld.setContactListener(null);
        physicsWorld.removeOnUpdateListener(this);
    }

    @Override
    public void beginContact(Contact contact) {
        if (isContactListenedTo(contact)) {
            pendingBeginContacts.add(contact);
        }
    }

    @Override
    public void endContact(Contact contact) {
        if (isContactListenedTo(contact)) {
            pendingEndContacts.add(contact);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {}

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {}


    public void addContactListener(
            Class<? extends BaseEntityUserData> entityType1,
            Class<? extends BaseEntityUserData> entityType2,
            GameContactListener contactListener) {
        Set<Class<? extends BaseEntityUserData>> key = createKeyFromTypes(entityType1, entityType2);

        if (!gameContactListenerMap.containsKey(key)) {
            gameContactListenerMap.put(key, new HashSet<GameContactListener>());
        }

        gameContactListenerMap.get(key).add(contactListener);
    }

    public void removeContactListener(
            Class<? extends BaseEntityUserData> entityType1,
            Class<? extends BaseEntityUserData> entityType2,
            GameContactListener contactListener) {
        Set<Class<? extends BaseEntityUserData>> key = createKeyFromTypes(entityType1, entityType2);
        if (gameContactListenerMap.containsKey(key)) {
            gameContactListenerMap.get(key).remove(contactListener);
        } else {
            throw new IllegalStateException("gameContactListenerMap does not contain a listener for these types");
        }
    }

    public boolean containsContactListener(
            Class<? extends BaseEntityUserData> entityType1,
            Class<? extends BaseEntityUserData> entityType2,
            GameContactListener contactListener) {
        Set<Class<? extends BaseEntityUserData>> key = createKeyFromTypes(entityType1, entityType2);
        return gameContactListenerMap.containsKey(key) && gameContactListenerMap.get(key).contains(contactListener);
    }

    /**
     * Returns true if the given contact is one that we are listening to. If not then discard it.
     */
    private boolean isContactListenedTo(Contact contact) {
        return gameContactListenerMap.containsKey(createKeyFromContact(contact));
    }

    private void notifyBeginContact(Set<GameContactListener> listeners, Fixture a, Fixture b) {
        if (physicsWorld.isLocked()) {
            throw new IllegalStateException("Physics world is locked during custom contact callback!");
        }
        for (GameContactListener listener : listeners) {
            listener.onBeginContact(a, b);
        }
    }

    private void notifyEndContact(Set<GameContactListener> listeners, Fixture a, Fixture b) {
        if (physicsWorld.isLocked()) {
            throw new IllegalStateException("Physics world is locked during custom contact callback!");
        }
        for (GameContactListener listener : listeners) {
            listener.onEndContact(a, b);
        }
    }

    /**
     * Returns the listeners (if any) for the supplied contact in the physics world
     * @param contact
     * @return
     */
    @Nullable
    private Set<GameContactListener> getListenersFromContact(Contact contact) {
        return gameContactListenerMap.get(createKeyFromContact(contact));
    }

    private Set<Class<? extends BaseEntityUserData>> createKeyFromContact(Contact contact) {
        if (contact.m_fixtureA.m_userData == null || contact.m_fixtureB.m_userData == null) {
            Log.w("GamePhysicsContacts", "Collision detected between one or more null BaseUserData fixtures");
            return null;
        }
        return createKeyFromTypes(
                (Class<BaseEntityUserData>) contact.m_fixtureA.m_userData.getClass(),
                (Class<BaseEntityUserData>) contact.m_fixtureB.m_userData.getClass());
    }

    private Set<Class<? extends BaseEntityUserData>> createKeyFromTypes(
            Class<? extends BaseEntityUserData> type1,
            Class<? extends BaseEntityUserData> type2) {
        if (type1 == null || type2 == null) {
            throw new IllegalArgumentException(
                    "You cannot create a key with one or more null BaseEntityUserData types");
        }
        Set<Class<? extends BaseEntityUserData>> key = new HashSet<>();
        key.add(type1);
        key.add(type2);
        return key;
    }
}
