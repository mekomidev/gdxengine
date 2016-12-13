package com.kerberjg.gdxstudio.entities;

import com.badlogic.gdx.utils.Disposable;

public abstract class EntitySystem implements Disposable {
	/** Best to call after every frame */
	public abstract void cleanup();
}
