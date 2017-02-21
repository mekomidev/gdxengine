package com.mekomidev.gdxengine.core.ecs;

import com.badlogic.gdx.utils.Disposable;
import com.mekomidev.gdxengine.core.utils.SubclassMapper;


//TODO: rename EntitySystem to something without Entity in the name
public abstract class EntitySystem implements Disposable {
	public static final SubclassMapper<EntitySystem> map = new SubclassMapper<>();
	public final int systemId;
	
	public EntitySystem() {
		systemId = EntitySystem.map.registerSubclass(this.getClass());
	}
	
	/**
	 * @return an int array containing the IDs of components to pass */
	protected abstract int[] init();
	
	public abstract void updateBegin(float delta);
	public abstract <C extends Component> void updateStep(C component);
	public abstract void updateEnd();
	
	public abstract void render();
}
