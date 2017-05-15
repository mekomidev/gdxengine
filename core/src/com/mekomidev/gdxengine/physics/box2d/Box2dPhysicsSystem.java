package com.mekomidev.gdxengine.physics.box2d;

import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.mekomidev.gdxengine.Game;
import com.mekomidev.gdxengine.ecs.Component;
import com.mekomidev.gdxengine.ecs.EntitySystem;
import com.mekomidev.gdxengine.graphics.g3d.CameraComponent;
import com.mekomidev.gdxengine.utils.Units;

public class Box2dPhysicsSystem extends EntitySystem {
	private static final int CAMERA_ID = Component.map.getSubclassId(CameraComponent.class);
	
	public int velocityIterations = 10,
				positionIterations = 5;
	
	private final Vector2 gravity = new Vector2(0, -Units.g);
	public final World world = new World(gravity, true);
	private final Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();
	
	@Override
	protected void init() {
		// Choose the number of iterations basing on the type of device
		/*
		switch(Math.round(Game.getComputingScore() / 0.2f)) {
			// Low-end mobile device
			case 0:*/
				velocityIterations = 6;
				positionIterations = 2; /*
				break;
			
			// Average mobile device OR Low-end PC
			case 1:
				velocityIterations = 8;
				positionIterations = 2;
				break;
				
			case 2:
				velocityIterations = 10;
				positionIterations = 4;
				break;
				
			case 3:
				velocityIterations = 12;
				positionIterations = 4;
				break;
				
			case 4:
			default:
				velocityIterations = 16;
				positionIterations = 6;
				break;	
		}
		*/
	}
	
	private float timeAccumulator = 0;
	
	@Override
	/** Simulates the world */
	protected void update(float delta) {
		// Locks the physics simulation to 60 FPS if no frame limit is set for the game
		float timeStep = Game.getFrameStep();
		timeStep = (timeStep == 0f ? (1f / 60) : timeStep);
		
		timeAccumulator += delta;
		
		while(timeAccumulator >= timeStep) {
			timeAccumulator -= timeStep;
			world.step(timeStep, velocityIterations, positionIterations);
		}
	}

	@Override
	protected void render() {
		if(Game.debug) {
			@SuppressWarnings("unchecked")
			List<CameraComponent> cameras = (List<CameraComponent>) Game.stage.getComponents(CAMERA_ID);
			
			for(CameraComponent cc : cameras)
				debugRenderer.render(world, cc.cam.projection);
		}
	}
	
	@Override
	public void dispose() {
		world.dispose();
		debugRenderer.dispose();
	}

}
