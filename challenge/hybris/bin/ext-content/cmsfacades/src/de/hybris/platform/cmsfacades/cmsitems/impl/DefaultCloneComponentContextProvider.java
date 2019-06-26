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
package de.hybris.platform.cmsfacades.cmsitems.impl;

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.SESSION_CLONE_COMPONENT_CONTEXT;

import de.hybris.platform.cmsfacades.cmsitems.CloneComponentContextProvider;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link CloneComponentContextProvider} responsible for storing (in a stack-like data
 * structure) context information per transaction when cloning a component.
 */
public class DefaultCloneComponentContextProvider implements CloneComponentContextProvider
{
	private SessionService sessionService;

	@Override
	public void initializeItem(final Entry<String, Object> entry)
	{
		Object value = getValueFromSession(false);
		if (value == null)
		{
			final Deque<ItemModel> stack = new LinkedList<>();
			value = new AtomicReference<>(stack);
			getSessionService().setAttribute(SESSION_CLONE_COMPONENT_CONTEXT, value);
		}
		getWrappedStack(value).push(entry);
	}

	@Override
	public boolean isInitialized()
	{
		return getValueFromSession(false) != null;
	}

	@Override
	public Entry<String, Object> getCurrentItem()
	{
		final Object value = getValueFromSession(true);
		return getWrappedStack(value).peek();
	}

	@Override
	public Object findItemForKey(final String key)
	{
		final Object value = getValueFromSession(false);
		return value == null ? value
				: getWrappedStack(value).stream().filter(entry -> entry.getKey().equals(key)).map(Entry::getValue)
				.findFirst().orElse(null);
	}

	@Override
	public void finalizeItem()
	{
		final Object value = getValueFromSession(true);
		getWrappedStack(value).pop();
	}

	/**
	 * Get the value stored in the session associated to the key {@code SESSION_CLONE_COMPONENT_CONTEXT}
	 *
	 * @param shouldThrowException
	 *           when set to <tt>TRUE</tt>, an {@link IllegalStateException} is thrown if no value is found in the
	 *           session
	 * @return the value stored in the session
	 */
	protected Object getValueFromSession(final boolean shouldThrowException)
	{
		final Object value = getSessionService().getAttribute(SESSION_CLONE_COMPONENT_CONTEXT);
		if (value == null && shouldThrowException)
		{
			throw new IllegalStateException(
					"There is no current entry. Please Initialize with #initializeItem before using this method.");
		}
		return value;
	}

	/**
	 * Values stored in the session service must be wrapped in AtomicReference objects to protect them from being altered
	 * during serialization. When values are read from the session service, they must be unwrapped. Thus, this method is
	 * used to retrieve the original value (stack) stored in the AtomicReference wrapper.
	 *
	 * @param rawValue
	 *           Object retrieved from the session service. The object must be an AtomicReference. Otherwise, an
	 *           IllegalStateException is thrown.
	 * @return stack stored within the AtomicReference.
	 */
	@SuppressWarnings("unchecked")
	protected Deque<Entry<String, Object>> getWrappedStack(final Object rawValue)
	{
		if (rawValue instanceof AtomicReference)
		{
			final AtomicReference<Deque<Entry<String, Object>>> originalValue = (AtomicReference<Deque<Entry<String, Object>>>) rawValue;
			return originalValue.get();
		}
		throw new IllegalStateException(
				"Session object for SESSION_CLONE_ITEM_CONTEXT should hold a reference of AtomicReference object.");
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

}
