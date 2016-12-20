package com.kerberjg.gdxstudio;

import com.badlogic.gdx.Gdx;
import com.kerberjg.gdxstudio.entities.Entity;
import com.kerberjg.gdxstudio.entities.EntityManager;
import com.kerberjg.gdxstudio.entities.EntitySystem;
import com.kerberjg.gdxstudio.entities.PrimitiveEntity;

public class Stage extends EntityManager implements PrimitiveEntity {
	
	protected Stage() {}
	
	@Override
	public void create() {
		// Initiates all systems
		for(EntitySystem es : systems.values())
			es.init();
		
		for(Entity e : entities)
			e.create();
	};
	
	public void pause() {
		for(Entity e : entities)
			e.event("game:status", Game.Status.PAUSE);
	}

	public void resume() {
		for(Entity e : entities)
			e.event("game:status", Game.Status.RESUME);
	}
	
	public void resize(int width, int height) {
		for(Entity e : entities)
			e.event("screen:resize", width, height);
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
	public interface StageBuilder {
		public Stage build();
	}	
}
