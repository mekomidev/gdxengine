package com.kerberjg.gdxstudio.core.utils;

import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ReflectionPool;
import com.kerberjg.gdxstudio.core.utils.collections.FastIntMap;

/**	This class maps subclasses of a specified class to integer IDs, allowing them to be referenced/identified/instantiated faster
 * 
 * @author kerberjg */
public class SubclassMapper<C> {
	/** Maps integer IDs to subclasses*/
	private FastIntMap<Class<? extends C>> subclassMap = new FastIntMap<>();
	/** Map for reverse lookup; maps subclasses to their IDs*/
	private ObjectIntMap<Class<? extends C>> inverseSubclassMap = new ObjectIntMap<>();
	/** Map of subclasses' object pools */
	private FastIntMap<Pool<? extends C>> poolMap = new FastIntMap<>();
	
	/** Resets all the maps */
	public void reset() {
		subclassMap.clear();
		inverseSubclassMap.clear();
		
		poolMap.clear();
	}
	
	/**	Registers the subclass in the manager and returns its new ID, or, if already registered, returns its preexisting ID
	 * @return subclass type ID
	 * @param ct subclass class to register */
	public <T extends C> int registerSubclass(Class<T> ct) {
		// Registers the component
		final int id = subclassMap.put(ct);
		inverseSubclassMap.put(ct, id);
		
		// Creates and registers a Pool for the component type
		poolMap.put(id, (Pool<T>) new ReflectionPool<T>(ct));
		
		return id;	
	}
	
	/** @return whether the subclass with the specified ID is registered
	 * @param id the ID of the subclass to check */
	public boolean hasSubclass(int id) {
		return subclassMap.containsKey(id);
	}
	
	/** @return a subclass' class by its ID 
	 * @param id the ID of a subclass */
	public Class<? extends C> getSubclassClass(int id) {
		return subclassMap.get(id);
	}
	
	/** @return a subclass' ID by it's class
	 * @param componentType a subclass' class */
	public int getSubclassId(Class<? extends C> componentType) {
		return inverseSubclassMap.get(componentType, -1);
	}
	
	/** @return a new instance of the subclass identified by a class
	 * @param componentType a subclass' class */
	public <T extends C> T getSubclassInstance(Class<T> componentType) {
		final int id = inverseSubclassMap.get(componentType, -1);
		return getSubclassInstance(id);
	}
	
	/** @return a new instance of a subclass identified by an ID
	 * @param id the ID of the subclass */
	public <T extends C> T getSubclassInstance(int id) {
		if(id >= 0) {
			@SuppressWarnings("unchecked")
			Class<T> ct = (Class<T>) subclassMap.get(id);
			@SuppressWarnings("unchecked")
			Pool<T> pool = (Pool<T>) poolMap.get(id);
			
			if(pool != null)
				return ct.cast(pool.obtain());
			else
				throw new RuntimeException("Class' " + ct.getClass().getName() + " pool wasn't registered properly");
		} else
			return null;	
	}
	
	/** Puts a subclass into the pool, freeing it from use
	 * @param c the Subclass to free */
	public <T extends C> void freeSubclass(T c) {
		@SuppressWarnings("unchecked")
		Pool<T> pool = (Pool<T>) poolMap.get(getSubclassId((Class<? extends C>)c.getClass()));
		
		if(pool != null)
			pool.free(c);
		else
			throw new RuntimeException("Class' " + c.getClass().getName() + " pool wasn't registered properly");
			
	}
}
