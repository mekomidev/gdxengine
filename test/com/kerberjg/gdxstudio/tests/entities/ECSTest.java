package com.kerberjg.gdxstudio.tests.entities;

import static org.junit.Assert.*;
import org.junit.Test;

import com.kerberjg.gdxstudio.core.entities.*;

public class ECSTest {

	@Test
	public void test() {
		// TODO: learn to write tests
		EntityManager manager = new EntityManager();
		
		manager.addSystem(new TestSystem());
		
		
	}

}
