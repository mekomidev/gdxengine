package com.mekomidev.gdxengine.utils;

public abstract class Units {
	//TODO: kilobytes... or KIBIBYTES?
	public static final long BYTE = 1;
	public static final long KILOBYTE = 1024;
	public static final long MEGABYTE = 1024 * KILOBYTE;
	public static final long GIGABYTE = 1024 * MEGABYTE;
	public static final long TERABYTE = 1024 * MEGABYTE;
	
	/*
	 *  Physics
	 */
	
	/** Earth's gravitational acceleration (m/s2) */
	public static final float g = 9.807f;
}
