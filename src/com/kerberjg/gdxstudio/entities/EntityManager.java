package com.kerberjg.gdxstudio.entities;

import com.kerberjg.gdxstudio.utils.collections.HybridMap;

public class EntityManager {
	private HybridMap<String, Entity> entities = new HybridMap<String, Entity>();
	
	public int addEntity(String name, Entity entity) {
		entities.put(name, entity);
		return entities.getId(name);
	}
	
	public boolean hasEntity(String name) {
		return entities.containsKey(name);
	}
	
	public boolean hasEntity(int id) {
		return entities.get(id) != null;
	}
	
	public int size() {
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
	}
}
