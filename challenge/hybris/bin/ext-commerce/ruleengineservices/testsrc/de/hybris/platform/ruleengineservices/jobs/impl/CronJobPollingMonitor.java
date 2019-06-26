/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.ruleengineservices.jobs.impl;

import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.ruleengineservices.model.RuleEngineCronJobModel;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.function.Supplier;


public abstract class CronJobPollingMonitor implements Runnable
{

	private Supplier<RuleEngineCronJobModel> ruleEngineCronJobModelSupplier;
	private CronJobService cronJobService;
	private ModelService modelService;

	public CronJobPollingMonitor(final CronJobService cronJobService, final ModelService modelService, final Supplier<RuleEngineCronJobModel> ruleEngineCronJobModelSupplier)
	{
		this.cronJobService = cronJobService;
		this.modelService = modelService;
		this.ruleEngineCronJobModelSupplier = ruleEngineCronJobModelSupplier;
	}

	@Override
	public void run()
	{
		try
		{
			final RuleEngineCronJobModel cronJob = ruleEngineCronJobModelSupplier.get();
			waitTillCronJobFinished(cronJob.getCode());
		}
		finally
		{
			onCronJobFinished();
		}
	}

	protected void waitTillCronJobFinished(final String cronJobCode)
	{
		try
		{
			Thread.sleep(5000);
			final CronJobModel cronJob =  cronJobService.getCronJob(cronJobCode);
			modelService.refresh(cronJob);
			while (cronJobService.isRunning(cronJob))
			{
				Thread.sleep(1000);
				modelService.refresh(cronJob);
			}
		}
		catch (final InterruptedException e)
		{
			Thread.currentThread().interrupt();
		}
		finally
		{
			onCronJobFinished();
		}
	}

	abstract void onCronJobFinished();

}
