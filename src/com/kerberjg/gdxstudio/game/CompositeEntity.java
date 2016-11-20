package com.kerberjg.gdxstudio.game;

import java.lang.reflect.Constructor;

import com.badlogic.gdx.utils.ObjectMap;

/** A compsite Entity class. Allows to extend the entity by attaching various Components.
 * 
 *  @author kerberjg */
public class CompositeEntity implements Entity {
	private final ObjectMap<Class<? extends Component>, Component> components = new ObjectMap<>();

	@Override
	public final void create() {
		for(Component c : components.values())
			c.create();
	}

	@Override
	public final void draw() {
		for(Component c : components.values())
			c.draw();
	}

	@Override
	public final void update(float delta) {
		for(Component c : components.values())
			c.update(delta);
	}

	@Override
	public final void dispose() {
		for(Component c : components.values())
			c.dispose();
	}
	
	/** Adds a Component to this entity. A CompositeEntity can have only one Component of its kind.
	 * 
	 * @author kerberjg */
	public final void addComponent(Class<? extends Component> t) {
		try {
			Constructor<? extends Component> tc = t.getConstructor(CompositeEntity.class);
			Component c = (Component) tc.newInstance(this);
			
			components.put(t, c);
		}
		// This is NEVER supposed to be thrown!
		catch(Exception e) { e.printStackTrace(); }
	}
	
	/** Returns a Component if included, returns null otherwise
	 * 
	 * @author kerberjg */
	public final Component getComponent(Class<? extends Component> t) {
		return components.get(t);
	}
	
	/** Removes a Component if included. 
	 * 
	 * @return whether the Component was removed
	 * @author kerberjg */
	public final boolean removeComponent(Class<? extends Component> t) {
		return components.remove(t) != null;
	}
	
	/** A base Component class for CompositeEntity
	 * 
	 * @author kerberjg */
	//TODO: Write some Components!
	public static abstract class Component implements Entity {
		public final CompositeEntity parent;
		
		protected Component(CompositeEntity parent) {
			this.parent = parent;
		}
	}
}
