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
package de.hybris.platform.assertions;

import org.assertj.core.api.AbstractAssert;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.PerformResult;


public class CronJobResultAssert extends AbstractAssert<CronJobResultAssert, PerformResult>
{
	public CronJobResultAssert(PerformResult actual)
	{
		super(actual, CronJobResultAssert.class);
	}

	public static CronJobResultAssert assertThat(PerformResult actual)
	{
		return new CronJobResultAssert(actual);
	}

	public CronJobResultAssert failed()
	{
		isNotNull();
		if (!CronJobResult.FAILURE.equals(actual.getResult()))
		{
			failWithMessage("Expected cron job result should be <%s> but was <%s>", CronJobResult.FAILURE, actual.getResult());
		}
		return this;
	}

	public CronJobResultAssert succeded()
	{
		isNotNull();
		if (!CronJobResult.SUCCESS.equals(actual.getResult()))
		{
			failWithMessage("Expected cron job result should be <%s> but was <%s>", CronJobResult.SUCCESS, actual.getResult());
		}
		return this;
	}


	public CronJobResultAssert aborted()
	{
		isNotNull();
		if (!CronJobStatus.ABORTED.equals(actual.getStatus()))
		{
			failWithMessage("Expected cron job status should be <%s> but was <%s>", CronJobStatus.ABORTED, actual.getStatus());
		}
		return this;
	}


	public CronJobResultAssert finished()
	{
		isNotNull();
		if (!CronJobStatus.FINISHED.equals(actual.getStatus()))
		{
			failWithMessage("Expected cron job status should be <%s> but was <%s>", CronJobStatus.FINISHED, actual.getStatus());
		}
		return this;
	}



}
