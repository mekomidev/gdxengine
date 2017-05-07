package com.kerberjg.gdxstudio.tests.entities;

import com.mekomidev.gdxengine.core.ecs.Component;
import com.mekomidev.gdxengine.core.ecs.EntitySystem;

public class TestSystem extends EntitySystem {
	int base;
	long x;
	boolean dirty;

	@Override
	public void init() {
		base = (int) (Math.random() * 1000D);
		dirty = false;
	}
	
	public void update(float delta) {
		x += delta * base;
		
		dirty = true;
	}

	@Override
	public void render() {
		System.out.print("Rendered frame");
	}

	@Override
	public void dispose() {
		base = 0;
	}
}
