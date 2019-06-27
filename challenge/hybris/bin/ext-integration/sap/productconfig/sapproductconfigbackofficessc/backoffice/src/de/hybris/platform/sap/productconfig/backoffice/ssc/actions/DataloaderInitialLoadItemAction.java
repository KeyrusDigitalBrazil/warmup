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
package de.hybris.platform.sap.productconfig.backoffice.ssc.actions;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.core.configuration.model.SAPRFCDestinationModel;
import de.hybris.platform.sap.productconfig.model.enums.DataLoadTriggerMode;
import de.hybris.platform.sap.productconfig.model.enums.DataloadStatus;
import de.hybris.platform.sap.productconfig.model.intf.DataLoaderCronjobParameters;
import de.hybris.platform.sap.productconfig.model.model.CPQDataloadStatusModel;
import de.hybris.platform.sap.productconfig.model.model.DataLoaderCronJobModel;
import de.hybris.platform.sap.productconfig.model.model.DataLoaderStopCronJobModel;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.internal.model.ServicelayerJobModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.EnumSet;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.zkoss.zul.Messagebox;

import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;
import com.hybris.cockpitng.actions.ActionResult.StatusFlag;
import com.hybris.cockpitng.actions.CockpitAction;


/**
 * Perform initial load action
 */
