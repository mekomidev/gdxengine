package com.kerberjg.gdxstudio.core.graphics.sprites;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ReflectionPool;
import com.kerberjg.gdxstudio.core.Game;
import com.kerberjg.gdxstudio.core.ecs.Component;
import com.kerberjg.gdxstudio.core.ecs.EntityManager;
import com.kerberjg.gdxstudio.core.ecs.EntitySystem;
import com.kerberjg.gdxstudio.core.utils.collections.FastIntMap;

/** Sprite rendering system implemented using the Painter's Algorithm.
 * Sorts sprites by depth before rendering them. 
 * 
 * @author kerberjg */
public class SpritePainterSystem extends EntitySystem {
	private static final int DEFAULT_MAX_SPRITES = 1000;
	
	/** Rendering batch */
	private SpriteBatch batch;
	/** List of sprites to render */
	private List<Sprite> sprites;
	/** Pool of Sprite vessels to use as copies */
	private Pool<Sprite> vessels;
	
	private int spriteComponentType;
	
	public SpritePainterSystem() {
		this(DEFAULT_MAX_SPRITES);
	}
	
	public SpritePainterSystem(int maxSprites) {
		batch = new SpriteBatch(maxSprites);
		sprites = new ArrayList<>(maxSprites);
		vessels = new ReflectionPool<>(Sprite.class, maxSprites);
	}
	
	@Override
	public void init() {
		spriteComponentType = Component.map.getSubclassId(SpriteComponent.class);
	}

	@Override
	public void update(float delta) {
		EntityManager manager = Game.stage;
		FastIntMap<SpriteComponent> components = manager.getComponentMap(spriteComponentType);
		ArrayList<SpriteComponent> sortedComponents = new ArrayList<>(components.size());
		
		// Copies all items to the new array
		for(SpriteComponent sc : components)
			if(sc.visible && sc.sprite != null)
				sortedComponents.add(sc);
		
		// Sorts the sprites
		Collections.sort(sortedComponents, new DepthComparator());
		
		// Adds the sprites to the rendering queue
		for(SpriteComponent sc : components) {
			Sprite s = vessels.obtain();
			s.set(sc.sprite);
		}
	}

	@Override
	public void dispose() {
		cleanup();
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
	}

	@Override
	public void cleanup() {
		sprites.clear();
	}
	
	/** Sorts SpriteComponents by putting the biggest numbers (deeper ones) first 
	 * 
	 * @author kerberjg*/
	private static class DepthComparator implements Comparator<SpriteComponent> {
		@Override
		public int compare(SpriteComponent a, SpriteComponent b) {
			// Checks for null cases, putting them at the end of the list
			if(a == null)
				return 1;
			else if (b == null)
				return -1;
			// Does actual sorting
			else if(a.depth == b.depth)
				return 0;
			else if(a.depth > b.depth)
				return 1;
			else
				return -1;
		}
	}
}
