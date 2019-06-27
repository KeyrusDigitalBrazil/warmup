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
package de.hybris.platform.apiregistryservices.services.impl;

import de.hybris.platform.apiregistryservices.dto.EventExportDeadLetterData;
import de.hybris.platform.apiregistryservices.model.EventExportDeadLetterModel;
import de.hybris.platform.apiregistryservices.services.EventDlqService;
import de.hybris.platform.servicelayer.model.ModelService;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link EventDlqService}
 */
public class DefaultEventDlqService implements EventDlqService
{
	private ModelService modelService;

	@Override
	public void sendToQueue(final EventExportDeadLetterData data)
	{
		final EventExportDeadLetterModel letter = getModelService().create(EventExportDeadLetterModel.class);
		letter.setDestinationTarget(data.getDestinationTarget());
		letter.setDestinationChannel(data.getDestinationTarget().getDestinationChannel());
		letter.setError(data.getError());
		letter.setId(data.getId());
		letter.setEventType(data.getEventType());
		letter.setPayload(data.getPayload());
		letter.setTimestamp(data.getTimestamp());
		getModelService().save(letter);
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}
}
