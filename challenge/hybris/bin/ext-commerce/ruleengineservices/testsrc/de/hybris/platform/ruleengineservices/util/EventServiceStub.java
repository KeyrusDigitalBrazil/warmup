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
package de.hybris.platform.ruleengineservices.util;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.stream;

import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;

import org.springframework.context.ApplicationListener;


public class EventServiceStub implements EventService
{

	private static final Long DELAY = new Long(1000);
	private Set<ApplicationListener> listeners = newHashSet();

	@Override
	public void publishEvent(final AbstractEvent event)
	{
		listeners.forEach(listener -> fireWithDelayIfEventTypeMatches(listener, event));
	}

	@Override
	public boolean registerEventListener(final ApplicationListener applicationListener)
	{
		if (!listeners.contains(applicationListener))
		{
			listeners.add(applicationListener);
			return true;
		}
		return false;
	}

	@Override
	public boolean unregisterEventListener(final ApplicationListener applicationListener)
	{
		if (listeners.contains(applicationListener))
		{
			listeners.remove(applicationListener);
			return true;
		}
		return false;
	}

	@Override
	public Set<ApplicationListener> getEventListeners()
	{
		return listeners;
	}

	private static void delay()
	{
		try
		{
			Thread.sleep(DELAY);
		}
		catch (InterruptedException e)
		{
			Thread.currentThread().interrupt();
		}
	}

	private void fireWithDelayIfEventTypeMatches(final ApplicationListener listener, final AbstractEvent event)
	{
		if (matchEventType(listener, event))
		{
			new Thread(() ->
			{
				delay();
				listener.onApplicationEvent(event);
			}).start();
		}
	}

	private boolean matchEventType(final ApplicationListener listener, final AbstractEvent event)
	{
		final Type[] genericInterfaces = listener.getClass().getGenericInterfaces();
		for (Type genericInterface : genericInterfaces)
		{
			if (genericInterface instanceof ParameterizedType)
			{
				ParameterizedType parameterizedTypeInterface = (ParameterizedType) genericInterface;
				final Type[] typeArguments = parameterizedTypeInterface.getActualTypeArguments();
				return stream(typeArguments).anyMatch(t -> t.getTypeName().equals(event.getClass().getName()));
			}
		}
		return false;
	}

}
