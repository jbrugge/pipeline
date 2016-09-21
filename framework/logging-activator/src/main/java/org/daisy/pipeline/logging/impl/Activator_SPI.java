package org.daisy.pipeline.logging.impl;

import org.daisy.common.service.CreateOnStart;

public class Activator_SPI implements CreateOnStart {
	public Activator_SPI() {
		JulToSlf4jBridgeSetup.setup();
	}
}
