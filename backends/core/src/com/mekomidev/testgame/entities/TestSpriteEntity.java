package com.mekomidev.testgame.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mekomidev.gdxengine.core.ecs.Entity;
import com.mekomidev.gdxengine.core.ecs.EntityManager;
import com.mekomidev.gdxengine.core.graphics.sprites.SpriteComponent;

public class TestSpriteEntity extends Entity {
	static float lol = 0;
	
	public TestSpriteEntity(EntityManager manager, String name) {
		super(manager, name);
		
		sc = addComponent(SpriteComponent.class);
		sc.sprite = new Sprite(new Texture("badlogic.jpg"));
		
		lol += 10;
		sc.sprite.setX(lol);
		
		System.out.println(name);
	}
	
	SpriteComponent sc;

	@Override
	public void create() {
		//sc.sprite.setPosition(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
		//sc.sprite.setOrigin(sc.sprite.getWidth() / 2, sc.sprite.getHeight() / 2);
	}

	@Override
	public void render() {
		// TODO Auto-generated method stub

	}

	private float rot = 0;
	
	@Override
	public void update(float delta) {
		rot += 45 * delta;
		sc.sprite.setRotation(rot);
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void triggerEvent(String eventName, Object... blob) {
		// TODO Auto-generated method stub

	}

}
