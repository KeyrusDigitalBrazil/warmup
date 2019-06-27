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
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.productconfig.model.dataloader.configuration.DataloaderSourceParameters;
import de.hybris.platform.sap.productconfig.model.enums.DataLoadTriggerMode;
import de.hybris.platform.sap.productconfig.model.intf.DataLoader;
import de.hybris.platform.sap.productconfig.model.intf.DataLoaderConfigurationHelper;
import de.hybris.platform.sap.productconfig.model.intf.DataLoaderManagerContainer;
import de.hybris.platform.sap.productconfig.model.model.CPQDataloadStatusModel;
import de.hybris.platform.sap.productconfig.model.model.DataLoaderCronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sap.custdev.projects.fbs.slc.dataloader.settings.IDataloaderConfiguration;
import com.sap.custdev.projects.fbs.slc.dataloader.standalone.DataloaderConfiguration;
import com.sap.custdev.projects.fbs.slc.dataloader.standalone.manager.DataloaderManager;
import com.sap.custdev.projects.fbs.slc.dataloader.standalone.manager.DataloaderManagerImpl;
import com.sap.custdev.projects.fbs.slc.dataloader.standalone.manager.DataloaderWorkerHybris;



/**
 * Performs initial and delta load
 */

public class DataLoaderJob extends AbstractJobPerformable<DataLoaderCronJobModel>
{
	private DataLoaderManagerContainer dataLoaderManagerContainer;

	private CronJobService cronJobService;

	private DefaultDataloaderProgressListenerImpl progressListener;

	private DefaultDataloaderMessageListenerImpl messageListener;

	private PropertyAccessFacade propertyAccessFacade;

	private DataLoader dataLoader;

	private DataLoaderConfigurationHelper dataLoaderConfigurationHelper;

	private static final Logger LOG = Logger.getLogger(DataLoaderJob.class);

	/**
	 * @return the propertyAccessFacade
	 */
	public PropertyAccessFacade getPropertyAccessFacade()
	{
		return propertyAccessFacade;
	}

	/**
	 * @return the messageListener
	 */
	public DefaultDataloaderMessageListenerImpl getMessageListener()
	{
		return messageListener;
	}

	/**
	 * @return the progressListener
	 */
	public DefaultDataloaderProgressListenerImpl getProgressListener()
	{
		return progressListener;
	}

	@Override
	public PerformResult perform(final DataLoaderCronJobModel dataLoaderCronJobModel)
	{
		DataLoadTriggerMode triggerMode = dataLoaderCronJobModel.getTriggerMode();


		final PerformResult resultTerminate = checkForResume(dataLoaderCronJobModel, triggerMode);
		if (resultTerminate != null)
		{
			return resultTerminate;
		}

		final SAPConfigurationModel sapConfiguration = dataLoaderCronJobModel.getSapConfiguration();
		if (sapConfiguration == null)
		{
			if (triggerMode == DataLoadTriggerMode.STARTINITIAL || triggerMode == DataLoadTriggerMode.STARTDELTA)
			{
				//This leads to job status CronJobResult.ERROR, CronJobStatus.ABORTED
				throw new IllegalArgumentException("We require an instance of SAPConfiguration to persist our statistics");
			}
			LOG.info("No instance of sapConfiguration available for job, assuming no download happened yet: No resume of download");
			return new PerformResult(CronJobResult.DATALOAD_NO_INITIAL_DOWNLOAD, CronJobStatus.FINISHED);
		}

		final DataloaderManager dataloaderManager = initializeDataLoaderManager(dataLoaderCronJobModel);

		if (dataloaderManager == null)
		{
			return new PerformResult(CronJobResult.DATALOAD_ALREADY_RUNNING, CronJobStatus.FINISHED);
		}

		// Start initial load
		if (triggerMode == DataLoadTriggerMode.STARTINITIAL)
		{
			dataLoader.performInitialLoad(sapConfiguration, dataloaderManager, getModelService());
			if (dataloaderManager.isStoppedDownloadManually() || !getPropertyAccessFacade().getStartDeltaloadAfterInitial())
			{
				return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
			}

			dataLoaderCronJobModel.setTriggerMode(DataLoadTriggerMode.STARTDELTA);
			triggerMode = dataLoaderCronJobModel.getTriggerMode();
		}

		return startDeltaLoad(triggerMode, sapConfiguration, dataloaderManager);
	}

