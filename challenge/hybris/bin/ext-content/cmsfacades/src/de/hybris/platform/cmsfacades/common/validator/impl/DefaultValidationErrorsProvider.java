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
package de.hybris.platform.cmsfacades.common.validator.impl;

import de.hybris.platform.cmsfacades.common.validator.ValidationErrors;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrorsProvider;
import de.hybris.platform.cmsfacades.constants.CmsfacadesConstants;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link ValidationErrorsProvider}. Stores the {@link ValidationErrors} instance on the
 * current Session.
 */
public class DefaultValidationErrorsProvider implements ValidationErrorsProvider
{
	private ObjectFactory<ValidationErrors> validationErrorsObjectFactory;

	private SessionService sessionService;

	private final ReentrantLock lock = new ReentrantLock();

	@Override
	public ValidationErrors initializeValidationErrors()
	{
		final ValidationErrors validationErrors = getValidationErrorsObjectFactory().getObject();
		lock.lock();
		try
		{
			Object value = getSessionService().getAttribute(CmsfacadesConstants.SESSION_VALIDATION_ERRORS_OBJ);
			if (value == null)
			{
				final Deque<ValidationErrors> stack = new LinkedList<>();
				value = new AtomicReference<>(stack);
			}
			else
			{
				final Deque<ValidationErrors> stack = getWrappedStack(value);
				if (!stack.isEmpty())
				{
					validationErrors.pushField(stack.peek().parseFieldStack());
				}
			}
			getWrappedStack(value).push(validationErrors);

			// Lists stored in the session service are serialized and modified. When they are retrieved, the result is a
			// Collections$UnmodifiableRandomAccessList. To prevent this from happening the collection is wrapped in the AtomicReference.
			getSessionService().setAttribute(CmsfacadesConstants.SESSION_VALIDATION_ERRORS_OBJ, value);
		}
		finally
		{
			lock.unlock();
		}
		return validationErrors;
	}

	@Override
	public ValidationErrors getCurrentValidationErrors()
	{
		lock.lock();
		try
		{
			final Object value = getSessionService().getAttribute(CmsfacadesConstants.SESSION_VALIDATION_ERRORS_OBJ);
			if (value == null)
			{
				throw new IllegalStateException(
						"There is no current validation error context. Please Initialize with #initializeValidationErrors before using this method.");
			}
			else
			{
				return getWrappedStack(value).peek();
			}
		}
		finally
		{
			lock.unlock();
		}
	}

	@Override
	public void finalizeValidationErrors()
	{
		lock.lock();
		try
		{
			final Object value = getSessionService().getAttribute(CmsfacadesConstants.SESSION_VALIDATION_ERRORS_OBJ);
			if (value == null)
			{
				throw new IllegalStateException(
						"There is no current validation error context. Please Initialize with #initializeValidationErrors before using this method.");
			}
			else
			{
				getWrappedStack(value).pop();
			}
		}
		finally
		{
			lock.unlock();
		}
	}

	@Override
	public void collectValidationErrors(final ValidationException e, final Optional<String> language,
			final Optional<Integer> position)
	{
		e.getValidationErrors().getValidationErrors().forEach(validationError -> {
			language.ifPresent(validationError::setLanguage);
			position.ifPresent(validationError::setPosition);
			this.getCurrentValidationErrors().add(validationError);
		});
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
	protected Deque<ValidationErrors> getWrappedStack(final Object rawValue)
	{
		if (rawValue instanceof AtomicReference)
		{
			final AtomicReference<Deque<ValidationErrors>> originalValue = (AtomicReference<Deque<ValidationErrors>>) rawValue;
			return originalValue.get();
		}
		throw new IllegalStateException(
				"Session object for SESSION_VALIDATION_ERRORS_OBJ should hold a reference of AtomicReference object.");
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

	protected ObjectFactory<ValidationErrors> getValidationErrorsObjectFactory()
	{
		return validationErrorsObjectFactory;
	}

	@Required
	public void setValidationErrorsObjectFactory(final ObjectFactory<ValidationErrors> validationErrorsObjectFactory)
	{
		this.validationErrorsObjectFactory = validationErrorsObjectFactory;
	}
}
