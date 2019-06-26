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
package com.hybris.ymkt.recommendation.cronjobs;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.ymkt.recommendation.services.ImpressionService;


public class AggregateRecoImpressionsJob extends AbstractJobPerformable<CronJobModel>
{
	private static final Logger LOGGER = LoggerFactory.getLogger(AggregateRecoImpressionsJob.class);
	protected ImpressionService impressionService;

	@Override
	public PerformResult perform(final CronJobModel cronJobModel)
	{
		LOGGER.info("{} started at {}", cronJobModel.getCode(), cronJobModel.getStartTime());

		try
		{
			impressionService.aggregateImpressions();
			LOGGER.info("{} completed in {}ms", cronJobModel.getCode(),
					System.currentTimeMillis() - cronJobModel.getStartTime().getTime());
			return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
		}
		catch (final RuntimeException e)
		{
			LOGGER.error("Error occurred during impressions aggregation.", e);
			return new PerformResult(CronJobResult.ERROR, CronJobStatus.ABORTED);
		}
	}

	@Required
	public void setImpressionService(final ImpressionService impressionService)
	{
		this.impressionService = impressionService;
	}

}
