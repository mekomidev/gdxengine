package com.mekomidev.gdxengine.collision;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pools;
import com.mekomidev.gdxengine.geometry.Shape;

//TODO: optimize/parallelize

/** A straightforward Axis-Aligned Bounding Box collision resolver 
 * Uses the Separating Axis Theorem to detect collisions and to calculate the Minimum Translation Vectors
 * 
 * @author kerberjg */
public class AABBCollisionResolver implements CollisionResolver {

	@Override
	public List<Collision> checkCollisions(Iterable<Shape> shapes) {
		ArrayList<Collision> events = new ArrayList<>();
		
		for(Shape a : shapes) {
			if(!a.getBoundingBox().isValid()) continue;
			
			for(Shape b : shapes) {
				if(!b.getBoundingBox().isValid()) continue;
				
				if(a.getBoundingBox().intersects(b.getBoundingBox()))
					events.add(new Collision(a, b));
			}
		}
		
		return events;
	}

	@Override
	public Collision checkCollision(Shape a, Shape b) {
		if(a.getBoundingBox().intersects(b.getBoundingBox()))
			return new Collision(a, b);
		else
			return null;
	}
	
	/** Calculates MTVs for each collision 
	 * 
	 * @param events an iterable collection of collisions to resolve
	 * @returns a map containing the shape of a collision and its relative translation vector*/
	public ObjectMap<Collision, Vector3> resolveCollisions(Iterable<Collision> events) {
		ObjectMap<Collision, Vector3> mtls = new ObjectMap<>();
		
		Vector3 acnt = Pools.get(Vector3.class).obtain(),
				bcnt = Pools.get(Vector3.class).obtain(),
				adim = Pools.get(Vector3.class).obtain(),
				bdim = Pools.get(Vector3.class).obtain();
		
		for(Collision e : events) {
			BoundingBox a = e.a.getBoundingBox(),
						b = e.b.getBoundingBox();
			
			a.getCenter(acnt);
			b.getCenter(bcnt);
			a.getDimensions(adim);
			b.getDimensions(bdim);
			
			//TODO: in the future try to reuse the calculations from checkCollisions()
			float lx = Math.abs(acnt.x - bcnt.x);
			float sumx = (adim.x / 2.0f) + (bdim.x / 2.0f);
			float difx = Math.abs(lx - sumx);

			float ly = Math.abs(acnt.y - bcnt.y);
			float sumy = (adim.y / 2.0f) + (bdim.y / 2.0f);
			float dify = Math.abs(ly - sumy);

			float lz = Math.abs(acnt.z - bcnt.z);
			float sumz = (adim.z / 2.0f) + (bdim.z / 2.0f);
			float difz = Math.abs(lz - sumz);

			if(difx <= dify && difx <= difz)
				mtls.put(e, Pools.get(Vector3.class).obtain().set(difx, 0, 0));
			else if(dify <= difx && dify <= difz)
				mtls.put(e, Pools.get(Vector3.class).obtain().set(0, dify, 0));
			else if(difz <= difx && difz <= dify)
				mtls.put(e, Pools.get(Vector3.class).obtain().set(0, 0, difz));
		}
		
		Pools.get(Vector3.class).free(acnt);
		Pools.get(Vector3.class).free(bcnt);
		Pools.get(Vector3.class).free(adim);
		Pools.get(Vector3.class).free(bdim);
		
		return mtls;
	}

}
