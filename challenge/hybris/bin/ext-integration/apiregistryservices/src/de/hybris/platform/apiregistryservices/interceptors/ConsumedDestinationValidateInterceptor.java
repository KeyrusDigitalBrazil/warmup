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

package de.hybris.platform.apiregistryservices.interceptors;

import de.hybris.platform.apiregistryservices.event.InvalidateCharonCacheEvent;
import de.hybris.platform.apiregistryservices.factory.ClientFactory;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.servicelayer.model.ModelService;

import org.springframework.beans.factory.annotation.Required;


public class ConsumedDestinationValidateInterceptor implements ValidateInterceptor<ConsumedDestinationModel>
{
	private ModelService modelService;
	private EventService eventService;
	private ClientFactory clientFactory;

	@Override
	public void onValidate(final ConsumedDestinationModel consumedDestination, final InterceptorContext interceptorContext)
			throws InterceptorException
	{
		if (!getModelService().isNew(consumedDestination))
		{
			eventService.publishEvent(new InvalidateCharonCacheEvent(getClientFactory().buildCacheKey(consumedDestination)));
		}
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

	protected ClientFactory getClientFactory()
	{
		return clientFactory;
	}

	@Required
	public void setClientFactory(final ClientFactory clientFactory)
	{
		this.clientFactory = clientFactory;
	}

	protected EventService getEventService()
	{
		return eventService;
	}

	@Required
	public void setEventService(final EventService eventService)
	{
		this.eventService = eventService;
	}
}
