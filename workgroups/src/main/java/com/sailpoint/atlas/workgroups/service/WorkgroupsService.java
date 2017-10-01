/*
 * Copyright (C) 2017 SailPoint Technologies, Inc.  All rights reserved.
 */
package com.sailpoint.atlas.workgroups.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sailpoint.atlas.AtlasConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.resteasy.util.HttpResponseCodes;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class WorkgroupsService {

	private static final Log _log = LogFactory.getLog(WorkgroupsService.class);

	public String ping() {
		_log.info("pinged");

		return "pong !!!";
	}
}