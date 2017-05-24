package com.mekomidev.gdxengine.ecs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.mekomidev.gdxengine.Game;
import com.mekomidev.gdxengine.ecs.EntitySystem;
import com.mekomidev.gdxengine.utils.collections.FastIntMap;
import com.mekomidev.gdxengine.utils.collections.HybridMap;

// TODO: write tests for the whole ECS framework
// TODO: consider separating the ECS from the game engine
// TODO: rename EntityManager to something without Entity in the name
/** Keeps Component.map in parallel arrays, sorted by their Entity ID
 * 
 * @author kerberjg */
public class EntityManager implements Disposable {
	// Data structures
	/** A map of all entities that allows them to be reached both by their ID and their String name */
	protected HybridMap<String, Entity> entities;
	/** A table holding all components, with Component types as rows and Entity IDs as columns */
	private FastIntMap<FastIntMap<? extends Component>> components;
	/** A map holding all the EntitySystem instances*/
	protected FastIntMap<EntitySystem> systems;
	private FastIntMap<Callable<Object>> systemTasks;
	
	// Caching
	/*
	private boolean enableCaching;
	private Bits changedComponents;
	private FastIntMap<int[]> cachedQueries;
	*/
	
	public EntityManager() {
		this(64);
	}
	
	public EntityManager(int entityCapacity) {
		entities = new HybridMap<>(entityCapacity);
		components = new FastIntMap<>();
		
		systems = new FastIntMap<>();
		systemTasks = new FastIntMap<>();
	}
	
	public EntityManager(int entityCapacity, int componentCapacity, int systemCapacity) {
		entities = new HybridMap<>(entityCapacity);
		components = new FastIntMap<>(componentCapacity);
		
		systems = new FastIntMap<>(systemCapacity);
		systemTasks = new FastIntMap<>(systemCapacity);
	}
	
	/*
	 * Execution
	 */
	private ExecutorService executor = Executors.newWorkStealingPool();
	private ArrayList<Callable<Object>> parallelTasks = new ArrayList<>();
	
	public void init() {
		
		for(Entity e : entities)
			e.create();
	}
	
	/** Updates all the entities, components and systems
	 * @param delta the time span between the beginning and the end of the last frame
	 */
	public void update(float delta) {
		// Systems
		// processed first
		try {
			parallelTasks.clear();
			parallelTasks.ensureCapacity(systems.size());
			
			for(Callable<Object> task : systemTasks)
				parallelTasks.add(task);
			
			// Executes all the tasks and waits for their completion
			executor.invokeAll(parallelTasks);
		} catch (InterruptedException e) {
			System.err.println("EntitySystem updating thread(s) was/were interrupted");
			e.printStackTrace();
		}
		
		// Entities
		// processed last
		for(Entity e : entities)
			e.update(delta);
	}
	
	/** Renders graphics to screen */
	public void render() {
		// Systems
		// render from component data, and potentially from queued commands
		for(EntitySystem es : systems)
			es.render();
		
		// Entities
		// processed first, due to the potential effects on systems' work
		for(Entity e : entities)
			e.render();	
	}
	
