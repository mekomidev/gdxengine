package com.mekomidev.gdxengine.core.utils.builders;

import com.mekomidev.gdxengine.core.Stage;
import com.mekomidev.gdxengine.core.Stage.StageBuilder;

public class DummyStageBuilder extends StageBuilder {

	@Override
	public Stage build() {
		return getStageInstance();
	}

}
