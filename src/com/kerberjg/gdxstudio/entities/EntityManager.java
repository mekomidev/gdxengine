package com.kerberjg.gdxstudio.entities;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.kerberjg.gdxstudio.utils.collections.FastIntMap;
import com.kerberjg.gdxstudio.utils.collections.HybridMap;

// TODO: write tests for the whole ECS framework
/** 
 * Keeps Components in parallel arrays, sorted by their Entity ID
 * 
 * @author kerberjg */
public class EntityManager implements Disposable {
	/** A map of all entities that allows them to be reached both by their ID and their String name */
	private HybridMap<String, Entity> entities = new HybridMap<String, Entity>();
	/** A table holding all components, with Component types as rows and Entity IDs as columns */
	private FastIntMap<FastIntMap<Component>> components = new FastIntMap<FastIntMap<Component>>();
	
	/** A map holding all the EntitySystem instances*/
	private ObjectMap<Class<? extends EntitySystem>, EntitySystem> systems = new ObjectMap<>();
	
	public EntityManager() {
		// TODO: do something useful here 
	}
	
	@Override
	/** Disposes of all entities, components and systems */
	public void dispose() {
		
	}
	
	//TODO: document everything properly
	
	/*
	 * Entity management
	 */
	
	/** Adds an Entity to the manager
	 * 
	 * @param name the String name that the Entity can be accessed with
	 * @param entity the Entity to be added
	 * 
	 * @return the ID of the new Entity */
	protected int addEntity(String name, Entity entity) {
		return entities.put(name, entity);
	}
	
	/** @return whether an Entity with a specific name is present */
	public boolean hasEntity(String name) {
		return entities.containsKey(name);
	}
	
	/** @return whether an Entity with a specific ID is present */
	public boolean hasEntity(int id) {
		return entities.containsId(id);
	}
	
	/** @return the number of Entities currently present in the manager */
	public int entityCount() {
		return entities.size();
	}
	
	/** @return an Entity referred to by its name */
	public Entity getEntity(String name) {
		return entities.get(name);
	}
	
	/** @return an Entity referred to by its ID */
	public Entity getEntity(int id) {
		return entities.get(id);
	}
	
	/** Finds all the Entity instances containing the requested Components
	 * 
	 * @param componentIds an array of Component IDs to find in Entities
	 * @return an Iterable collection containing Entities that contain all the Components */
	public Iterable<Entity> findEntities(int[] componentIds) {
		Array<Entity> i = new Array<>(entities.size());
		
		for(Entity e : entities) {
			int c = 0;
			
			// Counts the matching Components
			for(int cid : componentIds)
				if(components.get(cid).containsKey(e.id))
					++c;
			
			// Add to iterable if all Components are found
			if(c == componentIds.length)
				i.add(e);
		}
		
		return i;
	}
	
	/** Removes an Entity, searching it by its name
	 * @return the removed Entity */
	public Entity removeEntity(String name) {
		return entities.remove(name);
	}
	
	/** Removes an Entity, searching it by its ID
	 * @return the removed Entity */
	public Entity removeEntity(int id) {
		return entities.remove(id);
	}
	
	public void clearEntities() {
		// TODO: call dispose() on everything
		entities.clear();
		components.clear();
	}
	
	/*
	 * Component management
	 */
	
	/** Adds a Component to a specific Entity */
	protected void addComponent(int entityId, Component component) {
		if(entities.containsId(entityId)) {
			getComponentMap(component.typeId).put(component);
		} else
			throw new RuntimeException("Entity with ID " + entityId + " is not present");
	}
	
	/** @returns the Component of a specific type that belongs to a specific entity */
	protected <C extends Component> C getComponent(int entityId, int componentType) {
		@SuppressWarnings("unchecked")
		Class<C> ct = (Class<C>) Components.getComponentClass(componentType);
		return ct.cast(getComponentMap(componentType).get(entityId));
	}
	
	/** Removes a Component from an Entity, with both referred to by their IDs
	 * 
	 * @return the removed Component if present, otherwise null*/
	protected Component removeComponent(int entityId, int componentType) {
		return getComponentMap(componentType).remove(entityId);
	}
	
	private <C extends Component> FastIntMap<Component> getComponentMap(int componentType) {	
		// Checks if the Component is registered
		if(Components.hasComponent(componentType)) {
			// Checks if the Component is in the EntityManager's map
			if(components.containsKey(componentType)) {
				FastIntMap<Component> cm = components.get(componentType);
				return cm;
			} else {
				FastIntMap<Component> componentMap = new FastIntMap<Component>(entities.size());
				components.put(componentMap);
				return componentMap;
			}
		} else
			throw new RuntimeException("Component type with ID " + componentType + "has not been registered");
	}
	
	/*
	 * System management
	 */
	
	/** Adds an EntitySystem to the manager
	 * @return an EntitySystem of the same type if previously present */
	@SuppressWarnings("unchecked")
	protected <S extends EntitySystem> S addSystem(S system) {
		return (S) systems.put(system.getClass(), system);
	}
	
	/** @return the EntitySystem identified by its class*/
	protected <S extends EntitySystem> S getSystem(Class<S> systemType) {
		return systemType.cast(systems.get(systemType));
	}
	
	/** Removes an EntitySystem from the manager, calling its cleanup() and dispose() methods
	 * 
	 * @return whether the system was actually present and removed */
	protected <S extends EntitySystem> boolean removeSystem(Class<S> systemType) {
		EntitySystem es = systems.remove(systemType);
		
		if(es != null) {
			es.cleanup();
			es.dispose();
			return true;
		} else
			return false;
	}
	
}