	/**
	 * Tries to start delta load
	 *
	 * @param triggerMode
	 *           resume or startdelta, in other cases we throw an exception
	 * @param sapConfiguration
	 * @param dataloaderManager
	 * @return Result of load call
	 */
	protected PerformResult startDeltaLoad(final DataLoadTriggerMode triggerMode, final SAPConfigurationModel sapConfiguration,
			final DataloaderManager dataloaderManager)
	{
		if (triggerMode == DataLoadTriggerMode.RESUME || triggerMode == DataLoadTriggerMode.STARTDELTA)
		{
			// Check whether initial load has been done. Only in this case start the delta load
			if (!isDeltaLoadStartAllowed(sapConfiguration))
			{
				LOG.warn("Initial load has not been done. No delta load is possible");
				return new PerformResult(CronJobResult.DATALOAD_NO_INITIAL_DOWNLOAD, CronJobStatus.FINISHED);
			}
			else
			{
				dataLoader.performDeltaLoad(sapConfiguration, dataloaderManager, getModelService());
				return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
			}
		}
		else
		{
			//This leads to job status CronJobResult.ERROR, CronJobStatus.ABORTED
			throw new IllegalStateException("Non supported trigger mode");
		}
	}

	protected PerformResult checkForResume(final DataLoaderCronJobModel dataLoaderCronJobModel,
			final DataLoadTriggerMode triggerMode)
	{
		if (LOG.isDebugEnabled())
		{
			final Date timestamp = new Date();
			LOG.debug("Perform DataLoaderJob " + dataLoaderCronJobModel.getCode() + " " + timestamp + " " + triggerMode);
		}

		if (triggerMode == DataLoadTriggerMode.RESUME)
		{
			if (isResumePerformed())
			{
				LOG.debug("Resume was already performed");
				return new PerformResult(CronJobResult.DATALOAD_RESUME_ATTEMPT_DONE, CronJobStatus.FINISHED);
			}
			else
			{
				getDataLoaderManagerContainer().setResumePerformed(true);
			}
		}
		return null;
	}

	protected boolean isDeltaLoadStartAllowed(final SAPConfigurationModel sapConfiguration)
	{
		boolean startDeltaLoadAllowed = false;
		final CPQDataloadStatusModel cpqStatus = sapConfiguration.getSapproductconfig_cpqDataloadStatus();
		if (cpqStatus != null && cpqStatus.getCpqLastInitialLoadStartTime() != null
				&& cpqStatus.getCpqLastInitialLoadEndTime() != null)
		{
			final long startTime = cpqStatus.getCpqLastInitialLoadStartTime().getTime();
			final long endTime = cpqStatus.getCpqLastInitialLoadEndTime().getTime();
			if (startTime < endTime)
			{
				startDeltaLoadAllowed = true;
			}
		}
		return startDeltaLoadAllowed;
	}

	protected DataloaderManager initializeDataLoaderManager(final DataLoaderCronJobModel dataLoaderCronJobModel)
	{
		//The following check is not synchronized, as we cannot synchronize the entire process including writing the job status to the hybris persistence.
		//Worst case scenario: Two parallel AbstractJobPerformables reach this point, detect that another job runs, and quit execution.
		//In this case the log files tell the actual root cause.
		if (isAbortNeeded(dataLoaderCronJobModel))
		{
			return null;
		}

		// Prepare Dataload
		final DataloaderManager dataloaderManager = prepareDataloadManager(dataLoaderCronJobModel);
		getDataLoaderManagerContainer().setDataLoaderManager(dataloaderManager);

		return dataloaderManager;
	}

