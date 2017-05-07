package com.mekomidev.gdxengine.core.audio;

import java.util.List;

import com.mekomidev.gdxengine.core.Game;
import com.mekomidev.gdxengine.core.ecs.Component;
import com.mekomidev.gdxengine.core.ecs.EntitySystem;

public class SimpleAudioSystem extends EntitySystem {
	private static final int MUSIC_ID = Component.map.getSubclassId(MusicComponent.class);
	private static final int SOUND_ID = Component.map.getSubclassId(SoundComponent.class);
	
	private float volume = 1f;
	private boolean dirtyVolume = false;
	private float pitch = 1f;
	private boolean dirtyPitch = false;

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void update(float delta) {
		List<MusicComponent> music = (List<MusicComponent>) Game.stage.getComponents(MUSIC_ID);
		List<SoundComponent> sound = (List<SoundComponent>) Game.stage.getComponents(SOUND_ID);
		
		// Update music volume and pitch
		for(MusicComponent mc : music) {
			if(dirtyVolume || mc.dirtyVolume)
				mc.setVolume(volume * mc.getVolume());
			
			//NOTE: pitch in Music not yet supported
			//for(MusicComponent mc : music)
			//	mc.setPitch(pitch * mc.getPitch());
		}
		
		// Update sound volume and pitch
		for(SoundComponent sc : sound) {
			if(dirtyVolume)
				sc.setVolume(volume);
			
			if(dirtyPitch)
				sc.setPitch(pitch);
		}
		
		dirtyVolume = dirtyPitch = false;
	}

	@Override
	/** Does nothing */
	protected void render() {}
	
	@Override
	/** Does nothing */
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
	
	public void setVolume(float volume) {
		this.volume = volume;
		dirtyVolume = true;
	}
	
	public float getVolume() {
		return volume;
	}
	
	public void setPitch(float pitch) {
		this.pitch = pitch;
		dirtyPitch = true;
	}

}
