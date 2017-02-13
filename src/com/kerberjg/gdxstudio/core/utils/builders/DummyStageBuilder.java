package com.kerberjg.gdxstudio.core.utils.builders;

import com.kerberjg.gdxstudio.core.Stage;
import com.kerberjg.gdxstudio.core.Stage.StageBuilder;

public class DummyStageBuilder extends StageBuilder {

	@Override
	public Stage build() {
		return getStageInstance();
	}

}
