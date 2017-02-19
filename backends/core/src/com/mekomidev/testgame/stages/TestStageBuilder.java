package com.mekomidev.testgame.stages;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mekomidev.gdxstudio.core.Stage;
import com.mekomidev.gdxstudio.core.Stage.StageBuilder;
import com.mekomidev.gdxstudio.core.ecs.Entity;
import com.mekomidev.gdxstudio.core.graphics.sprites.SpriteComponent;
import com.mekomidev.gdxstudio.core.graphics.sprites.SpritePainterSystem;
import com.mekomidev.testgame.entities.SnoopDoggEntity;
import com.mekomidev.testgame.entities.TestSpriteEntity;

public class TestStageBuilder extends StageBuilder {

	@Override
	public Stage build() {
		Stage stg = getStageInstance();
		
		stg.addSystem(new SpritePainterSystem());
		
		
		Entity obj_test = new TestSpriteEntity(stg, "obj_test");
		Entity obj_test2 = new TestSpriteEntity(stg, "obj_test2");
		Entity obj_test3 = new TestSpriteEntity(stg, "obj_test3");
		
		
		//Entity obj_dogg = new SnoopDoggEntity(stg, "obj_dogg");
		
		return stg;
	}

}
