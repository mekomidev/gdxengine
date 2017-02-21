package com.mekomidev.gdxengine.core.utils.collections;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Stream;

import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.reflect.ArrayReflection;

/** A speed-optimized map which uses integers as keys. Always preserves the index and never moves the values around.
 * Uses an underlying raw array of items for storage and a bit-set for faster lookup.
 * 
 * @author kerberjg
 */
// TODO: Evaluate the perfomance advantage between FastIntMap and Vector
public class FastIntMap<V> implements Iterable<V> {
	public V[] items;
	public Bits bits;
	
	private int size;
	public float growthFactor = 1.5f;
	
	/** Creates a new {@link FastIntMap} with a initial capacity of 16 */
	public FastIntMap() {
		this(16);
	}
	
	@SuppressWarnings("unchecked")
	/** Creates a new {@link FastIntMap} with a specified initial capacity */
	public FastIntMap(int capacity) {
		items = (V[]) ArrayReflection.newInstance(Object.class, capacity);
		bits = new Bits(capacity);
	}

	public int size() {
		return size;
	}
	
	/** Grows the map to accomodate a specified maximum amount of items.
	 * If the current map size is equal or larger than required, no action is performed.
	 * 
	 * @param capacity the size to which the map has to be grown */
	@SuppressWarnings("unchecked")
	public void grow(int capacity) {
		// Does nothing if the map is already big enough
		if(capacity < items.length) return;
		
		V[] newItems = (V[])ArrayReflection.newInstance(items.getClass().getComponentType(), capacity);
		
		// Avoids copy if possible
		if(size == 1) {
			int i = bits.nextSetBit(0);
			newItems[i] = items[i];
		} else if(size > 0)
			System.arraycopy(items, 0, newItems, 0, items.length); //TODO: implement Bits#lastSetBit and use that as the number of items to copy
		
		
		this.items = newItems;
	}

	/** @return whether the map is empty */
	public boolean isEmpty() {
		return size == 0;
	}

	/**	Puts the element at the first free position
	 * 
	 * @param e the element to be inserted
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
	 * 
	 * @param i the key
	 * @param e the element to be inserted
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
		if(!bits.getAndSet(i)) ++size;
		
		items[i] = e;
		return prev;
	}
	
	/** @param i the index of the element to return
	 * @return the requested element if present, otherwise null */
	public V get(int i) {
		return items[i];
	}
	
	/** @return whetehr the map contains a specified integer key */
	public boolean containsKey(int key) {
		return bits.get(key);
	}

	/** Removes the element at the specified index */
	public V remove(int index) {
		V value = items[index];
		bits.clear(index);
		items[index] = null;
		return value;
	}

	/** Adds all elements of an iterable collection to this map */
	public boolean addAll(Iterable<? extends V> c) {
		int sizeBefore = this.size;
		
		for(V e : c)
			this.put(e);
		
		return (this.size - sizeBefore > 0);
	}

	/** Clears the map */
	public void clear() {
		bits.clear();
		for(int i = 0; i < items.length; i++)
			items[i] = null;
	}
	
	/** @return a copy of the underlying array */
	public V[] toArray() {
		@SuppressWarnings("unchecked")
		V[] newItems = (V[])ArrayReflection.newInstance(items.getClass().getComponentType(), items.length);
		System.arraycopy(items, 0, newItems, 0, items.length);
		return newItems;
	}

	public Stream<V> stream() {
		return Arrays.asList(items).stream();
	}
	
	public Stream<V> parallelStream() {
		return Arrays.asList(items).parallelStream();
	}
	
	@Override
	public Iterator<V> iterator() {
		return new FastIntMapIterator<V>();
	}
	
	/** This Iterator class uses the bitset to iterate faster over the underlying array of items
	 * 
	 * @author kerberjg
	 * */
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
