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
package com.sap.hybris.saprevenuecloudproduct.jobs;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.enums.ExportStatus;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.outboundservices.facade.OutboundServiceFacade;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import static de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService.RESPONSE_MESSAGE;
import static de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService.getPropertyValue;
import static de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService.isSentSuccessfully;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hybris.saprevenuecloudproduct.constants.SaprevenuecloudproductConstants;
import com.sap.hybris.saprevenuecloudproduct.enums.SapRevenueCloudReplicationModeEnum;
import com.sap.hybris.saprevenuecloudproduct.model.SapRevenueCloudProductCronjobModel;
import com.sap.hybris.saprevenuecloudproduct.service.SapRevenueCloudProductService;
import com.sap.hybris.scpiconnector.data.ResponseData;

import static de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService.*;

/**
 *
 */
public class SapRevenueCloudProductImportJob extends AbstractJobPerformable<SapRevenueCloudProductCronjobModel> {

	private final Logger LOG = LoggerFactory.getLogger(SapRevenueCloudProductImportJob.class);

	private ConfigurationService configurationService;
	private SapRevenueCloudProductService sapRevenueCloudProductService;
	private OutboundServiceFacade outboundServiceFacade;

	@Override
	public PerformResult perform(final SapRevenueCloudProductCronjobModel job) {
			// Check for the job replication mode and replication Date
			if (null != job.getReplicationMode()
					&& SapRevenueCloudReplicationModeEnum.DELTA.equals(job.getReplicationMode())) {
				Date lastSuccessRunTime = getSapRevenueCloudProductService()
						.getProductReplicationDateForCronjob(job.getCode());
				if (null == lastSuccessRunTime) {
					LOG.info(String.format(
							"Cannot find any successfull run history for the cronjob with code %s. Please perform atleast one FULL run",
							job.getCode()));
					return new PerformResult(CronJobResult.FAILURE, CronJobStatus.FINISHED);
				}
				job.setReplicationTime(lastSuccessRunTime);
			} else {
				job.setReplicationMode(SapRevenueCloudReplicationModeEnum.FULL);
			}
			// Send CronjobModel to SCPI
			getOutboundServiceFacade().send(job, "OutboundSapRevenueCloudProductCronjob", "scpiProductDestination")
					.subscribe(
							// onNext
							responseEntityMap -> {

								if (isSentSuccessfully(responseEntityMap)) {

									job.setResult(CronJobResult.SUCCESS);
									job.setStatus(CronJobStatus.FINISHED);
									LOG.info(String.format("Product replication has been successfully completed with message [%n%s]", getPropertyValue(responseEntityMap, RESPONSE_MESSAGE)));

								} else {
									job.setResult(CronJobResult.FAILURE);
									job.setStatus(CronJobStatus.FINISHED);
									LOG.info(String.format("Product replication failed with error message [%n%s]", getPropertyValue(responseEntityMap, RESPONSE_MESSAGE)));
								}

							}, error -> {
								job.setResult(CronJobResult.ERROR);
								job.setStatus(CronJobStatus.FINISHED);
								LOG.info(String.format("Product replication failed with message [%n%s]", error.getMessage()));
							}
					);
		return new PerformResult(job.getResult(),job.getStatus());
	}

	/**
	 * @return the configurationService
	 */
	public ConfigurationService getConfigurationService() {
		return configurationService;
	}

	/**
	 * @param configurationService the configurationService to set
	 */
	public void setConfigurationService(final ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	public SapRevenueCloudProductService getSapRevenueCloudProductService() {
		return sapRevenueCloudProductService;
	}

	public void setSapRevenueCloudProductService(SapRevenueCloudProductService sapRevenueCloudProductService) {
		this.sapRevenueCloudProductService = sapRevenueCloudProductService;
	}

	public OutboundServiceFacade getOutboundServiceFacade() {
		return outboundServiceFacade;
	}

	public void setOutboundServiceFacade(OutboundServiceFacade outboundServiceFacade) {
		this.outboundServiceFacade = outboundServiceFacade;
	}

}
