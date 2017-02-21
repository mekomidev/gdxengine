package com.mekomidev.gdxengine.core.config;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public class JsonGameConfiguration extends GameConfiguration {
	
	public JsonGameConfiguration(FileHandle configFile) {
		this(configFile, new JsonReader());
	}
	
	public JsonGameConfiguration(FileHandle configFile, JsonReader reader) {
		if(!configFile.exists())
			throw new GdxRuntimeException("A game config file ('" + configFile.path() + "') was not found in the assets folder");
		
		JsonValue config = reader.parse(configFile);
		
		// Game loop
		JsonValue loop = config.get("loop");
		
		if(loop != null) {
			try{ this.fps = loop.getInt("fps"); } catch(IllegalArgumentException e) {
				Gdx.app.log("gdxengine", "Missing the config value for 'FPS limit', setting a default of 0");
			}
			
			try{ this.vsync = loop.getBoolean("vsync"); } catch(IllegalArgumentException e) {
				Gdx.app.log("gdxengine", "Missing the config value for 'VSync', setting a default of 'false'");
			}
			
			try{ this.deltaScale = loop.getFloat("scale"); } catch(IllegalArgumentException e) {
				Gdx.app.log("gdxengine", "Missing the config value for 'time scalling factor', setting a default of 1");
			}
		}
		
		// First stage
		try{ this.firstStage = loop.getString("stage"); } catch(IllegalArgumentException e) {
			Gdx.app.log("gdxengine", "Missing the config value for 'first stage', setting a default of null");
		}
	}
}
