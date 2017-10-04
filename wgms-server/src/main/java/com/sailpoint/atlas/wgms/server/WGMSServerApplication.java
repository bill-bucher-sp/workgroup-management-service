/*
 * Copyright (C) 2017 SailPoint Technologies, Inc.  All rights reserved.
 */
package com.sailpoint.atlas.wgms.server;

import com.sailpoint.atlas.health.HealthAtlasModule;
import com.sailpoint.atlas.idn.IdnAtlasApplication;
import com.sailpoint.atlas.service.AtlasServiceModule;
import com.sailpoint.atlas.wgms.workgroups.WorkgroupsAtlasModule;

public class WGMSServerApplication extends IdnAtlasApplication {

	/**
	 * Constructs a new server application.
	 */
	public WGMSServerApplication() {
		IdnAtlasApplication.setStack("wms");

		addServiceModule(new AtlasServiceModule());
		loadModule(new WorkgroupsAtlasModule());
		loadModule(new HealthAtlasModule());
	}

	/**
	 * The application entry point.
	 * @param args The command-line arguments.
	 */
	public static void main(String[] args) {
		IdnAtlasApplication.run(WGMSServerApplication.class, args);
	}
}
