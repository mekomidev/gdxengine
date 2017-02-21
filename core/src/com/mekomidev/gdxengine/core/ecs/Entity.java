package com.mekomidev.gdxengine.core.ecs;

/**	An abstract Entity.
 * All of the ECS functionalities are implemented, what's left to the user is just the general behavior
 * 
 * @author kerberjg*/
public abstract class Entity implements PrimitiveEntity {
	private final EntityManager manager;
	public final int id;
	public final String name;
	
	/** Constructs an Entity and registers it within an indicated EntityManager
	 * 
	 * @param manager the EntityManager to register the Entity in
	 * @param name the name to register this Entity with; has to be unique */
	// TODO: this API is a mess (or is it...?)
	public Entity(EntityManager manager, String name) {
		this.manager = manager;
		this.name = name;
		this.id = manager.addEntity(name, this);
	}
	
	/** Triggers an event on this object
	 * 
	 * @param eventName self-explanatory
	 * @param blob optional parameters to pass as message(s) for this event */
	public abstract void triggerEvent(String eventName, Object... blob);
	
	/*
	 * 	Component management
	 */

	/** @return the added Component instance
	 * @param componentType the class of the Component to add to the Entity */
	public <C extends Component> C addComponent(Class<C> componentType) {
		//TODO: this should be done inside the EntityManager
		C component = Component.map.getSubclassInstance(componentType);
		manager.addComponent(id, component);
		return componentType.cast(component);
	}
	
	/** @return the requested Component
	 * @param componentId the type ID of the Component to return */
	public <C extends Component> C getComponent(Class<C> componentType) {
		int componentId = Component.map.getSubclassId(componentType);
		
		return componentType.cast(manager.getComponent(id, componentId));
	}
	
	/** @return whether the Component was removed
	 * @param componentType the class of the Component to remove */
	public <C extends Component> boolean removeComponent(Class<C> componentType) {
		int componentId = Component.map.getSubclassId(componentType);
		return manager.removeComponent(id, componentId);
	}
	
	/** @return whether the Component was removed
	 * @param componentId the type ID of the Component to remove */
	public <C extends Component> boolean removeComponent(int componentId) {
		return manager.removeComponent(id, componentId);
	}
}
