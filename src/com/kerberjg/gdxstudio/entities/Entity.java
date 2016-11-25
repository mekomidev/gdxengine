package com.kerberjg.gdxstudio.entities;

import com.badlogic.gdx.utils.Disposable;
import com.kerberjg.gdxstudio.utils.collections.HybridMap;

public class Entity implements Disposable {
	private HybridMap<Class<? extends Component>, Component> components = new HybridMap<Class<? extends Component>, Component>();
	
	public Entity() {}

	@Override
	public void dispose() {}
}