public class DataloaderInitialLoadItemAction implements CockpitAction<SAPConfigurationModel, String>
{
	@Resource(name = "cronJobService")
	private CronJobService cronJobService;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "sapProductConfigDataLoaderCronjobParameters")
	private DataLoaderCronjobParameters dataLoaderCronJobParameters;

	private static final Logger LOG = Logger.getLogger(DataloaderInitialLoadItemAction.class);

	@Override
	public ActionResult<String> perform(final ActionContext<SAPConfigurationModel> ctx)
	{
		final ActionResult<String> result = new ActionResult<>(ActionResult.SUCCESS);

		final SAPConfigurationModel configuration = ctx.getData();

		checkParameters(ctx, result, configuration);

		if (result.getResultCode().equals(ActionResult.ERROR))
		{
			return result;
		}


		if (isDataLoadRunning())
		{
			final boolean stopJobResult = stopDataload();

			if (!stopJobResult)
			{
				result.setResultCode(ActionResult.ERROR);
				showMessageBox(ctx.getLabel("text.sapproductconfig_running_dataloadjob_not_stopped"));
				return result;
			}
		}

		CPQDataloadStatusModel dataloadStatus = configuration.getSapproductconfig_cpqDataloadStatus();
		if (dataloadStatus == null)
		{
			dataloadStatus = new CPQDataloadStatusModel();
			dataloadStatus.setCpqDataloadStatusForSapConfiguration(configuration.getCore_name());
			dataloadStatus.setOwner(configuration);
			dataloadStatus.setCpqDataloadStatus(DataloadStatus.NOT_STARTED);
			getModelService().save(dataloadStatus);
			configuration.setSapproductconfig_cpqDataloadStatus(dataloadStatus);
			getModelService().save(configuration);
		}

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Try to submit initial Load for SAPConfiguration " + configuration.getCore_name());
		}

		// Submit initial Load
		final DataLoaderCronJobModel cronJobModel = getModelService().create(DataLoaderCronJobModel.class);

		cronJobModel.setSapConfiguration(configuration);
		cronJobModel.setTriggerMode(DataLoadTriggerMode.STARTINITIAL);

		final ServicelayerJobModel jobModel = (ServicelayerJobModel) getCronJobService().getJob(
				getDataLoaderCronJobParameters().getDataloadStartJobBeanId());


		cronJobModel.setJob(jobModel);

		final Integer nodeIdForStartJob = getDataLoaderCronJobParameters().retrieveNodeIdForStartJob();
		if (nodeIdForStartJob != null)
		{
			cronJobModel.setNodeID(nodeIdForStartJob);
		}

		getModelService().save(cronJobModel);

		getCronJobService().performCronJob(cronJobModel, false);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Initial Load for SAPConfiguration " + configuration.getCore_name() + " is submitted");
		}

		result.setResultCode(ActionResult.SUCCESS);
		// trigger refresh
		final EnumSet<StatusFlag> statusFlags = result.getStatusFlags();
		statusFlags.add(StatusFlag.OBJECT_MODIFIED);
		result.setStatusFlags(statusFlags);

		showMessageBox(ctx.getLabel("text.sapproductconfig_initial_load_successful"));
		return result;
	}


	protected void checkParameters(final ActionContext<SAPConfigurationModel> ctx, final ActionResult<String> result,
			final SAPConfigurationModel configuration)
	{
		// Verify if required configuration parameters are maintained

		boolean isError = false;
		final boolean isDestinationAvailable = configuration != null && configuration.getSapproductconfig_sapServer() != null;

		if (isDestinationAvailable)
		{
			final SAPRFCDestinationModel destinationModel = configuration.getSapproductconfig_sapServer();

			if (destinationModel.getRfcDestinationName() == null || destinationModel.getRfcDestinationName().isEmpty()
					|| configuration.getSapproductconfig_sapRFCDestination() == null
					|| configuration.getSapproductconfig_sapRFCDestination().isEmpty())
			{
				isError = true;
			}
		}

		if (isError || (!isDestinationAvailable))
		{
			result.setResultCode(ActionResult.ERROR);
			showMessageBox(ctx.getLabel("text.sapproductconfig_configuration_not_set"));
		}
	}


	protected void showMessageBox(final String msg)
	{
		Messagebox.show(msg);
	}


	protected boolean stopDataload()
	{
		final DataLoaderStopCronJobModel stopCronJobModel = getModelService().create(DataLoaderStopCronJobModel.class);
		final String stopJobId = getDataLoaderCronJobParameters().getDataloadStopJobBeanId();
		final ServicelayerJobModel jobModel = (ServicelayerJobModel) getCronJobService().getJob(stopJobId);
		stopCronJobModel.setJob(jobModel);

		final Integer nodeIdForStopJob = getDataLoaderCronJobParameters().retrieveNodeIdForStopJob();
		if (nodeIdForStopJob != null)
		{
			stopCronJobModel.setNodeID(nodeIdForStopJob);
		}

		getModelService().save(stopCronJobModel);

		// perform stop job synchronously
		getCronJobService().performCronJob(stopCronJobModel, true);

		final CronJobResult stopJobResult = stopCronJobModel.getResult();

		if (stopJobResult != CronJobResult.SUCCESS)
		{
			return false;
		}

		// wait before start job is stopped, but not longer than 3 sec
		for (int i = 0; i < 30; i++)
		{
			if (!isDataLoadRunning())
			{
				break;
			}
			try
			{
				Thread.sleep(100);
			}
			catch (final InterruptedException e)
			{
				Thread.currentThread().interrupt();
			}
		}

		return true;
	}


	protected boolean isDataLoadRunning()
	{
		// Verify if dataload job is running already
		final List<CronJobModel> cjList = getCronJobService().getRunningOrRestartedCronJobs();
		for (final CronJobModel cj : cjList)
		{
			if (cj instanceof DataLoaderCronJobModel)
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canPerform(final ActionContext<SAPConfigurationModel> arg0)
	{
		return true;
	}

	@Override
	public String getConfirmationMessage(final ActionContext<SAPConfigurationModel> ctx)
	{
		return ctx.getLabel("text.sapproductconfig_initial_load_start_confirmation");
	}

	@Override
	public boolean needsConfirmation(final ActionContext<SAPConfigurationModel> ctx)
	{
		return isDataLoadRunning();
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

	/**
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * @return the dataLoaderCronJobParameters
	 */
	public DataLoaderCronjobParameters getDataLoaderCronJobParameters()
	{
		return dataLoaderCronJobParameters;
	}


	/**
	 * @param dataLoaderCronJobParameters
	 */
	public void setDataLoaderCronJobParameters(final DataLoaderCronjobParameters dataLoaderCronJobParameters)
	{
		this.dataLoaderCronJobParameters = dataLoaderCronJobParameters;
	}

}
