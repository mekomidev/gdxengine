package com.kerberjg.gdxstudio.core;

import com.badlogic.gdx.Gdx;
import com.kerberjg.gdxstudio.core.ecs.Entity;
import com.badlogic.gdx.graphics.Color;
import com.kerberjg.gdxstudio.core.ecs.EntityManager;
import com.kerberjg.gdxstudio.core.ecs.EntitySystem;
import com.kerberjg.gdxstudio.core.ecs.PrimitiveEntity;

public class Stage extends EntityManager implements PrimitiveEntity {
	/** Background color */
	public final Color backgroundColor;
	
	protected Stage() {
		backgroundColor = new Color(Color.PURPLE);
	}
	
	@Override
	public void create() {
		// Initiates all systems
		for(EntitySystem es : systems.values())
			es.init();
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
