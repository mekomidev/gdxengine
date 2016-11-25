package com.kerberjg.gdxstudio.entities;

public abstract class Component {
	/** Counter variable for the incremental generation of component IDs
	 * @hide*/
	private static int COMPONENTS_COUNT = 0;
	
	/** The EntitySystem ID used for faster retrieval */
	public static final int COMPONENT_ID = Component.COMPONENTS_COUNT++;
}
