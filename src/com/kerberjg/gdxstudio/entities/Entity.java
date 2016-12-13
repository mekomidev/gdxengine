package com.kerberjg.gdxstudio.entities;

import com.badlogic.gdx.utils.Disposable;

public class Entity implements Disposable {
	private final EntityManager manager;
	public final int id;
	public final String name;
	
	public Entity(EntityManager manager, String name) {
		this.manager = manager;
		this.name = name;
		this.id = manager.addEntity(name, this);
	}

	@Override
	public void dispose() {
	}
	
	
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
	 * @param componentId the type ID of the Component to remove
	 * @return whether the Component was removed*/
	public <C extends Component> boolean removeComponent(int componentId) {
		return manager.removeComponent(id, componentId) != null;
	}
}
