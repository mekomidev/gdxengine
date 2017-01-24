package com.kerberjg.gdxstudio.core.ecs;

import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ReflectionPool;
import com.kerberjg.gdxstudio.core.utils.collections.FastIntMap;

/** A static singleton for the management of components
 * 
 * @author kerberjg
 * */
// TODO: this class is a minefield of NullPointerExceptions; eradicate those abominations!
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
	
	/**	Registers the Component in the manager and returns its new ID, or, if already registered, returns its preexisting ID
	 * @return Component type ID
	 * @param ct Component class to register */
	public static <C extends Component> int registerComponent(Class<C> ct) {
		// Registers the component
		final int id = componentMap.put(ct);
		inverseComponentMap.put(ct, id);
		
		// Creates and registers a Pool for the component type
		poolMap.put(id, (Pool<? extends Component>) new ReflectionPool<C>(ct));
		
		return id;	
	}
	
	/** @return whether the Component with the specified ID is registered
	 * @param id the ID of the Component to check */
	public static boolean hasComponent(int id) {
		return componentMap.containsKey(id);
	}
	
	/** @return a Component's class by its ID 
	 * @param id the ID of a Component */
	public static Class<? extends Component> getComponentClass(int id) {
		return componentMap.get(id);
	}
	
	/** @return a Component's ID by it's class
	 * @param componentType a Component's class */
	public static int getComponentId(Class<? extends Component> componentType) {
		return inverseComponentMap.get(componentType, -1);
	}
	
	/** @return a new instance of Component with class ct
	 * @param componentType a Component's class */
	public static <C extends Component> C getComponentInstance(Class<C> componentType) {
		final int id = inverseComponentMap.get(componentType, -1);
		return getComponentInstance(id);
	}
	
	/** @return a new instance of Component with ID id
	 * @param id the ID of a Component */
	public static <C extends Component> C getComponentInstance(int id) {
		@SuppressWarnings("unchecked")
		Class<C> ct = (Class<C>) componentMap.get(id);
		
		if(id >= 0)
			return ct.cast(poolMap.get(id).obtain());
		else
			return null;	
	}
	
	@SuppressWarnings("unchecked")
	/** Puts a Component into the pool, freeing it from use
	 * @param c the Component to free */
	public static <C extends Component> void freeComponent(C c) {
		((Pool<C>)poolMap.get(c.typeId)).free(c);
	}
}
