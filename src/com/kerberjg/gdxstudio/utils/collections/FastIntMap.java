package com.kerberjg.gdxstudio.utils.collections;

import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.reflect.ArrayReflection;

/** A speed-optimized map which uses integers as keys. Always preserves the index and never moves the values around */
public class FastIntMap<V> {
	public V[] items;
	public Bits bits;
	
	private int size;
	public float growthFactor = 1.5f;
	
	public FastIntMap() {
		this(16);
	}
	
	@SuppressWarnings("unchecked")
	public FastIntMap(int capacity) {
		items = (V[])new Object[capacity];
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
		return bits.isEmpty();
	}

	public int add(V e) {
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
	
	public V get(int i) {
		return items[i];
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
			this.add(e);
		
		return (this.size - sizeBefore > 0);
	}

	public void clear() {
		bits.clear();
		for(int i = 0; i < items.length; i++)
			items[i] = null;
	}

}
