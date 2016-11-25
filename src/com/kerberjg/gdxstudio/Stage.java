package com.kerberjg.gdxstudio;

import com.badlogic.gdx.Gdx;
import com.kerberjg.gdxstudio.entities.EntityManager;

public class Stage {
	
	protected Stage() {}

	/**	Initializes all entities, also setting up other facilities
	 * 
	 * @author kerberjg*/
	public void create() {}
	
	/**	Updates the game logic on all entities
	 * 
	 * @author kerberjg*/
	public void update(float delta) {}
	
	/**	Updates the camera and renders all the entities
	 * 
	 * @author kerberjg*/
	public void render() {}

	public void pause() {}

	public void resume() {}
	
	/**	Resizes the viewport
	 * 
	 * @author kerberjg*/
	public void resize(int width, int height) {}

	/**	Calls dispose() on all entities, disposes of all additional classes, deactivates the listeners and calls the GC to clean up.
	 * 
	 * @author kerberjg */
	public void dispose() {
		// Reset game's InputProcessor
		Gdx.input.setInputProcessor(null);

		// Clear up the memory
		//entities.dispose();
		System.gc();
	}

	/** A helper factory interface for building Stage instances */
	public interface StageFactory {
		public Stage build();
	};
}
