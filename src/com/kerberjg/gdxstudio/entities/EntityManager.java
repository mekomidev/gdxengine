package com.kerberjg.gdxstudio.entities;

import com.kerberjg.gdxstudio.utils.collections.FastIntMap;
import com.kerberjg.gdxstudio.utils.collections.HybridMap;

// TODO: write tests for the whole ECS framework
/** 
 * Keeps Components in parallel arrays, sorted by their Entity ID
 * 
 * @author kerberjg */
public class EntityManager {
	/** A map of all entities that allows them to be reached both by their ID and their String name */
	private HybridMap<String, Entity> entities = new HybridMap<String, Entity>();
	/** A table holding all components, with Component types as rows and Entity IDs as columns */
	private FastIntMap<FastIntMap<Component>> components = new FastIntMap<FastIntMap<Component>>();
	
	// TODO: missing EntitySystem management
	
	public EntityManager() {
		// TODO: do something useful here 
	}
	
	//TODO: document everything properly
	
	public int addEntity(String name, Entity entity) {
		return entities.put(name, entity);
	}
	
	public boolean hasEntity(String name) {
		return entities.containsKey(name);
	}
	
	public boolean hasEntity(int id) {
		return entities.containsId(id);
	}
	
	public int entityCount() {
		return entities.size();
	}
	
	public Entity getEntity(String name) {
		return entities.get(name);
	}
	
	public Entity getEntity(int id) {
		return entities.get(id);
	}
	
	public Entity removeEntity(String name) {
		return entities.remove(name);
	}
	
	public Entity removeEntity(int id) {
		return entities.remove(id);
	}
	
	public void clearEntities() {
		entities.clear();
		components.clear();
	}
	
	protected void addComponent(int entityId, Component component) {
		if(entities.containsId(entityId)) {
			getComponentMap(component.typeId).put(component);
		} else
			throw new RuntimeException("Entity with ID " + entityId + " is not present");
	}
	
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
}
