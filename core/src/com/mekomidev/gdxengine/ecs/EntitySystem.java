package com.mekomidev.gdxengine.ecs;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.utils.Disposable;
import com.mekomidev.gdxengine.utils.SubclassMapper;


//TODO: rename EntitySystem to something without Entity in the name
public abstract class EntitySystem implements Disposable {
	public static final SubclassMapper<EntitySystem> map = new SubclassMapper<>();
	public final int systemId;
	
	public EntitySystem() {
		systemId = EntitySystem.map.registerSubclass(this.getClass());
	}
	
	protected abstract void init();
	protected abstract void update(float delta);
	protected abstract void render();
	
	protected static class ComponentFilter<C extends Component> implements Iterable<C> {
		public final Class<C> type;
		
		private final ArrayList<C> components = new ArrayList<>();
		
		private ComponentFilter(Class<C> classType) {
			this.type = classType;
		}
		
		public static <T extends Component> ComponentFilter<T> getInstance(Class<T> type) {
			return new ComponentFilter<T>(type);
		}

		@Override
		public Iterator<C> iterator() {
			return components.iterator();
		} 
		
	}
}
