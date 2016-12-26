package com.kerberjg.gdxstudio.core;

import static com.kerberjg.gdxstudio.core.Stage.StageBuilder;

import java.io.File;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.PerformanceCounter;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.kerberjg.gdxstudio.core.utils.builders.JsonStageBuilder;

/** The main ApplicationListener
 * 
 *  @author kerberjg*/
public final class Game implements ApplicationListener {
	/** An enumerator representing the various states of the game engine */
	public static enum Status { INIT, RUN, PAUSE, RESUME, STOP };
	private static Status status;
	
	private static void setGameStatus(final Status status) {
		Game.status = status;
		if(stage != null && status != Status.STOP)
			stage.triggerEvent("game:status", status);
	}
	
	public static Status getGameStatus() {
		return status;
	}
	
	/** Current stage */
	public static Stage stage;
	/** Stage queue; at the end of a frame, the stage in this variable is loaded */
	private static Stage nextStage;
	/** A map of Stage factories for fast Stage instancing */
	private static ObjectMap<String, StageBuilder> stages;
	
	/** Game's asset manager */
	public static AssetManager assets;
	
	// TODO: consider a performance-scalling mode for low-power operation
	private static boolean limitFps;
	private static int maxDeltaTime;
	/** Time simulation scale factor */
	public static float deltaScale;
	
	/** Performance counters used for performance profiling */
	private PerformanceCounter loopCounter, drawCounter, updateCounter;
	
	/** Initializes all necessary classes and sets all default values */
	private Game() {
		// Game status
		status = Status.STOP;
		
		// Managers and collections
		assets = new AssetManager();
		stages = new ObjectMap<>(16);
		
		// Performance counters
		loopCounter = new PerformanceCounter("Loop duration");
		drawCounter = new PerformanceCounter("Draw duration");
		updateCounter = new PerformanceCounter("Update duration");
	}
	
	public static void init() {
		// Allows to run the method only if the class was stopped
		if(Game.status != Status.STOP) return;
		
		// Updates the game status
		setGameStatus(Status.INIT);
		
		// Counts initialization performance
		PerformanceCounter initCounter = new PerformanceCounter("Init time");
		initCounter.start();
		
		/*
		 *  Loads configs
		 */
		JsonReader reader = new JsonReader();
		FileHandle configFile = Gdx.files.internal("config.json");
		if(!configFile.exists())
			throw new GdxRuntimeException("A game config file ('config.json') was not found in the assets folder");
		
		JsonValue config = reader.parse(configFile);
		
		// Game loop
		JsonValue loop = config.get("loop");
		int fps = 0; boolean vsync = true; float scale = 1f; // defaults
		
		if(loop != null) {
			try{ fps = loop.getInt("fps"); } catch(IllegalArgumentException e) {
				Gdx.app.log("gdxengine", "Missing the config value for 'FPS limit', setting a default of 0");
			}
			
			try{ vsync = loop.getBoolean("vsync"); } catch(IllegalArgumentException e) {
				Gdx.app.log("gdxengine", "Missing the config value for 'VSync', setting a default of 'true'");
			}
			
			try{ scale = loop.getFloat("scale"); } catch(IllegalArgumentException e) {
				Gdx.app.log("gdxengine", "Missing the config value for 'time scalling factor', setting a default of 1");
			}
		}
		
		setFPSLimit(fps);
		Gdx.graphics.setVSync(vsync);
		deltaScale = scale;
		
		
		/*
		 *	Load stages
		 */
		
		FileHandle f = Gdx.files.internal("assets/Screen.png");
		System.out.println(String.valueOf(f.exists()) + '\n');
		
		FileHandle stagesDir = Gdx.files.internal("stages/");
		if(!stagesDir.exists() || !stagesDir.isDirectory())
			throw new GdxRuntimeException("A 'stages' directory was not found in the assets folder");
		
		for(FileHandle stageFile : stagesDir.list(".stg"))
			try {
				JsonValue stage = reader.parse(stageFile);
				StageBuilder builder;
				
				// Gets the name
				String name = stage.name;
				
				// Gets the StageBuilder
				switch(stage.getString("type")) {
				case "class":
					Class<?> builderClass = ClassReflection.forName(stage.getString("class"));
					builder = (StageBuilder) ClassReflection.newInstance(builderClass);
					break;
				
				case "json":
					JsonValue def = stage.get("def");
					builder = new JsonStageBuilder(def);
					break;
					
				default:
					throw new IllegalArgumentException("The required type of stage \"" + name + "\" is not present");
				}
				
				stages.put(name, builder);
			} catch(ReflectionException e) {
				
			} catch(IllegalArgumentException e1) {
				
			}
		
		// Loads the first stage
		
		initCounter.stop();
		Gdx.app.log("gdxengine", "Game initialized in " + (initCounter.current * 1000) + " ms");
	}
	
	@Override
	public void create() {
		if(stage != null)
			stage.create();
		else {
			Gdx.app.error("gdxengine", "A Stage instance was not initialized before the Game#create event");
			Game.exit();
		}
	}

	@Override
	public void resize(int width, int height) {
		Gdx.app.debug("GAME", "Resizing screen to" + width + "x" + height);
		assert(stage != null);
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
		
		// Runs the game loop
		updateLogic();
		renderGraphics();
		
		loopCounter.stop();
		
		// Limits the rendering rate if enabled
		if(limitFps) {
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
	
	public static void exit() {
		if(stage != null)
			stage.dispose();
		
		Gdx.graphics.setContinuousRendering(false);
		System.exit(0);
	}
	
	/** Maps a string to a StageFactory for future Stage loading
	 * 
	 * @author kerberjg */
	public static void addStage(String name, StageBuilder sfactory) {
		stages.put(name, sfactory);
	}
	
	/** Instantiates a Stage via its registered factory and loads it on the next frame
	 * 
	 * @return whether the Stage was properly instanced
	 * @author kerberjg */
	public static boolean loadStage(String name) {
		StageBuilder builder = stages.get(name);
		
		if(builder != null) {
			nextStage = builder.build();
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
			limitFps = false;
			maxDeltaTime = 0;
		}
		else {
			limitFps = true;
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
