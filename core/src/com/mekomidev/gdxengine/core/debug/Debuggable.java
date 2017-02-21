package com.mekomidev.gdxengine.core.debug;

/** A debugging interface for entities, designed to use in development mode and inside the editor 
 * 
 * @author kerberjg */

public interface Debuggable {
	/** Renders debug information, such as bounding shapes, vectors, etc. */
	public void debugRender();
	/** Allows to manipulate various debug options, for example, what debug information to log or display */
	public void setDebugOption(String key, String value);
}
