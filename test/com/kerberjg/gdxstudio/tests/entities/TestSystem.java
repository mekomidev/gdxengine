package com.kerberjg.gdxstudio.tests.entities;

import com.kerberjg.gdxstudio.core.entities.EntitySystem;

public class TestSystem extends EntitySystem {
	int base;
	long x;
	boolean dirty;

	@Override
	public void init() {
		base = (int) (Math.random() * 1000D);
		dirty = false;
	}
	
	@Override
	public void update(float delta) {
		x += delta * base;
		
		dirty = true;
	}

	@Override
	public void render() {
		System.out.print("Rendered frame");
	}

	@Override
	public void cleanup() {
		dirty = false;
	}

	@Override
	public void dispose() {
		base = 0;
	}
}
