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
package de.hybris.platform.acceleratorservices.dataexport.generic.impl;

import de.hybris.platform.acceleratorservices.dataexport.generic.event.UploadDataEvent;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.event.EventService;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link AbstractJobPerformable}.
 */
public class DefaultUploadDataJobPerformable extends AbstractJobPerformable
{
	private EventService eventService;

	protected EventService getEventService()
	{
		return eventService;
	}

	@Required
	public void setEventService(final EventService eventService)
	{
		this.eventService = eventService;
	}


	@Override
	public PerformResult perform(final CronJobModel cronJob)
	{
		final UploadDataEvent event = new UploadDataEvent();
		getEventService().publishEvent(event);
		return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
	}
}
