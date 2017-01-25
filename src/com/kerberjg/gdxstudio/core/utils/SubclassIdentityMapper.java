package com.kerberjg.gdxstudio.core.utils;

import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ReflectionPool;
import com.kerberjg.gdxstudio.core.utils.collections.FastIntMap;

//TODO: this class is a minefield of NullPointerExceptions; eradicate those abominations!
public class SubclassIdentityMapper<C> {
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
	
	/**	Registers the Subclass in the manager and returns its new ID, or, if already registered, returns its preexisting ID
	 * @return Subclass type ID
	 * @param ct Subclass class to register */
	public <T extends C> int registerSubclass(Class<T> ct) {
		// Registers the component
		final int id = subclassMap.put(ct);
		inverseSubclassMap.put(ct, id);
		
		// Creates and registers a Pool for the component type
		poolMap.put(id, (Pool<T>) new ReflectionPool<T>(ct));
		
		return id;	
	}
	
	/** @return whether the Subclass with the specified ID is registered
	 * @param id the ID of the Subclass to check */
	public boolean hasSubclass(int id) {
		return subclassMap.containsKey(id);
	}
	
	/** @return a Subclass's class by its ID 
	 * @param id the ID of a Subclass */
	public Class<? extends C> getSubclassClass(int id) {
		return subclassMap.get(id);
	}
	
	/** @return a Subclass's ID by it's class
	 * @param componentType a Subclass's class */
	public int getSubclassId(Class<? extends C> componentType) {
		return inverseSubclassMap.get(componentType, -1);
	}
	
	/** @return a new instance of Subclass with class ct
	 * @param componentType a Subclass's class */
	public <T extends C> T getSubclassInstance(Class<T> componentType) {
		final int id = inverseSubclassMap.get(componentType, -1);
		return getSubclassInstance(id);
	}
	
	/** @return a new instance of Subclass with ID id
	 * @param id the ID of a Subclass */
	public <T extends C> T getSubclassInstance(int id) {
		@SuppressWarnings("unchecked")
		Class<T> ct = (Class<T>) subclassMap.get(id);
		
		if(id >= 0)
			return ct.cast(poolMap.get(id).obtain());
		else
			return null;	
	}
	
	@SuppressWarnings("unchecked")
	/** Puts a Subclass into the pool, freeing it from use
	 * @param c the Subclass to free */
	public <T extends C> void freeSubclass(T c) {
		((Pool<T>)poolMap.get(getSubclassId((Class<? extends C>)c.getClass())) ).free(c);
	}
}
