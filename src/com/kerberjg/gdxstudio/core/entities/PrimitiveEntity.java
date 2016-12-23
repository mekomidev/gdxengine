package com.kerberjg.gdxstudio.core.entities;

import com.badlogic.gdx.utils.Disposable;

/** 
 * Defines what a game object should essentially do.
 * This class should serve as a compass of what the development should actually strive to achieve
 * 
 * @hide
 * @author kerberjg
 * */
public interface PrimitiveEntity extends Disposable {
	public void create();
	public void render();
	public void update(float delta);
}	
