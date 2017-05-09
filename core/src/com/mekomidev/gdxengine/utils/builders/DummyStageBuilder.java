package com.mekomidev.gdxengine.utils.builders;

import com.mekomidev.gdxengine.Stage;
import com.mekomidev.gdxengine.Stage.StageBuilder;

public class DummyStageBuilder extends StageBuilder {

	@Override
	public Stage build() {
		return getStageInstance();
	}

}
