package com.kerberjg.gdxstudio.core.graphics.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.kerberjg.gdxstudio.core.ecs.Component;

public class SpriteComponent extends Component {
	/** The Sprite object to be drawn */
	public Sprite sprite;
	/** Rendering depth.
	 * Lower value = on top, rendered last
	 * Higher value = on bottom, rendered first*/
	public int depth;
	/** Visibility flag. When set to false, the rendering of this sprite is skipped */
	public boolean visible = true;
	
	public SpriteComponent() {
		this(null, 0);
	}
	
	public SpriteComponent(Sprite sprite) {
		this(sprite, 0);
	}
	
	public SpriteComponent(Sprite sprite, int depth) {
		this.sprite = sprite;
		this.depth = depth;
	}

	@Override
	public void reset() {
		sprite = null;
	}

	@Override
	public void dispose() {}

}
