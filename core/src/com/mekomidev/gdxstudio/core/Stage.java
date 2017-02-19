package com.mekomidev.gdxstudio.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.mekomidev.gdxstudio.core.ecs.EntityManager;
import com.mekomidev.gdxstudio.core.ecs.PrimitiveEntity;

public class Stage extends EntityManager implements PrimitiveEntity {
	/** Background color */
	public final Color backgroundColor;
	
	// Camera system
	public Camera camera;
	
	protected Stage() {
		backgroundColor = new Color(Color.PURPLE);
		
		camera = new OrthographicCamera(100,100);
	}
	
	@Override
	public void create() {
		this.init();
		Game.assets.finishLoading();
	}	
	
	@Override
	public void render() {
		// Clears the screen with the background color
		Gdx.gl.glClearColor( backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a );
		Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT );
		
		// Renders the graphics
		super.render();
	}
	
	public void pause() {
		triggerEvent("game:status", Game.Status.PAUSE);
	}

	public void resume() {
		triggerEvent("game:status", Game.Status.RESUME);
	}
	
	public void resize(int width, int height) {
		camera.viewportWidth = width;
		camera.viewportHeight = height;
		camera.position.x = width / 2;
		camera.position.y = height / 2;
		camera.update();
		
		triggerEvent("screen:resize", width, height);
	}

	/**	Calls dispose() on all entities, disposes of all additional classes, deactivates the listeners and calls the GC to clean up */
	@Override
	public void dispose() {
		// Disposes entities, components, systems
		super.dispose();
		// Reset game's InputProcessor
		Gdx.input.setInputProcessor(null);
		// Clear up the memory
		System.gc();
	}

	/** A helper factory interface for building Stage instances 
	 * @author kerberjg */
	public static abstract class StageBuilder {
		protected Stage getStageInstance() { return new Stage(); }
		public abstract Stage build();
	}
}
