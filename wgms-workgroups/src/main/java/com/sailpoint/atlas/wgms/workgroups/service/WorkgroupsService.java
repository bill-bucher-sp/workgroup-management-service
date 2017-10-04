/*
 * Copyright (C) 2017 SailPoint Technologies, Inc.  All rights reserved.
 */
package com.sailpoint.atlas.wgms.workgroups.service;

import com.google.inject.Singleton;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Singleton
public class WorkgroupsService {

	private static final Log _log = LogFactory.getLog(WorkgroupsService.class);

	public String ping() {
		_log.info("pinged");

		return "pong !!!";
	}
}