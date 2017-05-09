package com.mekomidev.gdxengine.graphics.sprites;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ReflectionPool;
import com.mekomidev.gdxengine.Game;
import com.mekomidev.gdxengine.ecs.Component;
import com.mekomidev.gdxengine.ecs.EntitySystem;

/** Sprite rendering system implemented using the Painter's Algorithm.
 * Sorts sprites by depth before rendering them. 
 * 
 * @author kerberjg */
public class SpritePainterSystem extends EntitySystem {
	private static final int DEFAULT_MAX_SPRITES = 1000;
	private static final int SPRITE_ID = Component.map.getSubclassId(SpriteComponent.class);
	
	/** Rendering batch */
	private SpriteBatch batch;
	/** List of sprites to render */
	private List<Sprite> sprites;
	/** Pool of Sprite vessels to use as copies */
	private Pool<Sprite> vessels;
	
	public SpritePainterSystem() {
		this(DEFAULT_MAX_SPRITES);
	}
	
	public SpritePainterSystem(int maxSprites) {
		batch = new SpriteBatch(maxSprites);
		sprites = new ArrayList<>(maxSprites);
		vessels = new ReflectionPool<>(Sprite.class, maxSprites);
	}
	
	@Override
	protected void init() {}
	
	@Override
	public void update(float delta) {
		@SuppressWarnings("unchecked")
		List<SpriteComponent> sprites = (List<SpriteComponent>) Game.stage.getComponents(SPRITE_ID);
		ArrayList<SpriteComponent> sortedComponents = new ArrayList<>();
		
		for(SpriteComponent sc : sprites)
			if(sc != null && sc.sprite != null && sc.sprite.getTexture() != null && sc.visible)
				sortedComponents.add(sc);
		
		// Sorts the sprites
		Collections.sort(sortedComponents, new DepthComparator());
		
		// Adds the sprites to the rendering queue
		for(SpriteComponent sc : sortedComponents) {
			Sprite s = vessels.obtain();
			s.set(sc.sprite);
			this.sprites.add(s);
		}
		
		batch.setProjectionMatrix(Game.stage.camera.combined);
	}

	@Override
	public void dispose() {
		vessels.clear();
	}

	@Override
	public void render() {
		batch.begin();
		
		// Draws the sprites and frees the vessel
		for(Sprite s : sprites) {
			s.draw(batch);
			vessels.free(s);
		}
		
		batch.end();
		sprites.clear();
	}
	
	/** Sorts SpriteComponents by putting the biggest numbers (deeper ones) first 
	 * 
	 * @author kerberjg*/
	private static class DepthComparator implements Comparator<SpriteComponent> {
		@Override
		public int compare(SpriteComponent a, SpriteComponent b) {
			//INFO: shouldn't be null, due updateStep() already checking for nullability 
			// Checks for null cases, putting null items at the end of the list
			/*
			if(a == null)
				return 1;
			else if (b == null)
				return -1;
			
			// Does actual sorting
			else*/ if(a.depth == b.depth)
				return 0;
			else if(a.depth > b.depth)
				return 1;
			else
				return -1;
		}
	}
}
