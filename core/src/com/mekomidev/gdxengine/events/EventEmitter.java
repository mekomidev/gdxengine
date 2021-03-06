package com.mekomidev.gdxengine.events;

public interface EventEmitter {
	public void addListener(EventListener o);
	public void removeListener(EventListener o);
	public void triggerEvent(String event, Object... args);
}
