package com.mekomidev.gdxengine.audio;

class SoundInstance {
	public final long id;
	
	public float volume, pitch, pan;
	public boolean looping;
	
	SoundInstance(long id) {
		this.id = id;
		reset();
	}
	
	public void reset() {
		volume = 1f;
		pitch = 1f;
		pan = 0.5f;
		looping = false;
	}
}
