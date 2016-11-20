package com.kerberjg.gdxstudio.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Stage {
	/** The list containing all the entities on the stage */
	//TODO: consider using something similar to Android's R
	public final ObjectMap<String, Entity> entities;
	
	/** Stage's input manager */
	public final InputMultiplexer input = new InputMultiplexer();
	
	public Camera camera;
	public Viewport viewport;
	
	protected Stage(Viewport vp, Camera cam, ObjectMap<String, Entity> objects) {
		this.entities = new ObjectMap<String, Entity>(objects);
		
		camera = cam;
		viewport = vp;
		//TODO: Viewport is not used correctly, fix that!
	}

	/**	Initializes all entities, also setting up other facilities
	 * 
	 * @author kerberjg*/
	public void create() {
		Gdx.input.setInputProcessor(input);
		
		for(Entity a : entities.values())
			a.create();
	}
	
	/**	Updates the game logic on all entities
	 * 
	 * @author kerberjg*/
	public void update(float delta) {
		for(Entity a : entities.values())
			a.update(delta);
	}
	
	/**	Updates the camera and renders all the entities
	 * 
	 * @author kerberjg*/
	public void draw() {
		camera.update();
		
		for(Entity a : entities.values())
			a.draw();
	}


	public void pause() {
		// TODO Auto-generated method stub
		
	}

	public void resume() {
		// TODO Auto-generated method stub
		
	}
	
	/**	Resizes the viewport
	 * 
	 * @author kerberjg*/
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		viewport.update(width, height);
	}

	/**	Calls dispose() on all entities, disposes of all additional classes, deactivates the listeners and calls the GC to clean up.
	 * 
	 * @author kerberjg */
	public void dispose() {
		Gdx.input.setInputProcessor(null);
		
		for(String id : entities.keys())
			entities.remove(id).dispose();
		
		System.gc();
	}

	/** A helper factory class for building Stage instances */
	public interface StageFactory {
		public Stage build();
	};
}
