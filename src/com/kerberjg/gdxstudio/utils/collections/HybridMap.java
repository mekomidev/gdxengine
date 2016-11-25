package com.kerberjg.gdxstudio.utils.collections;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.utils.ObjectIntMap;

/** Maps the value both via a generic K and a integer ID */
public class HybridMap<K,V> implements Map<K,V> {
	private FastIntMap<V> intSubmap = new FastIntMap<V>();
	private ObjectIntMap<K> keySubmap = new ObjectIntMap<K>();

	//TODO: add a capacity and copy constructor to this and to all underlying classes
	
	@Override
	public int size() {
		return intSubmap.size();
	}

	@Override
	public boolean isEmpty() {
		return intSubmap.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return keySubmap.containsKey((K) key);
	}
	
	public boolean containsId(int id) {
		return intSubmap.get(id) == null;
	}

	@Override
	public boolean containsValue(Object value) {
		V val = (V) value;
		for(V item : intSubmap.items)
			return (item == null ? val == null : item.equals(val));
		
		return false;
	}

	@Override
	public V get(Object key) {
		int id = keySubmap.get((K) key, -1);
		return (id >= 0 ? intSubmap.get(id) : null);
	}
	
	public int getId(Object key) {
		return keySubmap.get((K) key, -1);
	}
	
	public V get(int id) {
		return intSubmap.get(id);
	}

	@Override
	public V put(K key, V value) {
		// Retrieves the previous present value
		V prev = get(key);
		
		keySubmap.put(key, intSubmap.add(value));
		
		return prev;
	}

	@Override
	public V remove(Object key) {
		int id = keySubmap.remove((K) key, -1);
		return (id >= 0 ? intSubmap.remove(id) : null);
	}
	
	public V remove(int id) {
		return intSubmap.remove(id);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		for(Entry<? extends K, ? extends V> e : m.entrySet())
			this.put(e.getKey(), e.getValue());
	}

	@Override
	public void clear() {
		keySubmap.clear();
		intSubmap.clear();
	}

	@Override
	public Set<K> keySet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<V> values() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		// TODO Auto-generated method stub
		return null;
	}

}
