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
package com.hybris.ymkt.clickstream.listeners;

import de.hybris.eventtracking.model.events.AbstractProductAwareTrackingEvent;
import de.hybris.eventtracking.model.events.AbstractTrackingEvent;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.ymkt.clickstream.services.ClickStreamService;
import com.hybris.ymkt.common.consent.YmktConsentService;
import com.hybris.ymkt.common.constants.SapymktcommonConstants;
import com.hybris.ymkt.common.user.UserContextService;


public class ClickStreamListener extends AbstractEventListener<AbstractTrackingEvent>
{
	private static final Logger LOG = LoggerFactory.getLogger(ClickStreamListener.class);

	protected static final Predicate<String> STRING_IS_EMPTY = String::isEmpty;
	protected static final Predicate<String> STRING_IS_NOT_EMPTY = STRING_IS_EMPTY.negate();

	protected final Set<String> allowedEvents = new HashSet<>();
	protected final ConcurrentLinkedQueue<AbstractTrackingEvent> batchQueue = new ConcurrentLinkedQueue<>();
	protected int batchSize = 1;
	protected ClickStreamService clickStreamService;
	protected FlexibleSearchService flexibleSearchService;
	protected UserContextService userContextService;
	protected YmktConsentService ymktConsentService;

	protected AbstractTrackingEvent enrich(final AbstractTrackingEvent event)
	{
		event.setYmktContactId(event.getPiwikId());
		event.setYmktContactIdOrigin(this.userContextService.getAnonymousUserOrigin());

		final Optional<String> customerId = Optional.of(event).map(AbstractTrackingEvent::getUserId).filter(STRING_IS_NOT_EMPTY);
		if (customerId.isPresent())
		{
			try
			{
				final CustomerModel customer = new CustomerModel();
				customer.setCustomerID(customerId.get());
				final List<CustomerModel> customers = this.flexibleSearchService.getModelsByExample(customer);
				if (!customers.isEmpty())
				{
					event.setYmktContactId(customers.get(0).getCustomerID());
					event.setYmktContactIdOrigin(this.userContextService.getLoggedInUserOrigin());
				}
			}
			catch (final SystemException e)
			{
				LOG.warn("Error reading customer ID {}", customerId, e);
			}
		}
		return event;
	}

	protected boolean filterByAllowedEvents(final AbstractTrackingEvent event)
	{
		final String eventType = event.getEventType();
		return this.allowedEvents.contains(eventType);
	}

	protected boolean filterByConsent(final AbstractTrackingEvent event)
	{
		final String customerId = event.getUserId();
		return this.ymktConsentService.getUserConsent(customerId, SapymktcommonConstants.PERSONALIZATION_CONSENT_ID);
	}

	protected boolean filterByProductEvents(final AbstractTrackingEvent event)
	{
		if (event instanceof AbstractProductAwareTrackingEvent)
		{
			// Product events must have a product ID
			return Optional.of(event) //
					.map(AbstractProductAwareTrackingEvent.class::cast) //
					.map(AbstractProductAwareTrackingEvent::getProductId) //
					.filter(STRING_IS_NOT_EMPTY).isPresent();
		}
		return true;
	}

	@Override
	protected void onEvent(final AbstractTrackingEvent event)
	{
		Optional.of(event) //
				.filter(this::filterByAllowedEvents) //
				.filter(this::filterByProductEvents) //
				.filter(this::filterByConsent) //
				.map(this::enrich) //
				.ifPresent(this.batchQueue::offer);
		this.prepareBatchEvents().ifPresent(this.clickStreamService::sendEvents);
	}

	protected Optional<List<AbstractTrackingEvent>> prepareBatchEvents()
	{
		if (this.batchQueue.size() < this.batchSize)
		{
			return Optional.empty();
		}

		synchronized (this.batchQueue)
		{
			if (this.batchQueue.size() < this.batchSize)
			{
				return Optional.empty();
			}
			return Optional.of(Stream.generate(this.batchQueue::poll).limit(this.batchSize).collect(Collectors.toList()));
		}
	}

	@Required
	public void setAllowedEvents(final List<String> allowedEvents)
	{
		LOG.debug("allowedEvents={}", allowedEvents);
		this.allowedEvents.clear();
		allowedEvents.stream().map(String::intern).forEach(this.allowedEvents::add);
	}

	@Required
	public void setBatchSize(final int batchSize)
	{
		LOG.debug("batchSize={}", batchSize);
		this.batchSize = Math.max(1, batchSize);
	}

	@Required
	public void setClickStreamService(final ClickStreamService clickStreamService)
	{
		this.clickStreamService = Objects.requireNonNull(clickStreamService);
	}

	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = Objects.requireNonNull(flexibleSearchService);
	}

	@Required
	public void setUserContextService(final UserContextService userContextService)
	{
		this.userContextService = Objects.requireNonNull(userContextService);
	}

	@Required
	public void setYmktConsentService(final YmktConsentService ymktConsentService)
	{
		this.ymktConsentService = Objects.requireNonNull(ymktConsentService);
	}
}
