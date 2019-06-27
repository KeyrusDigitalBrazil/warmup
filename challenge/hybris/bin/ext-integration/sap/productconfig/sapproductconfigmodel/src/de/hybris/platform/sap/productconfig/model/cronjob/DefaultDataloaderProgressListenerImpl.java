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

import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.productconfig.model.enums.DataloadStatus;
import de.hybris.platform.sap.productconfig.model.model.CPQDataloadStatusModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

import com.sap.custdev.projects.fbs.slc.dataloader.standalone.manager.DataloaderProgressListener;
import com.sap.custdev.projects.fbs.slc.dataloader.standalone.manager.DataloaderQueuesInfo;
import com.sap.custdev.projects.fbs.slc.dataloader.standalone.manager.Progress;


/**
 * Listens to data loader progress and persists the data
 */
public class DefaultDataloaderProgressListenerImpl implements DataloaderProgressListener
{
	private static final Logger LOG = Logger.getLogger(DefaultDataloaderProgressListenerImpl.class);
	private SAPConfigurationModel sapConfiguration;

	private ModelService modelService;


	@Override
	public void progressReported(final Progress progress)
	{
		if (sapConfiguration == null)
		{
			throw new IllegalStateException("We need an instance of SAPConfiguration to proceed");
		}

		final double totalDataReceivedInMegabytes = progress.getTotalDataReceivedInMegabytes();
		BigDecimal totalDataReceivedInMegabytesAsBigDecimal = BigDecimal.valueOf(totalDataReceivedInMegabytes);
		totalDataReceivedInMegabytesAsBigDecimal = totalDataReceivedInMegabytesAsBigDecimal.setScale(3, BigDecimal.ROUND_HALF_UP);


		final DataloaderQueuesInfo dataloaderQueuesInfo = progress.getDataloaderQueuesInfo();
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Progress MB: " + totalDataReceivedInMegabytes);
			LOG.debug("Progress Rate: " + progress.getProcessingRatePerHourInMegabytes());
			LOG.debug("Progress Queue Info: " + dataloaderQueuesInfo);
		}

		final CPQDataloadStatusModel datloadStatus = sapConfiguration.getSapproductconfig_cpqDataloadStatus();



		if (datloadStatus.getCpqDataloadStatus() == DataloadStatus.INITIAL_LOAD)
		{
			datloadStatus.setCpqCurrentInitialLoadTransferredVolume(totalDataReceivedInMegabytesAsBigDecimal);
		}
		else
		{
			datloadStatus.setCpqCurrentDeltaLoadTransferredVolume(totalDataReceivedInMegabytesAsBigDecimal);
			final int entriesInAllQueues = dataloaderQueuesInfo.getEntriesInConditionQueue() + //
					dataloaderQueuesInfo.getEntriesInCustomizingQueue() + //
					dataloaderQueuesInfo.getEntriesInKbQueue() + //
					dataloaderQueuesInfo.getEntriesInMaterialQueue();
			datloadStatus.setCpqNumberOfEntriesInDeltaLoadQueue(Integer.valueOf(entriesInAllQueues));
		}

		getModelService().save(datloadStatus);
	}

	/**
	 * @return the sapConfiguration
	 */
	public SAPConfigurationModel getSapConfiguration()
	{
		return sapConfiguration;
	}

	/**
	 * @param sapConfiguration
	 *           the sapConfiguration to set
	 */
	public void setSapConfiguration(final SAPConfigurationModel sapConfiguration)
	{
		this.sapConfiguration = sapConfiguration;
	}

	/**
	 * @param modelService
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}
}