	protected boolean isAbortNeeded(final DataLoaderCronJobModel dataLoaderCronJobModel)
	{
		boolean needToAbort = false;
		// Verify if another dataload job is already running
		final String currentJobCode = dataLoaderCronJobModel.getCode();

		final List<CronJobModel> cjList = getCronJobService().getRunningOrRestartedCronJobs();
		for (final CronJobModel cj : cjList)
		{
			if (cj instanceof DataLoaderCronJobModel && !cj.getCode().equalsIgnoreCase(currentJobCode))
			{
				LOG.info("Dataload job start aborted while another dataload job is running already");
				needToAbort = true;
			}
		}
		return needToAbort;
	}

	protected DataloaderManager prepareDataloadManager(final DataLoaderCronJobModel dataLoaderCronJobModel)
	{
		final SAPConfigurationModel sapConfiguration = dataLoaderCronJobModel.getSapConfiguration();

		// Prepare data load parameters
		final DataLoaderConfigurationHelper dlConfHelper = getDataLoaderConfigurationHelper();
		final DataloaderSourceParameters params = dlConfHelper.getDataloaderSourceParam(sapConfiguration);

		final Map<String, String> dataloaderConfigMap = dlConfHelper.createConfigMap(params);

		// Filter files
		dlConfHelper.prepareFilterFiles(dataloaderConfigMap, sapConfiguration);

		// Set up dataloader manager
		final DataloaderConfiguration dataloaderConfigurationWrapper = new DataloaderConfiguration(true, true, true, true, true,
				(HashMap<String, String>) dataloaderConfigMap);
		final IDataloaderConfiguration dataloaderConfiguration = dataloaderConfigurationWrapper.getConfiguration();
		final DataloaderManager dataloaderManager = createDataloaderManager(dataloaderConfiguration);

		// Add dataloader event listeners
		final DefaultDataloaderProgressListenerImpl newProgressListener = getProgressListener();
		newProgressListener.setSapConfiguration(sapConfiguration);
		dataloaderManager.setOrReplaceProgressListener(newProgressListener);
		dataloaderManager.setOrReplaceMessageListener(getMessageListener());

		return dataloaderManager;
	}

	protected DataloaderManager createDataloaderManager(final IDataloaderConfiguration dataloaderConfiguration)
	{
		return new DataloaderManagerImpl(dataloaderConfiguration, new DataloaderWorkerHybris());
	}

	/**
	 * @return the dataLoaderManagerContainer
	 */
	public DataLoaderManagerContainer getDataLoaderManagerContainer()
	{
		return dataLoaderManagerContainer;
	}

	/**
	 * @param dataLoaderManagerContainer
	 *           the dataLoaderManagerContainer to set
	 */
	public void setDataLoaderManagerContainer(final DataLoaderManagerContainer dataLoaderManagerContainer)
	{
		this.dataLoaderManagerContainer = dataLoaderManagerContainer;
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

	protected ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * Sets progress listener
	 *
	 * @param progressListener
	 */
	public void setProgressListener(final DefaultDataloaderProgressListenerImpl progressListener)
	{
		this.progressListener = progressListener;
	}

	/**
	 * Sets message listener
	 *
	 * @param messageListener
	 */
	public void setMessageListener(final DefaultDataloaderMessageListenerImpl messageListener)
	{
		this.messageListener = messageListener;
	}

	/**
	 * @param propertyAccessFacade
	 */
	public void setPropertyAccessFacade(final PropertyAccessFacade propertyAccessFacade)
	{
		this.propertyAccessFacade = propertyAccessFacade;
	}

	protected boolean isResumePerformed()
	{
		return getDataLoaderManagerContainer().isResumePerformed();
	}


	/**
	 * @param dataLoader
	 */
	public void setDataLoader(final DataLoader dataLoader)
	{
		this.dataLoader = dataLoader;
	}

	/**
	 * @return Configuration helper
	 */
	public DataLoaderConfigurationHelper getDataLoaderConfigurationHelper()
	{
		return dataLoaderConfigurationHelper;
	}

	/**
	 * Set configuration helper
	 *
	 * @param dataLoaderConfigurationHelper
	 */
	public void setDataLoaderConfigurationHelper(final DataLoaderConfigurationHelper dataLoaderConfigurationHelper)
	{
		this.dataLoaderConfigurationHelper = dataLoaderConfigurationHelper;
	}

}
