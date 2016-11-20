package com.kerberjg.gdxstudio.game;

public interface Entity {
	public void create();
	public void draw();
	public void update(float delta);
	public void dispose();
}
