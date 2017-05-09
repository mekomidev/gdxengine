package com.mekomidev.testgame.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mekomidev.gdxengine.ecs.Entity;
import com.mekomidev.gdxengine.ecs.EntityManager;
import com.mekomidev.gdxengine.graphics.sprites.SpriteComponent;
import com.mekomidev.gdxengine.utils.loaders.GifDecoder;

public class SnoopDoggEntity extends Entity {
	Animation<TextureRegion> gif;
	SpriteComponent sc;
	
	public SnoopDoggEntity(EntityManager manager, String name) {
		super(manager, name);
		
		FileHandle file = Gdx.files.internal("animation.gif");
		gif = GifDecoder.loadGIFAnimation(Animation.PlayMode.LOOP, file.read(), (int)file.length());
		sc = addComponent(SpriteComponent.class);
		sc.sprite = new Sprite();
		sc.depth = -100;
	}

	@Override
	public void create() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render() {
		// TODO Auto-generated method stub
		
	}

	float animTimer = 0;
	
	@Override
	public void update(float delta) {
		animTimer = (animTimer + delta) % 1f;
		
		TextureRegion tr = gif.getKeyFrame(animTimer, true);
		sc.sprite.setRegion(tr);
	}

	@Override
	public void dispose() {
		
	}

	@Override
	public void triggerEvent(String eventName, Object... blob) {
		// TODO Auto-generated method stub
		
	}

}
