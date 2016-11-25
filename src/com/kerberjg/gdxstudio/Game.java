package com.kerberjg.gdxstudio;

import static com.kerberjg.gdxstudio.Stage.StageFactory;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.PerformanceCounter;

/** The main ApplicationListener
 * 
 *  @author kerberjg*/
public final class Game implements ApplicationListener {
	/** Current stage */
	public static Stage stage;
	/** Stage queue; at the end of a frame, the stage in this variable is loaded */
	private static Stage nextStage;
	/** A map of Stage factories for fast Stage instancing */
	private static ObjectMap<String, StageFactory> stages;
	
	/** Game's asset manager */
	public static AssetManager assets;
	
	private static int limitFps, maxDeltaTime;
	/** Time simulation scale factor */
	public static float deltaScale;
	
	/** Performance counters used for performance profiling */
	private PerformanceCounter loopCounter, drawCounter, updateCounter;
	
	/** Multithreading flag. If set to true, the game's update will be performed on a secondary thread.
	 * If set, the change will become effective only in the next frame */
	public boolean mtEnabled;
	/** Game's update thread. Used only when multithreading is enabled */
	private Thread updateThread;
	
	private Game() {
		PerformanceCounter initCounter = new PerformanceCounter("Init time");
		initCounter.start();
		
		/*
		 *  Initializes the game
		 */
		assets = new AssetManager();
		stages = new ObjectMap<String, StageFactory>(10);
		
		deltaScale = 1f;
		
		loopCounter = new PerformanceCounter("Loop duration");
		drawCounter = new PerformanceCounter("Draw duration");
		updateCounter = new PerformanceCounter("Update duration");
		
		updateThread = new Thread("updateThread") {
			@Override
			public void run() { updateLogic(); }
		};
		
		/*
		 *  Loads configs
		 */
		
		initCounter.stop();
		Gdx.app.debug("GAME", "Game initialized in " + (initCounter.current * 1000) + " ms");
	}
	
	@Override
	public void create() {
		stage.create();
	}

	@Override
	public void resize(int width, int height) {
		Gdx.app.debug("GAME", "Resizing screen to" + width + "x" + height);
		stage.resize(width, height);
	}

	/** Runs the game's main loop, limiting the FPS if requested and profiling the performance 
	 * 
	 * @author kerberjg */
	@Override
	public void render() {
		loopCounter.start();
		
		// Loads new stage if queued
		// TODO: solve this better!
		if(nextStage != null) {
			stage.dispose();
			stage = nextStage;
			nextStage = null;
			stage.create();
		}
		
		if(mtEnabled) {
			try {
				// TODO: rewrite this to support more than 2 threads
				// Start update thread
				updateThread.start();
				
				// Render graphics
				renderGraphics();
				
				// Wait for the update thread
				updateThread.join();
			}
			catch(InterruptedException e) {
				e.printStackTrace();
				Gdx.app.error("LOOP", "Failed to join the update thread: " + e.getMessage());
			}
		}
		else {
			updateLogic();
			renderGraphics();
		}
		
		loopCounter.stop();
		
		// Limits the rendering rate if enabled
		if(limitFps > 0) {
			try {
				float diff = maxDeltaTime - Gdx.graphics.getRawDeltaTime();
				
				if(diff > 0)
					Thread.sleep((long) (diff * 1000));
			}
			catch(InterruptedException e) {
				e.printStackTrace();
				Gdx.app.error("LOOP", "Failed to sleep: " + e.getMessage());
			}
		}
	}
	
	private void renderGraphics() {
		drawCounter.start();
		
		// Clears the screen
		Gdx.gl.glClearColor( 1f, 0f, 1f, 1f );
		Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT );
		
		stage.render();
		
		drawCounter.stop();
	}
	
	private void updateLogic() {
		updateCounter.start();
		
		float delta = Gdx.graphics.getDeltaTime();
		stage.update(delta * deltaScale);
		
		updateCounter.stop();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		stage.pause();
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		stage.resume();
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		stage.dispose();
	}
	
	/** Maps a string to a StageFactory for future Stage loading
	 * 
	 * @author kerberjg */
	public static void addStage(String name, StageFactory sfactory) {
		stages.put(name, sfactory);
	}
	
	/** Instantiates a Stage via its registered factory and loads it on the next frame
	 * 
	 * @return whether the Stage was properly instanced
	 * @author kerberjg */
	public static boolean loadStage(String name) {
		StageFactory sfactory = stages.get(name);
		
		if(sfactory != null) {
			nextStage = sfactory.build();
			return true;
		} else
			return false;
	}
	
	/** Sets the FPS limit for the game
	 * 
	 *  @param fps Game's max refresh rate rate. If 0, the limiting will be disabled
	 *  @author kerberjg */
	public static void setFPSLimit(int fps) {
		if(fps == 0) {
			limitFps = 0;
			maxDeltaTime = 0;
		}
		else {
			limitFps = fps;
			maxDeltaTime = 1000 / fps;
		}
	}
	
	/** The singleton instance of this class */
	private static Game instance;
	
	/** Returns the singleton instance of this class
	 * 
	 * @author kerberjg */
	public static Game getInstance() {
		if(instance == null)
			instance = new Game();
		
		return instance;
	}
}
