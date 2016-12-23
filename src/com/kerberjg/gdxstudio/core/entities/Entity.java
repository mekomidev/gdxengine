package com.kerberjg.gdxstudio.core.entities;


public abstract class Entity implements PrimitiveEntity {
	private final EntityManager manager;
	public final int id;
	public final String name;
	
	public Entity(EntityManager manager, String name) {
		this.manager = manager;
		this.name = name;
		this.id = manager.addEntity(name, this);
	}
	
	public abstract void event(String eventName, Object... blob);
	
	/*
	 * 	Component management
	 */

	/**
	 * @param componentType the class of the Component to add to the Entity
	 * @return the added Component instance */
	public <C extends Component> C addComponent(Class<C> componentType) {
		C component = Components.getComponentInstance(componentType);
		manager.addComponent(id, component);
		return componentType.cast(component);
	}
	
	/**
	 * @param componentId the type ID of the Component to return
	 * @return the requested Component */
	public <C extends Component> C getComponent(Class<C> componentType) {
		int componentId = Components.getComponentId(componentType);
		
		return componentType.cast(manager.getComponent(id, componentId));
	}
	
	/**
	 * @param componentType the class of the Component to remove
	 * @return whether the Component was removed*/
	public <C extends Component> boolean removeComponent(Class<C> componentType) {
		int componentId = Components.getComponentId(componentType);
		return manager.removeComponent(id, componentId);
	}
	
	/**
	 * @param componentId the type ID of the Component to remove
	 * @return whether the Component was removed*/
	public <C extends Component> boolean removeComponent(int componentId) {
		return manager.removeComponent(id, componentId);
	}
}
