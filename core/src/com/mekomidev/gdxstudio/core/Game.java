package com.mekomidev.gdxstudio.core;

import static com.mekomidev.gdxstudio.core.Stage.StageBuilder;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.PerformanceCounter;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.mekomidev.gdxstudio.core.config.GameConfiguration;
import com.mekomidev.gdxstudio.core.config.JsonGameConfiguration;
import com.mekomidev.gdxstudio.core.utils.builders.DummyStageBuilder;
import com.mekomidev.gdxstudio.core.utils.builders.JsonStageBuilder;

/** The main ApplicationListener
 * 
 *  @author kerberjg*/
public final class Game implements ApplicationListener {
	/** Debug flag */
	public static boolean debug;
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
	
	private static boolean limitFps;
	private static int maxDeltaTime;
	/** Time simulation scale factor */
	public static float deltaScale;
	
	/** Performance counters used for performance profiling */
	private PerformanceCounter loopCounter, drawCounter, updateCounter;
	
	/** The singleton instance of this class */
	private static Game instance;
	
	/** Singleton getter
	 * @return the singleton instance of this class
	 * 
	 * @author kerberjg */
	public static Game init(boolean debug) {
		Game.debug = debug;
		
		if(instance == null)
			instance = new Game();
		
		return instance;
	}
	
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
	
	@Override
	public void create() {
		// Updates the game status
		setGameStatus(Status.INIT);
		
		// Debug mode
		if(debug)
			Gdx.app.setLogLevel(Application.LOG_DEBUG);
		
		// Counts initialization performance
		PerformanceCounter initCounter = new PerformanceCounter("Init time");
		initCounter.start();
		
		// Render splash screen
		System.out.println(Gdx.files.internal("config.json").file().getAbsolutePath());
		
		FileHandle splashFile = Gdx.files.internal("Screen.png");
		if(splashFile.exists()) {
			
		}
		
		JsonReader reader = new JsonReader();
		
		/*
		 *  Loads configs
		 */
		GameConfiguration config;
		FileHandle configFile = Gdx.files.internal("config.json");
		
		if(configFile.exists()) {
			Gdx.app.debug("gdxengine", "Found config.json, loading...");
			config = new JsonGameConfiguration(configFile, reader);
		} else {
			Gdx.app.debug("gdxengine", "No config.json found, loading default configs...");
			config = new GameConfiguration();
		}
		
		
		setFPSLimit(config.fps);
		Gdx.graphics.setVSync(config.vsync);
		deltaScale = config.deltaScale;
		
		/*
		 *	Load stages
		 */
		
		FileHandle stagesDir = Gdx.files.internal("stages/");
		if(!stagesDir.exists() || !stagesDir.isDirectory())
			Gdx.app.error("gdxengine", "A 'stages' directory was not found in the assets folder");
		
		for(FileHandle stageFile : stagesDir.list(".stg"))
			try {
				JsonValue stage = reader.parse(stageFile);
				StageBuilder builder;
				
				// Gets the name
				String name = stage.getString("name");
				
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
				System.err.print("Error loading class for stage '" + stageFile.name() + "': ");
				e.printStackTrace();
			} catch(IllegalArgumentException e1) {
				System.err.print("Error while getting stage '" + stageFile.name() + "': ");
				e1.printStackTrace();
			}
		
		// Loads the first stage
		StageBuilder firstStage = null;
		
		String firstStageName = config.firstStage;
		if(stages.size > 0)
			firstStage = (firstStageName != null ? stages.get(firstStageName) : stages.iterator().next().value);
		else {
			// TODO: in the future allow to load an empty stage with a debug interface
			Gdx.app.log("gdxengine", "No stages found, initializing a dummy stage");
			firstStage = new DummyStageBuilder();
		}
		
		System.out.println(firstStage.getClass().getName());
		stage = firstStage.build();
		
		initCounter.stop();
		Gdx.app.log("gdxengine", "Game initialized in " + (initCounter.current * 1000) + " ms");
		
		stage.create();
	}

	@Override
	public void resize(int width, int height) {
		Gdx.app.log("GAME", "Resizing screen to " + width + "x" + height);
		stage.resize(width, height);
	}

	/** Runs the game's main loop, limiting the FPS if requested and profiling the performance 
	 * 
	 * @author kerberjg */
	@Override
	public void render() {
		loopCounter.start();
		
		// Loads new stage if queued
		if(nextStage != null) {
			// Destroys the old stage gracefully
			stage.triggerEvent("stage:dispose");
			stage.dispose();
			
			// Loads the new stage
			stage = nextStage;
			nextStage = null;
			stage.create();
		}
		
		/*
		 * Updating
		 */
		updateCounter.start();
		
		delta = Gdx.graphics.getDeltaTime();
		stage.update(delta * deltaScale);
		
		updateCounter.stop();
		updateCounter.tick();
		
		/*
		 * Rendering
		 */
		drawCounter.start();
		
		stage.render();
		
		drawCounter.stop();
		drawCounter.tick();
		
		loopCounter.stop();
		loopCounter.tick();
		
		
		/*if(debug) {
			System.out.println("Loop time:" + Math.round(loopCounter.time.latest * 1000f) + " ms");
			System.out.println("Update time:" + Math.round(updateCounter.time.latest * 1000f) + " ms");
			System.out.println("Render time:" + Math.round(drawCounter.time.latest * 1000f) + " ms\n");
		}*/
		
		/*
		 *  Framerate limiting
		 */
		if(limitFps) {
			try {
				long diff = maxDeltaTime - (long)(Gdx.graphics.getRawDeltaTime() * 1000);
				
				if(diff > 0)
					Thread.sleep(diff);
			}
			catch(InterruptedException e) {
				Gdx.app.debug("LOOP", "Failed to sleep: " + e.getMessage());
			}
		}
	}

	@Override
	public void pause() {
		stage.pause();
	}

	@Override
	public void resume() {
		assets.finishLoading();
		stage.resume();
	}

	@Override
	public void dispose() {
		stage.dispose();
		assets.dispose();
	}
	
	/** Exits the game, disposing of all of its resources 
	 * 
	 * @author kerberjg */
	public static void exit() {
		stage.triggerEvent("game:exit");
		Gdx.app.exit();
	}
	
	/** Maps a string to a StageFactory for future Stage loading
	 * @param name the name to register the Stage with
	 * @param builder the relative StageBuilder class
	 * 
	 * @author kerberjg */
	public static void addStage(String name, StageBuilder builder) {
		stages.put(name, builder);
	}
	
	/** Instantiates a Stage via its registered factory and loads it on the next frame
	 * @param name the name of the Stage to load
	 * @return whether the Stage was properly instanced
	 * 
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
	 *  @param fps Game's max refresh rate rate. If 0, the limiting will be disabled
	 *  
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
	
	private static float delta;
	public static float getDelta() {
		return delta;
	}
}
