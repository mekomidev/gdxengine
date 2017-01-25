package com.kerberjg.gdxstudio.core.ecs;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.kerberjg.gdxstudio.core.utils.SubclassIdentityMapper;

public abstract class Component implements Pool.Poolable, Disposable {
	public static final SubclassIdentityMapper<Component> map = new SubclassIdentityMapper<>();
	public final int typeId;
	
	public Component() {
		typeId = Component.map.registerSubclass(this.getClass());
	}
}
