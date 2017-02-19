package com.mekomidev.gdxstudio.core.utils.builders;

import com.mekomidev.gdxstudio.core.Stage;
import com.mekomidev.gdxstudio.core.Stage.StageBuilder;

public class DummyStageBuilder extends StageBuilder {

	@Override
	public Stage build() {
		return getStageInstance();
	}

}
