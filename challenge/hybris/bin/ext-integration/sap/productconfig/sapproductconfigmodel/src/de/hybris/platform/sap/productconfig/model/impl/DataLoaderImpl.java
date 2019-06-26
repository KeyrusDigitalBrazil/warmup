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

import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.productconfig.model.enums.DataloadStatus;
import de.hybris.platform.sap.productconfig.model.intf.DataLoader;
import de.hybris.platform.sap.productconfig.model.model.CPQDataloadStatusModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Date;

import org.apache.log4j.Logger;

import com.sap.custdev.projects.fbs.slc.dataloader.standalone.manager.DataloaderFailureException;
import com.sap.custdev.projects.fbs.slc.dataloader.standalone.manager.DataloaderManager;


/**
 * Responsible to start SSC initial and delta download
 */
public class DataLoaderImpl implements DataLoader
{
	private static final Logger LOG = Logger.getLogger(DataLoaderImpl.class);

	@Override
	public void performInitialLoad(final SAPConfigurationModel sapConfiguration, final DataloaderManager dataloaderManager,
			final ModelService modelService)
	{
		try
		{
			performInitialLoadRaisingException(dataloaderManager, sapConfiguration, modelService);
		}
		catch (final DataloaderFailureException e)
		{
			final CPQDataloadStatusModel dataloadStatus = sapConfiguration.getSapproductconfig_cpqDataloadStatus();
			dataloadStatus.setCpqLastInitialLoadEndTime(null);
			dataloadStatus.setCpqDataloadStatus(DataloadStatus.ERROR);
			resetStatistics(dataloadStatus);
			modelService.save(dataloadStatus);
			// This leads to job status CronJobResult.ERROR, CronJobStatus.ABORTED
			throw new IllegalStateException("Initial load failed", e);
		}
	}


	protected void performInitialLoadRaisingException(final DataloaderManager dataloaderManager,
			final SAPConfigurationModel sapConfiguration, final ModelService modelService) throws DataloaderFailureException
	{
		LOG.debug("Initial load start");

		CPQDataloadStatusModel dataloadStatus = sapConfiguration.getSapproductconfig_cpqDataloadStatus();

		if (dataloadStatus == null)
		{
			dataloadStatus = new CPQDataloadStatusModel();
			dataloadStatus.setCpqDataloadStatusForSapConfiguration(sapConfiguration.getCore_name());
			dataloadStatus.setOwner(sapConfiguration);
			modelService.save(dataloadStatus);
			sapConfiguration.setSapproductconfig_cpqDataloadStatus(dataloadStatus);
			modelService.save(sapConfiguration);
		}

		dataloadStatus.setCpqLastInitialLoadStartTime(new Date());
		dataloadStatus.setCpqLastInitialLoadEndTime(null);
		dataloadStatus.setCpqDataloadStatus(DataloadStatus.INITIAL_LOAD);
		resetStatistics(dataloadStatus);
		modelService.save(dataloadStatus);

		dataloaderManager.createTables();
		dataloaderManager.startInitialDownload();

		if (dataloaderManager.isStoppedDownloadManually())
		{
			LOG.debug("Dataload is stopped during initial load");

			dataloadStatus.setCpqDataloadStatus(DataloadStatus.INITIAL_LOAD_STOPPED);
			dataloadStatus.setCpqLastInitialLoadTransferredVolume(dataloadStatus.getCpqCurrentInitialLoadTransferredVolume());
			dataloadStatus.setCpqCurrentInitialLoadTransferredVolume(null);
			modelService.save(dataloadStatus);
			return;
		}

		LOG.debug("Initial load end");

		dataloadStatus.setCpqLastInitialLoadEndTime(new Date());
		dataloadStatus.setCpqDataloadStatus(DataloadStatus.INITIAL_LOAD_COMPLETED);
		dataloadStatus.setCpqLastInitialLoadTransferredVolume(dataloadStatus.getCpqCurrentInitialLoadTransferredVolume());
		dataloadStatus.setCpqCurrentInitialLoadTransferredVolume(null);
		modelService.save(dataloadStatus);
	}


	/**
	 * Resets data loader statistics
	 *
	 * @param dataloadStatus
	 */
	protected void resetStatistics(final CPQDataloadStatusModel dataloadStatus)
	{
		dataloadStatus.setCpqCurrentInitialLoadTransferredVolume(null);
		dataloadStatus.setCpqCurrentDeltaLoadTransferredVolume(null);
		dataloadStatus.setCpqNumberOfEntriesInDeltaLoadQueue(null);
	}

	protected void performDeltaLoadRaisingException(final DataloaderManager dataloaderManager,
			final SAPConfigurationModel sapConfiguration, final ModelService modelService) throws DataloaderFailureException
	{
		LOG.debug("Delta load start");

		final CPQDataloadStatusModel datloadStatus = sapConfiguration.getSapproductconfig_cpqDataloadStatus();

		datloadStatus.setCpqDataloadStatus(DataloadStatus.DELTA_LOAD);
		modelService.save(datloadStatus);

		dataloaderManager.startDeltaDownload();

		if (dataloaderManager.isStoppedDownloadManually())
		{
			LOG.debug("Dataload is stopped during delta load");
			datloadStatus.setCpqDataloadStatus(DataloadStatus.DELTA_LOAD_STOPPED);
			datloadStatus.setCpqNumberOfEntriesInDeltaLoadQueue(null);
			datloadStatus.setCpqCurrentDeltaLoadTransferredVolume(null);
			modelService.save(datloadStatus);
			return;
		}


		LOG.debug("Delta load end");
	}

	@Override
	public void performDeltaLoad(final SAPConfigurationModel sapConfiguration, final DataloaderManager dataloaderManager,
			final ModelService modelService)
	{
		try
		{
			performDeltaLoadRaisingException(dataloaderManager, sapConfiguration, modelService);
		}
		catch (final DataloaderFailureException e)
		{
			final CPQDataloadStatusModel dataloadStatus = sapConfiguration.getSapproductconfig_cpqDataloadStatus();
			dataloadStatus.setCpqDataloadStatus(DataloadStatus.ERROR);
			resetStatistics(dataloadStatus);
			modelService.save(dataloadStatus);
			// This leads to job status CronJobResult.ERROR, CronJobStatus.ABORTED
			throw new IllegalStateException("Delta Load failed", e);
		}
	}

}
