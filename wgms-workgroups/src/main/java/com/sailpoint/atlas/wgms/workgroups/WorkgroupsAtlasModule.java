/*
 * Copyright (C) 2017 SailPoint Technologies, Inc.  All rights reserved.
 */
package com.sailpoint.atlas.wgms.workgroups;

import com.sailpoint.atlas.AtlasApplication;
import com.sailpoint.atlas.AtlasModule;
import com.sailpoint.atlas.rest.RestDeployment;
import com.sailpoint.atlas.wgms.workgroups.rest.WorkgroupsRestApplication;
import com.sailpoint.atlas.wgms.workgroups.service.WorkgroupsServiceModule;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * WorkgroupsModule - loads Workgroups REST application
 */
public class WorkgroupsAtlasModule implements AtlasModule {

	static Log _log = LogFactory.getLog(WorkgroupsAtlasModule.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(AtlasApplication app) {
		app.addRestDeployment(new RestDeployment("/workgroups", WorkgroupsRestApplication.class));
		app.addServiceModule(new WorkgroupsServiceModule());
	}
}