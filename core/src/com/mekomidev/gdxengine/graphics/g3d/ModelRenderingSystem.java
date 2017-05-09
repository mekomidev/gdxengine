package com.mekomidev.gdxengine.graphics.g3d;

import java.util.List;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.utils.ObjectMap;
import com.mekomidev.gdxengine.Game;
import com.mekomidev.gdxengine.ecs.Component;
import com.mekomidev.gdxengine.ecs.EntitySystem;

public class ModelRenderingSystem extends EntitySystem {
	private static final int CAMERA_ID = Component.map.getSubclassId(CameraComponent.class);
	private static final int MODEL_ID = Component.map.getSubclassId(ModelComponent.class);
	
	public Environment environment;
	private ObjectMap<Camera, ModelBatch> batches;

	@Override
	protected void init() {
		batches = new ObjectMap<>();
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void update(float delta) {
		List<CameraComponent> cameras = (List<CameraComponent>) Game.stage.getComponents(CAMERA_ID);
		List<ModelComponent> models = (List<ModelComponent>) Game.stage.getComponents(MODEL_ID);
		
		for(CameraComponent cc : cameras) {
			Camera cam = cc.cam;
			ModelBatch batch = new ModelBatch();
			
			batch.begin(cam);
			
			for(ModelComponent mc : models)
				batch.render(mc.model, environment, mc.shader);
			
			batches.put(cam, batch);
		}
	}

	@Override
	protected void render() {
		for(ModelBatch batch : batches.values())
			batch.end();
	}
	
	@Override
	public void dispose() {
		
	}
}
