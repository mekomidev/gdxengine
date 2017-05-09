package com.mekomidev.gdxengine.utils.collections;

import java.util.Iterator;
import java.util.stream.Stream;

import com.badlogic.gdx.utils.ObjectIntMap;

/** Maps the value both via a generic K and a integer ID */
public class HybridMap<K,V> implements Iterable<V> {
	private FastIntMap<V> intSubmap;
	private ObjectIntMap<K> keySubmap;

	public HybridMap() {
		intSubmap = new FastIntMap<V>();
		keySubmap = new ObjectIntMap<K>();
	}
	
	public HybridMap(int capacity) {
		intSubmap = new FastIntMap<V>(capacity);
		keySubmap = new ObjectIntMap<K>(capacity);
	}
	
	public int size() {
		return intSubmap.size();
	}

	public boolean isEmpty() {
		return intSubmap.isEmpty();
	}

	public boolean containsKey(K key) {
		return keySubmap.containsKey(key);
	}
	
	public boolean containsId(int id) {
		return intSubmap.get(id) != null;
	}

	public boolean containsValue(V value) {
		V val = value;
		for(V item : intSubmap.items)
			return (item == null ? val == null : item.equals(val));
		
		return false;
	}

	public int getId(K key) {
		return keySubmap.get(key, -1);
	}
	
	public V get(K key) {
		int id = keySubmap.get(key, -1);
		return (id >= 0 ? intSubmap.get(id) : null);
	}
	
	public V get(int id) {
		return intSubmap.get(id);
	}

	public int put(K key, V value) {
		int id = intSubmap.put(value);
		keySubmap.put(key, id);
		return id;
	}
	
	public void put(int id, K key, V value) {
		intSubmap.put(id, value);
		keySubmap.put(key, id);
	}

	public V remove(K key) {
		int id = keySubmap.remove(key, -1);
		return (id >= 0 ? intSubmap.remove(id) : null);
	}
	
	public V remove(int id) {
		return intSubmap.remove(id);
	}

	public void clear() {
		keySubmap.clear();
		intSubmap.clear();
	}
	
	public Stream<V> stream() {
		return intSubmap.stream();
	}
	
	public Stream<V> parallelStream() {
		return intSubmap.parallelStream();
	}

	@Override
	public Iterator<V> iterator() {
		return intSubmap.iterator();
	}

	//TODO: Implement .Entry, .Values, .Keys
	/*
	public void putAll(Map<? extends K, ? extends V> m) {
		for(Entry<? extends K, ? extends V> e : m.entrySet())
			this.put(e.getKey(), e.getValue());
	}

	public void clear() {
		keySubmap.clear();
		intSubmap.clear();
	}

	public Set<K> keySet() {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<V> values() {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<java.util.Map.Entry<K, V>> entrySet() {
		// TODO Auto-generated method stub
		return null;
	}*/

}
