package com.mekomidev.gdxstudio.core.ecs;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.mekomidev.gdxstudio.core.utils.SubclassMapper;

public abstract class Component implements Pool.Poolable, Disposable {
	/** SubclassMapper for components */
	public static final SubclassMapper<Component> map = new SubclassMapper<>();
	static { map.registerSubclass(Component.class); }
	
	/** Type ID for this Component subclass*/
	public final int typeId = map.getSubclassId(this.getClass());
}
