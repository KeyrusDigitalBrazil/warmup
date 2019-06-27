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
import de.hybris.platform.apiregistryservices.model.ConsumedOAuthCredentialModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.RemoveInterceptor;

import org.springframework.beans.factory.annotation.Required;


public class ConsumedOAuthCredentialRemoveInterceptor implements RemoveInterceptor<ConsumedOAuthCredentialModel>
{

	private ClientFactory clientFactory;
	private EventService eventService;

	@Override
	public void onRemove(final ConsumedOAuthCredentialModel consumedOAuthCredential, final InterceptorContext interceptorContext)
			throws InterceptorException
	{
		eventService.publishEvent(new InvalidateCharonCacheEvent(consumedOAuthCredential.getId()));
	}

	protected ClientFactory getClientFactory()
	{
		return clientFactory;
	}

	@Required
	public void setClientFactory(final ClientFactory charonFactory)
	{
		this.clientFactory = charonFactory;
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
