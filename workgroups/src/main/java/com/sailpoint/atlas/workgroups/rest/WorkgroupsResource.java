/*
 * Copyright (C) 2017 SailPoint Technologies, Inc.  All rights reserved.
 */
package com.sailpoint.atlas.workgroups.rest;

import com.sailpoint.atlas.workgroups.service.WorkgroupsService;
import com.google.inject.Inject;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Workgroups REST resource.
 */
@Path("workgroups")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WorkgroupsResource {

	@Inject
	WorkgroupsService _workgroupsService;

	static public final String HEADER_TOTAL_COUNT = "X-Total-Count";

	/**
	 * Test method to verify that the service is running
	 */
	@GET
	public String ping() {

		return _workgroupsService.ping();
	}
}