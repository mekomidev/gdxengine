package com.kerberjg.gdxstudio.utils.collections;

import java.util.Iterator;

import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.reflect.ArrayReflection;

/** A speed-optimized map which uses integers as keys. Always preserves the index and never moves the values around
 * 
 * @author kerberjg
 */
public class FastIntMap<V> implements Iterable<V> {
	public V[] items;
	public Bits bits;
	
	private int size;
	public float growthFactor = 1.5f;
	
	public FastIntMap() {
		this(16);
	}
	
	@SuppressWarnings("unchecked")
	public FastIntMap(int capacity) {
		items = (V[]) ArrayReflection.newInstance(Object.class, capacity);
		bits = new Bits(capacity);
	}

	public int size() {
		return size;
	}
	
	@SuppressWarnings("unchecked")
	public void grow(int capacity) {
		V[] newItems = (V[])ArrayReflection.newInstance(items.getClass().getComponentType(), capacity);
		System.arraycopy(items, 0, newItems, 0, Math.min(size, newItems.length));
		this.items = newItems;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	/**	Puts the element at the first free position
	 * @return the element ID */
	public int put(V e) {
		if(this.size == items.length) {
			this.grow((int) (size * growthFactor));
			bits.set(items.length - 1);
			bits.clear(items.length - 1);
		}
		
		int i = bits.nextClearBit(0);
		bits.set(i);
		items[i] = e;
		++size;
		return i;
	}
	
	/** Puts the element at a specified position
	 * @return previously stored object if any, otherwise null */
	public V put(int i, V e) {
		V prev;
		
		// Checks if growth is needed
		if(this.size - 1 < i) {
			grow(i + 1);
			prev = null;
		} else 
			prev = get(i);
		
		// Increment size only if no previous element was there
		if(bits.getAndSet(i)) ++size;
		
		items[i] = e;
		return prev;
	}
	
	/**
	 * @param i the index of the element to return
	 * @return the requested element if present, otherwise null */
	public V get(int i) {
		return items[i];
	}
	
	public boolean containsKey(int key) {
		return bits.get(key);
	}

	public V remove(int index) {
		V value = items[index];
		bits.clear(index);
		items[index] = null;
		return value;
	}

	public boolean addAll(Iterable<? extends V> c) {
		int sizeBefore = this.size;
		
		for(V e : c)
			this.put(e);
		
		return (this.size - sizeBefore > 0);
	}

	public void clear() {
		bits.clear();
		for(int i = 0; i < items.length; i++)
			items[i] = null;
	}

	@Override
	public Iterator<V> iterator() {
		return new FastIntMapIterator<V>();
	}
	
	private class FastIntMapIterator<T> implements Iterator<V> {
		private int i = 0;

		@Override
		public boolean hasNext() {
			return bits.nextSetBit(i) != -1;
		}

		@Override
		public V next() {
			int next = bits.nextSetBit(i++);
			
			if(next >= 0)
				return items[next];
			else
				return null;
		}
		
	}

}
