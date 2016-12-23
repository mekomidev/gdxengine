package com.kerberjg.gdxstudio.core.entities;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;

public abstract class Component implements Pool.Poolable, Disposable {
	public final int typeId;
	
	public Component() {
		typeId = Components.registerComponent(this.getClass());
	}
}
