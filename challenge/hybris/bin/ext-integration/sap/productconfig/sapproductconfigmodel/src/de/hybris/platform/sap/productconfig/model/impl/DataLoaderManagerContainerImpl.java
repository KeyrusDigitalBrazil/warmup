/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.productconfig.model.impl;

import de.hybris.platform.sap.productconfig.model.intf.DataLoaderManagerContainer;

import com.sap.custdev.projects.fbs.slc.dataloader.standalone.manager.DataloaderManager;


/**
 * Spring managed wrapper for {@link DataloaderManager}
 */
public class DataLoaderManagerContainerImpl implements DataLoaderManagerContainer
{

	private DataloaderManager dataLoaderManager;
	private boolean resumePerformed;


	@Override
	public DataloaderManager getDataLoaderManager()
	{
		return dataLoaderManager;
	}

	@Override
	public void setDataLoaderManager(final DataloaderManager manager)
	{
		this.dataLoaderManager = manager;

	}

	@Override
	public void setResumePerformed(final boolean b)
	{
		resumePerformed = b;

	}

	@Override
	public boolean isResumePerformed()
	{
		return resumePerformed;
	}

}
