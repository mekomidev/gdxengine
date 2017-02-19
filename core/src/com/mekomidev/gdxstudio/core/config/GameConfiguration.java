package com.mekomidev.gdxstudio.core.config;

public class GameConfiguration {
	/** Initial FPS limit;
	 * if <= 0, the framerate is not limited;
	 * default: 60 */
	public int fps;
	/** VSync flag;
	 * default: false */
	public boolean vsync;
	/** Scalling factor of the frame delta time
	 * ignored when <= 0;
	 * default: 1 */
	public float deltaScale;
	/** Name of the first stage to open;
	 * the game will load the first stage from the list if this is null;
	 * default: null */
	public String firstStage;
	
	/** Sets default settings */
	public GameConfiguration() {
		fps = 60;
		vsync = false;
		deltaScale = 1f;
		firstStage = null;
	}
	
	/** Accepts customized settings */
	public GameConfiguration(int fps, boolean vsync, float deltaScale, String stage) {
		this.fps = fps;
		this.vsync = vsync;
		this.deltaScale = deltaScale;
		this.firstStage = stage;
	}
}
