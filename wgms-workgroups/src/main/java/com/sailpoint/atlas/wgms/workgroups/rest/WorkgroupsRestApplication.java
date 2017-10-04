/*
 * Copyright (C) 2017 SailPoint Technologies, Inc.  All rights reserved.
 */
package com.sailpoint.atlas.wgms.workgroups.rest;

import com.sailpoint.atlas.rest.RestApplication;

public class WorkgroupsRestApplication extends RestApplication {

	public WorkgroupsRestApplication() {
		add(WorkgroupsResource.class);
	}
}