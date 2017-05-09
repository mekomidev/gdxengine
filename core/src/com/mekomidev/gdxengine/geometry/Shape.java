package com.mekomidev.gdxengine.geometry;

import com.badlogic.gdx.math.collision.*;

public abstract class Shape {
	public abstract BoundingBox getBoundingBox();
	public abstract Sphere getBoundingSphere();
}
