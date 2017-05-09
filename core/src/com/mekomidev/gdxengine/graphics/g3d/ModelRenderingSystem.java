package com.mekomidev.gdxengine.graphics.g3d;

import java.util.List;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.mekomidev.gdxengine.Game;
import com.mekomidev.gdxengine.ecs.Component;
import com.mekomidev.gdxengine.ecs.EntitySystem;

public class ModelRenderingSystem extends EntitySystem {
	private static final int CAMERA_ID = Component.map.getSubclassId(CameraComponent.class);
	private static final int MODEL_ID = Component.map.getSubclassId(ModelComponent.class);
	
	public Environment environment = new Environment();
	private ObjectMap<Camera, Array<ModelComponent>> models = new ObjectMap<>();

	@Override
	protected void init() {
		//models = new ObjectMap<>();
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void update(float delta) {
		List<CameraComponent> cameras = (List<CameraComponent>) Game.stage.getComponents(CAMERA_ID);
		List<ModelComponent> models = (List<ModelComponent>) Game.stage.getComponents(MODEL_ID);
		
		for(CameraComponent cc : cameras) {
			Camera cam = cc.cam;
			
			Array<ModelComponent> mcs = new Array<>();
			
			for(ModelComponent mc : models)
				mcs.add(mc);
			
			
			this.models.put(cam, mcs);
		}
	}

	@Override
	protected void render() {
		for(Camera cam : models.keys()) {
			ModelBatch batch = new ModelBatch();
			batch.begin(cam);
			
			for(ModelComponent mc : models.get(cam))
				batch.render(mc.model, environment, mc.shader);
			
			batch.end();
			batch.dispose();
		}
			
	}
	
	@Override
	public void dispose() {
		
	}
}
