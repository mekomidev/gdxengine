package com.mekomidev.testgame.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mekomidev.gdxengine.Stage;
import com.mekomidev.gdxengine.Stage.StageBuilder;
import com.mekomidev.gdxengine.ecs.Entity;
import com.mekomidev.gdxengine.graphics.g3d.ModelRenderingSystem;
import com.mekomidev.gdxengine.graphics.sprites.SpriteComponent;
import com.mekomidev.gdxengine.graphics.sprites.SpritePainterSystem;
import com.mekomidev.testgame.entities.SnoopDoggEntity;
import com.mekomidev.testgame.entities.TestSpriteEntity;
import com.mekomidev.testgame.entities.TexturedBallEntity;

public class TestStageBuilder extends StageBuilder {

	@Override
	public Stage build() {
		Gdx.graphics.setWindowedMode(1280, 800);
		
		
		Stage stg = getStageInstance();
		
		stg.backgroundColor = Color.BLACK;
		stg.addSystem(new ModelRenderingSystem());
		
		Entity obj_scene = new TexturedBallEntity(stg, "obj_scene");
		
		return stg;
	}

}