	@Override
	/** Disposes of all entities, components and systems */
	public void dispose() {
		for(Entity e : entities) {
			e.dispose();
			
			for(FastIntMap<? extends Component> cs : components) {
				Component c = cs.remove(e.id);
				
				if(c != null)
					c.dispose();
			}
		}
			
		for(EntitySystem es : systems)
			removeSystem(es.systemId);
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
	 * @param name the name of the Entity to remove
	 * @return whether the Entity was removed */
	public boolean removeEntity(String name) {	
		return removeEntity(entities.getId(name));
	}
	
	/** Removes an Entity, searching it by its ID
	 * @param id the ID of the Entity to remove
	 * @return whether the Entity was removed */
	public boolean removeEntity(int id) {
		Entity e = entities.remove(id);
		
		if(e != null) {
			// Dispose of the removed Entity
			e.dispose();
			
			// Removes all of entity's components
			for(FastIntMap<? extends Component> cm : components)
				cm.remove(e.id);
			
			return true;
		} else
			return false;
	}
	
	/** Removes all Entities and their Components. EntitySystems are left intact.
	 * This operation isn't immediate; it is postponed to the end of the current update, if such is currently in motion */
	public void clearEntities() {
		// TODO: call dispose() on everything
		entities.clear();
		components.clear();
	}
	
	/*
	 * Event system
	 */
	
	public void triggerEvent(String event, Object... blob) {
		for(Entity e : entities)
			e.triggerEvent(event, blob);
	}
	
	/*
	 * Component management
	 */
	
	/** Adds a Component to a specific Entity */
	protected void addComponent(int entityId, Component component) {
		if(component == null)
			throw new RuntimeException("Can't add a null Component");
		else if(entities.containsId(entityId)) {
			getComponentMap(component.typeId).put(entityId, component);
			//changedComponents.set(component.typeId);
		} else
			throw new RuntimeException("Entity with ID " + entityId + " is not present");
	}
	
	protected boolean hasComponent(int entityId, Class<? extends Component> componentType) {
		return hasComponent(entityId, Component.map.getSubclassId(componentType));
	}
	
	protected boolean hasComponent(int entityId, int componentType) {
		return components.get(componentType).containsKey(entityId);
	}
	
	/** @returns the Component of a specific type that belongs to a specific entity */
	public <C extends Component> C getComponent(int entityId, int componentType) {
		@SuppressWarnings("unchecked")
		Class<C> ct = (Class<C>) Component.map.getSubclassClass(componentType);
		return ct.cast(getComponentMap(componentType).get(entityId));
	}
	
	/** Removes a Component from an Entity, with both referred to by their IDs
	 * @return whether the component was removed */
	protected boolean removeComponent(int entityId, int componentType) {
		Component c = getComponentMap(componentType).remove(entityId);
		
		/*
		if(c != null) {
			changedComponents.set(componentType);
			c.dispose();
			return true;
		} else
			return false;
		*/
		
		return (c != null);
	}
	
	@SuppressWarnings("unchecked")
	private <C extends Component> FastIntMap<C> getComponentMap(int componentType) {	
		// Checks if the Component is registered
		if(Component.map.hasSubclass(componentType)) {
			// Checks if the Component is in the EntityManager's map...
			if(components.containsKey(componentType)) {
				FastIntMap<? extends Component> componentMap = components.get(componentType);
				return (FastIntMap<C>) componentMap;
			// ...and creates a new Component map otherwise
			} else {
				FastIntMap<? extends Component> componentMap = new FastIntMap<Component>(entities.size());
				components.put(componentType, componentMap);
				return (FastIntMap<C>) componentMap;
			}
		} else
			throw new RuntimeException("Component type with ID " + componentType + "has not been registered");
	}
	
	public List<? extends Component> getComponents(int componentType) {
		// Do NOT try to use arrays here... are you listening?
		// You will lose hours over goddamn type erasure because the component maps rely on generic arrays
		// Even though your IDE will tell you "it's all fine, kiddo, assign .items to a Component array... because it technically is such!
		// But noooo, it turns out that this cute Component array was an Object array ALL ALONG
		// So please... for the sake of your own sanity... please don't do this mistake again... I beg you..
		//		Regards, your future self
		List<? extends Component> list = Collections.unmodifiableList(Arrays.asList(getComponentMap(componentType).items));
		return list;
	}
	
	/*
	 * System management
	 */
	
	/** Adds an EntitySystem to the manager
	 * @return an EntitySystem of the same type if previously present */
	public <S extends EntitySystem> void addSystem(S system) {
		EntitySystem prev = systems.put(system.systemId, system);
		
		if(prev != null)
			prev.dispose();
		
		system.init();
		
		systemTasks.put(system.systemId, new Callable<Object>() {
			@Override
			public Object call() {
				try {
					system.update(Game.getDelta());
				} catch(Exception e) {
					e.printStackTrace();
				}
				
				return null;
			}
		});
	}
	
	/** @return the EntitySystem identified by its class*/
	@SuppressWarnings("unchecked")
	public <S extends EntitySystem> S getSystem(int systemType) {
		Class<S> systemClass = (Class<S>) EntitySystem.map.getSubclassClass(systemType);
		return systemClass.cast(systems.get(systemType));
	}
	
	/** Removes an EntitySystem from the manager, calling its cleanup() and dispose() methods
	 * 
	 * @return whether the system was actually present and removed */
	public <S extends EntitySystem> boolean removeSystem(int systemType) {
		EntitySystem es = systems.remove(systemType);
		
		if(es != null) {
			es.dispose();
			return true;
		} else
			return false;
	}
	
}
