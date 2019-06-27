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

import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.cronjob.model.JobModel;
import de.hybris.platform.sap.productconfig.model.intf.DataLoaderCronjobParameters;
import de.hybris.platform.sap.productconfig.model.model.DataLoaderCronJobModel;
import de.hybris.platform.servicelayer.cronjob.CronJobService;

import java.util.Collection;
import java.util.Date;
import java.util.List;


/**
 * Spring managed parameter container for dataload jobs
 */
public class DataLoaderCronjobParametersImpl implements DataLoaderCronjobParameters
{

	private String dataloadStartJobBeanId;
	private String dataloadStopJobBeanId;

	private CronJobService cronJobService;

	@Override
	public String getDataloadStartJobBeanId()
	{
		return dataloadStartJobBeanId;
	}

	@Override
	public void setDataloadStartJobBeanId(final String dataloadStartJobBeanId)
	{
		this.dataloadStartJobBeanId = dataloadStartJobBeanId;
	}

	@Override
	public String getDataloadStopJobBeanId()
	{
		return dataloadStopJobBeanId;
	}

	@Override
	public void setDataloadStopJobBeanId(final String dataloadStopJobBeanId)
	{
		this.dataloadStopJobBeanId = dataloadStopJobBeanId;
	}

	/**
	 * @return the cronJobService
	 */
	public CronJobService getCronJobService()
	{
		return cronJobService;
	}

	/**
	 * @param cronJobService
	 */
	public void setCronJobService(final CronJobService cronJobService)
	{
		this.cronJobService = cronJobService;
	}

	@Override
	public Integer retrieveNodeIdForStartJob()
	{
		Integer nodeId = null;
		final JobModel jobModel = getCronJobService().getJob(getDataloadStartJobBeanId());
		final Collection<CronJobModel> cronJobs = jobModel.getCronJobs();

		Date creationTimeOld = null;
		for (final CronJobModel cronJob : cronJobs)
		{
			final Date creationTime = cronJob.getCreationtime();
			if (creationTimeOld == null || creationTimeOld.compareTo(creationTime) > 0)
			{
				nodeId = cronJob.getNodeID();
				creationTimeOld = creationTime;
			}
		}
		return nodeId;
	}

	@Override
	public Integer retrieveNodeIdForStopJob()
	{
		Integer runningOnNodeId = null;
		final List<CronJobModel> cjList = getCronJobService().getRunningOrRestartedCronJobs();
		for (final CronJobModel cj : cjList)
		{
			if (cj instanceof DataLoaderCronJobModel)
			{
				runningOnNodeId = cj.getRunningOnClusterNode();
				break;
			}
		}
		return runningOnNodeId;
	}

}
