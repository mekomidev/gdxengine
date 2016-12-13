package com.kerberjg.gdxstudio.entities;

import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ReflectionPool;
import com.kerberjg.gdxstudio.utils.collections.FastIntMap;

/**
 * 
 * @author kerberjg
 * */
public final class Components {
	/**Prevent this class from instancing (static singleton)*/
	private Components() {}
	
	/** Maps integer IDs to Component classes*/
	private static FastIntMap<Class<? extends Component>> componentMap = new FastIntMap<Class<? extends Component>>();
	/** Map for reverse lookup; maps Component classes to their IDs*/
	private static ObjectIntMap<Class<? extends Component>> inverseComponentMap = new ObjectIntMap<>();
	/** Map of Components' object pools */
	private static FastIntMap<Pool<? extends Component>> poolMap = new FastIntMap<Pool<? extends Component>>();
	
	/** Resets all the maps */
	public static void init() {
		componentMap.clear();
		inverseComponentMap.clear();
		
		poolMap.clear();
	}
	
	/**	Registers the Component in the manager; if the Component has been registered previously, just returns its type ID
	 * @return Component type ID*/
	public static <C extends Component> int registerComponent(Class<C> ct) {
		// Registers the component
		final int id = componentMap.put(ct);
		inverseComponentMap.put(ct, id);
		
		// Creates and registers a Pool for the component type
		poolMap.put(id, (Pool<? extends Component>) new ReflectionPool<C>(ct));
		
		return id;	
	}
	
	public static boolean hasComponent(int id) {
		return componentMap.containsKey(id);
	}
	
	/** @return a Component's class by its ID */
	public static Class<? extends Component> getComponentClass(int id) {
		return componentMap.get(id);
	}
	
	/** @return a Component's ID by it's class */
	public static int getComponentId(Class<? extends Component> componentType) {
		return inverseComponentMap.get(componentType, -1);
	}
	
	/** @return a new instance of Component with class ct*/
	public static <C extends Component> C getComponentInstance(Class<C> componentType) {
		final int id = inverseComponentMap.get(componentType, -1);
		return getComponentInstance(id);
	}
	
	/** @return a new instance of Component with ID id*/
	public static <C extends Component> C getComponentInstance(int id) {
		@SuppressWarnings("unchecked")
		Class<C> ct = (Class<C>) componentMap.get(id);
		
		if(id >= 0)
			return ct.cast(poolMap.get(id).obtain());
		else
			return null;	
	}
	
	@SuppressWarnings("unchecked")
	public static <C extends Component> void freeComponent(C c) {
		((Pool<C>)poolMap.get(c.typeId)).free(c);
	}
}
