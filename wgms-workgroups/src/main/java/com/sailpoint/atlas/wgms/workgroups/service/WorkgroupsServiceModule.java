/*
 * Copyright (C) 2017 SailPoint Technologies, Inc.  All rights reserved.
 */
package com.sailpoint.atlas.wgms.workgroups.service;

import com.google.inject.AbstractModule;

public class WorkgroupsServiceModule extends AbstractModule {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configure() {
		binder().requireExplicitBindings();

		bind(WorkgroupsService.class);
	}
}