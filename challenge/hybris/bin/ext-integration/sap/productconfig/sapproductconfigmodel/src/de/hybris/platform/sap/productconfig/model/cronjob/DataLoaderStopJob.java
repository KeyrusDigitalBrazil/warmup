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
package de.hybris.platform.sap.productconfig.model.cronjob;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.sap.productconfig.model.intf.DataLoaderManagerContainer;
import de.hybris.platform.sap.productconfig.model.model.DataLoaderCronJobModel;
import de.hybris.platform.sap.productconfig.model.model.DataLoaderStopCronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import java.util.List;

import org.apache.log4j.Logger;

import com.sap.custdev.projects.fbs.slc.dataloader.standalone.manager.DataloaderFailureException;
import com.sap.custdev.projects.fbs.slc.dataloader.standalone.manager.DataloaderManager;


/**
 * Stops initial or delta load
 */
public class DataLoaderStopJob extends AbstractJobPerformable<DataLoaderStopCronJobModel>
{
	private static final Logger LOG = Logger.getLogger(DataLoaderStopJob.class);

	private DataLoaderManagerContainer dataLoaderManagerContainer;
	private CronJobService cronJobService;

	@Override
	public PerformResult perform(final DataLoaderStopCronJobModel dataLoaderCronJobModel)
	{

		// Stop Job (this job) has to be executed on the same server node where the running start job is executed
		final Integer stopJobNodeId = dataLoaderCronJobModel.getRunningOnClusterNode();
		final Integer startJobNodeId = retrieveDataloadStartJobNodeId();

		if (startJobNodeId == null)
		{
			LOG.debug("There was no running dataload start job");
			return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
		}

		if (startJobNodeId.compareTo(stopJobNodeId) != 0)
		{
			//This leads to job status CronJobResult.ERROR, CronJobStatus.ABORTED
			throw new IllegalStateException(
					"Stop download failed. Stop job has to run on the same node as the start job. stopJobNodeId = " + stopJobNodeId
							+ "; startJobNodeId = " + startJobNodeId);
		}

		final DataloaderManager dataLoaderManager = getDataLoaderManagerContainer().getDataLoaderManager();

		if (dataLoaderManager != null)
		{
			try
			{
				LOG.debug("Data Load Stop requested");

				if (dataLoaderManager.isDownloadRunning())
				{
					dataLoaderManager.stopDownload();
					LOG.debug("Data Load Stop executed");
				}
				else
				{
					LOG.debug("There was no running dataload");
				}
			}
			catch (final DataloaderFailureException e)
			{
				//This leads to job status CronJobResult.ERROR, CronJobStatus.ABORTED
				throw new IllegalStateException("Stop download failed", e);
			}
		}

		return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);

	}


	/**
	 * @return the dataLoaderManagerContainer
	 */
	public DataLoaderManagerContainer getDataLoaderManagerContainer()
	{
		return dataLoaderManagerContainer;
	}


	/**
	 * @param container
	 *           dataLoaderManagerContainer
	 */
	public void setDataLoaderManagerContainer(final DataLoaderManagerContainer container)
	{
		this.dataLoaderManagerContainer = container;
	}

	/**
	 * @return the cronjob service
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


	protected Integer retrieveDataloadStartJobNodeId()
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
