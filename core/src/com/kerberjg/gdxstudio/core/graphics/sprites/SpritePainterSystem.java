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
import com.kerberjg.gdxstudio.core.ecs.EntitySystem;

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
	
	public SpritePainterSystem() {
		this(DEFAULT_MAX_SPRITES);
	}
	
	public SpritePainterSystem(int maxSprites) {
		batch = new SpriteBatch(maxSprites);
		sprites = new ArrayList<>(maxSprites);
		vessels = new ReflectionPool<>(Sprite.class, maxSprites);
	}
	
	private static int[] components;
	
	@Override
	protected int[] init() {
		if(components == null) {
			components = new int[1];
			components[0] = Component.map.getSubclassId(SpriteComponent.class);
		}
		
		return components;
	}

	ArrayList<SpriteComponent> sortedComponents = new ArrayList<>();
	
	@Override
	public void updateBegin(float delta) {
		batch.setProjectionMatrix(Game.stage.camera.combined);
	}
	
	@Override
	public <C extends Component> void updateStep(C c) {
		SpriteComponent sc = (SpriteComponent) c;
		if(sc != null && sc.sprite != null && sc.sprite.getTexture() != null && sc.visible)
			sortedComponents.add(sc);
				
	}
	
	@Override
	public void updateEnd() {
		// Sorts the sprites
		Collections.sort(sortedComponents, new DepthComparator());
		
		// Adds the sprites to the rendering queue
		for(SpriteComponent sc : sortedComponents) {
			Sprite s = vessels.obtain();
			s.set(sc.sprite);
			sprites.add(s);
		}
		
		sortedComponents.clear();
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
