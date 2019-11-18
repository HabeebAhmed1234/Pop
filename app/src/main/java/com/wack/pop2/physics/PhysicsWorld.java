package com.wack.pop2.physics;

import com.wack.pop2.physics.util.iterators.BodyIterator;
import com.wack.pop2.physics.util.iterators.ContactIterator;
import com.wack.pop2.physics.util.iterators.JointIterator;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.runnable.RunnableHandler;
import org.jbox2d.callbacks.ContactFilter;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.callbacks.DestructionListener;
import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.callbacks.RayCastCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.JointDef;

import java.util.Iterator;
import java.util.List;

/**
 * (c) 2010 Nicolas Gramlich 
 * (c) 2011 Zynga Inc.
 * 
 * @author Nicolas Gramlich
 * @since 15:18:19 - 15.07.2010
 */
public class PhysicsWorld implements IUpdateHandler {
	// ===========================================================
	// Constants
	// ===========================================================

	public interface OnUpdateListener {
		/**
		 * Called after a physics step completes.
		 */
		void onUpdateCompleted();
	}

	static {
		//System.loadLibrary( "andenginephysicsbox2dextension" );
	}

	public static final int VELOCITY_ITERATIONS_DEFAULT = 8;
	public static final int POSITION_ITERATIONS_DEFAULT = 8;

	// ===========================================================
	// Fields
	// ===========================================================

	protected final PhysicsConnectorManager mPhysicsConnectorManager = new PhysicsConnectorManager();
	protected final RunnableHandler mRunnableHandler = new RunnableHandler();
	protected final World mWorld;

	protected int mVelocityIterations = VELOCITY_ITERATIONS_DEFAULT;
	protected int mPositionIterations = POSITION_ITERATIONS_DEFAULT;

	private OnUpdateListener listener;

	// ===========================================================
	// Constructors
	// ===========================================================

	public PhysicsWorld(final Vec2 pGravity, final boolean pAllowSleep) {
		this(pGravity, pAllowSleep, VELOCITY_ITERATIONS_DEFAULT, POSITION_ITERATIONS_DEFAULT);
	}

	public PhysicsWorld(final Vec2 pGravity, final boolean pAllowSleep, final int pVelocityIterations, final int pPositionIterations) {
		this.mWorld = new World(pGravity);
		this.mWorld.setAllowSleep(pAllowSleep);
		this.mVelocityIterations = pVelocityIterations;
		this.mPositionIterations = pPositionIterations;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	//	public World getWorld() {
	//		return this.mWorld;
	//	}

	public int getPositionIterations() {
		return this.mPositionIterations;
	}

	public void setPositionIterations(final int pPositionIterations) {
		this.mPositionIterations = pPositionIterations;
	}

	public int getVelocityIterations() {
		return this.mVelocityIterations;
	}

	public void setVelocityIterations(final int pVelocityIterations) {
		this.mVelocityIterations = pVelocityIterations;
	}

	public PhysicsConnectorManager getPhysicsConnectorManager() {
		return this.mPhysicsConnectorManager;
	}

	public void clearPhysicsConnectors() {
		this.mPhysicsConnectorManager.clear();
	}

	public void registerPhysicsConnector(final PhysicsConnector pPhysicsConnector) {
		this.mPhysicsConnectorManager.add(pPhysicsConnector);
	}

	public void unregisterPhysicsConnector(final PhysicsConnector pPhysicsConnector) {
		this.mPhysicsConnectorManager.remove(pPhysicsConnector);
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public void onUpdate(final float pSecondsElapsed) {
		this.mRunnableHandler.onUpdate(pSecondsElapsed);
		this.mWorld.step(pSecondsElapsed, this.mVelocityIterations, this.mPositionIterations);
		this.mPhysicsConnectorManager.onUpdate(pSecondsElapsed);
		this.listener.onUpdateCompleted();
	}

	public void setOnUpdateListener(OnUpdateListener listener) {
		this.listener = listener;
	}

	@Override
	public void reset() {
		// TODO Reset all native physics objects !?!??!
		this.mPhysicsConnectorManager.reset();
		this.mRunnableHandler.reset();
	}

	// ===========================================================
	// Methods
	// ===========================================================

	public void postRunnable(final Runnable pRunnable) {
		this.mRunnableHandler.postRunnable(pRunnable);
	}

	public void clearForces() {
		this.mWorld.clearForces();
	}

	public Body createBody(final BodyDef pDef) {
		return this.mWorld.createBody(pDef);
	}

	public Joint createJoint(final JointDef pDef) {
		return this.mWorld.createJoint(pDef);
	}

	public void destroyBody(final Body pBody) {
		this.mWorld.destroyBody(pBody);
	}

	public void destroyJoint(final Joint pJoint) {
		this.mWorld.destroyJoint(pJoint);
	}

	public void dispose() {
		// TODO: wat
		//this.mWorld.dispose();
	}

	public boolean getAutoClearForces() {
		return this.mWorld.getAutoClearForces();
	}

	public Iterator<Body> getBodies() {
		return BodyIterator.instanceOf(mWorld);
	}

	public int getBodyCount() {
		return this.mWorld.getBodyCount();
	}

	public int getContactCount() {
		return this.mWorld.getContactCount();
	}

	public Iterator<Contact> getContacts() {
		return ContactIterator.instanceOf(mWorld);
	}

	public Vec2 getGravity() {
		return this.mWorld.getGravity();
	}

	public Iterator<Joint> getJoints() {
		return JointIterator.instanceOf(mWorld);
	}

	public int getJointCount() {
		return this.mWorld.getJointCount();
	}

	public int getProxyCount() {
		return this.mWorld.getProxyCount();
	}

	public boolean isLocked() {
		return this.mWorld.isLocked();
	}

	public void QueryAABB(final QueryCallback pCallback, final float pLowerX, final float pLowerY, final float pUpperX, final float pUpperY) {
		this.mWorld.queryAABB(pCallback, new AABB(new Vec2(pLowerX, pLowerY) , new Vec2(pUpperX, pUpperY)));
	}

	public void setAutoClearForces(final boolean pFlag) {
		this.mWorld.setAutoClearForces(pFlag);
	}

	public void setContactFilter(final ContactFilter pFilter) {
		this.mWorld.setContactFilter(pFilter);
	}

	public void setContactListener(final ContactListener pListener) {
		this.mWorld.setContactListener(pListener);
	}

	public void setContinuousPhysics(final boolean pFlag) {
		this.mWorld.setContinuousPhysics(pFlag);
	}

	public void setDestructionListener(final DestructionListener pListener) {
		this.mWorld.setDestructionListener(pListener);
	}

	public void setGravity(final Vec2 pGravity) {
		this.mWorld.setGravity(pGravity);
	}

	public void setWarmStarting(final boolean pFlag) {
		this.mWorld.setWarmStarting(pFlag);
	}

	public void rayCast(final RayCastCallback pRayCastCallback, final Vec2 pPoint1, final Vec2 pPoint2) {
		this.mWorld.raycast(pRayCastCallback, pPoint1, pPoint2);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
