package com.kerberjg.gdxstudio.entities;

import com.badlogic.gdx.utils.Disposable;
import com.kerberjg.gdxstudio.utils.Updatable;

public abstract class EntitySystem implements Updatable, Disposable {
	public abstract void init();
	public abstract void render();
	/** Best to call after every frame */
	public abstract void cleanup();
}
