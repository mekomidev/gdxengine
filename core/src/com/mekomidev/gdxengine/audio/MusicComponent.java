package com.mekomidev.gdxengine.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.ObjectMap;
import com.mekomidev.gdxengine.ecs.Component;

public class MusicComponent extends Component implements Music {
	private static ObjectMap<FileHandle, Music> musics = new ObjectMap<>();
	
	private Music msc;
	private FileHandle file;
	
	protected boolean dirtyVolume, dirtyPitch;
	
	public MusicComponent(FileHandle file) {
		msc = musics.get(file);
		
		if(msc == null) {
			msc = Gdx.audio.newMusic(file);
			musics.put(file, msc);
		}
		
		this.file = file;
	}

	@Override
	public void reset() {
		msc.stop();
	}

	@Override
	public void dispose() {
		msc.dispose();
		musics.remove(file);
	}

	@Override
	public void play() {
		msc.play();
	}

	@Override
	public void pause() {
		msc.pause();
	}

	@Override
	public void stop() {
		msc.stop();
	}

	@Override
	public boolean isPlaying() {
		return msc.isPlaying();
	}

	@Override
	public void setLooping(boolean isLooping) {
		msc.setLooping(isLooping);
	}

	@Override
	public boolean isLooping() {
		return msc.isLooping();
	}

	@Override
	public void setVolume(float volume) {
		msc.setVolume(volume);
	}

	@Override
	public float getVolume() {
		return msc.getVolume();
	}

	@Override
	public void setPan(float pan, float volume) {
		msc.setPan(pan, volume);
	}

	@Override
	public void setPosition(float position) {
		msc.setPosition(position);
	}

	@Override
	public float getPosition() {
		return msc.getPosition();
	}

	@Override
	public void setOnCompletionListener(OnCompletionListener listener) {
		msc.setOnCompletionListener(listener);
	}

}
