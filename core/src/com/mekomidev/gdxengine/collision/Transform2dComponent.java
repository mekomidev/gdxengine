package com.mekomidev.gdxengine.collision;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pools;
import com.mekomidev.gdxengine.ecs.Component;

public class Transform2dComponent extends Component {
	/** Position in the stage */
	public final Vector2 pos;
	
	/** Rotation in radiants */
	public float rot;
	
	public Transform2dComponent() {
		pos = Pools.get(Vector2.class).obtain();
	}

	@Override
	public void reset() {
		pos.setZero();
		rot = 0;
	}

	@Override
	public void dispose() {
		Pools.get(Vector2.class).free(pos);
	}
}
