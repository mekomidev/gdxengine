package com.mekomidev.gdxengine.core.audio;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.LongMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.mekomidev.gdxengine.core.ecs.Component;
import com.mekomidev.gdxengine.core.utils.Units;

public class SoundComponent extends Component implements Sound, Iterable<SoundInstance> {
	public static final int MAX_SOUNDS = 128;
	public static final int MAX_SOUND_INSTANCES = 4;
	
	private static ObjectMap<FileHandle, Sound> sounds = new ObjectMap<>(MAX_SOUNDS);
	
	private Sound snd;
	private FileHandle file;
	
	private float globalVolume, globalPitch;
	private LongMap<SoundInstance> instances = new LongMap<>(MAX_SOUND_INSTANCES);
	
	public SoundComponent(FileHandle file) {
		long size = file.length();
		if(size > Units.MEGABYTE)
			throw new IllegalArgumentException("A sound can't be larger than one megabyte");
		
		snd = sounds.get(file);
		
		if(snd == null) {
			snd = Gdx.audio.newSound(file);
			sounds.put(file, snd);
		}
		
		this.file = file;
	}

	@Override
	public void reset() {
		snd.stop();
		snd = null;
	}

	@Override
	public void dispose() {
		snd.dispose();
		sounds.remove(file);
	}
	
	private SoundInstance newInstance(long id) {
		SoundInstance si = instances.get(id);
		
		if(si == null) {
			si = new SoundInstance(id);
			instances.put(id, si);
		} else
			si.reset();
		
		return si;
	}

	@Override
	public long play() {
		long id = snd.play();
		newInstance(id);
		
		return id;
	}

	@Override
	public long play(float volume) {
		long id = snd.play();
		SoundInstance si = newInstance(id);
		si.volume = volume;
		
		return id;
	}

	@Override
	public long play(float volume, float pitch, float pan) {
		long id = snd.play();
		SoundInstance si = newInstance(id);
		si.volume = volume;
		si.pitch = pitch;
		si.pan = pan;
		
		return id;
	}

	@Override
	public long loop() {
		long id = snd.play();
		SoundInstance si = newInstance(id);
		si.looping = true;
		
		return id;
	}

	@Override
	public long loop(float volume) {
		long id = snd.loop(volume * globalVolume);
		SoundInstance si = newInstance(id);
		si.looping = true;
		si.volume = volume;
		
		return id;
	}

	@Override
	public long loop(float volume, float pitch, float pan) {
		long id = snd.loop(volume * globalVolume, pitch * globalPitch, pan);
		SoundInstance si = newInstance(id);
		si.looping = true;
		si.volume = volume;
		si.pitch = pitch;
		si.pan = pan;
		
		return id;
	}

	@Override
	public void stop() {
		snd.stop();
	}

	@Override
	public void pause() {
		snd.pause();
	}

	@Override
	public void resume() {
		snd.resume();
	}

	@Override
	public void stop(long soundId) {
		snd.stop(soundId);
	}

	@Override
	public void pause(long soundId) {
		snd.pause(soundId);
	}

	@Override
	public void resume(long soundId) {
		snd.resume(soundId);
	}

	@Override
	public void setLooping(long soundId, boolean looping) {
		SoundInstance si = instances.get(soundId);
		
		if(si != null) {
			snd.setLooping(soundId, looping);
			si.looping = looping;
		}
	}
	
	/** Applies the pitch to all instances of the sound*/
	public void setPitch(float pitch) {
		if(pitch == globalPitch) return;
		
		globalPitch = pitch;
		
		for(SoundInstance si : instances.values())
			snd.setPitch(si.id, si.pitch);
	}

	@Override
	public void setPitch(long soundId, float pitch) {
		SoundInstance si = instances.get(soundId);
		
		if(si != null) {
			snd.setPitch(soundId, pitch * globalPitch);
			si.pitch = pitch;
		}
	}

	public float getPitch(long soundId) {
		SoundInstance si = instances.get(soundId);
		return (si != null ? si.pitch : -1);
	}
	
	public void setVolume(float volume) {
		globalVolume = volume;
		
		for(SoundInstance si : instances.values())
			snd.setVolume(si.id, si.volume);
	}
	
	@Override
	public void setVolume(long soundId, float volume) {
		SoundInstance si = instances.get(soundId);
		
		if(si != null) {
			snd.setVolume(soundId, volume * globalVolume);
			si.volume = volume;
		}
	}
	
	public float getVolume(long soundId) {
		SoundInstance si = instances.get(soundId);
		return (si != null ? si.volume : -1);
	}

	@Override
	public void setPan(long soundId, float pan, float volume) {
		SoundInstance si = instances.get(soundId);
		
		if(si != null) {
			snd.setPan(soundId, pan, volume * globalVolume);
			si.pan = pan;
			si.volume = volume;
		}
	}
	
	@Override
	public Iterator<SoundInstance> iterator() {
		return instances.values().iterator();
	}
}
