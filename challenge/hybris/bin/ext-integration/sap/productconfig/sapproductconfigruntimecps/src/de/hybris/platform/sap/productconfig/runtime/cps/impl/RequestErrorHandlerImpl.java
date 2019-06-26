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
package de.hybris.platform.sap.productconfig.runtime.cps.impl;

import de.hybris.platform.sap.productconfig.runtime.cps.RequestErrorHandler;
import de.hybris.platform.sap.productconfig.runtime.cps.cache.CPSCache;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingDocumentResult;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationNotFoundException;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingEngineException;

import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.charon.exp.ClientException;
import com.hybris.charon.exp.HttpException;
import com.hybris.charon.exp.NotFoundException;

import rx.Observable;
import rx.schedulers.Schedulers;


/**
 * Default implementation of {@link RequestErrorHandler}. Just forwards the exceptions into runtime exceptions, a
 * specific exception text is raised for HTTP NOT FOUND
 */
public class RequestErrorHandlerImpl implements RequestErrorHandler
{

	private static final String PRICING_TIMEOUT_ERROR_MESSAGE = "Timeout exception occured during pricing call";
	private static final String CONFIGURATION_TIMEOUT_ERROR_MESSAGE = "Timeout exception occured during configuration call";
	private static final String NOT_FOUND_MESSAGE = "Entity could not be found. Please review the session timeout settings in hybris ECP and in CPS";
	private static final String OUTDATED_MESSAGE = "Optimistic Lock Error (412): Sent configuration is outdated. The provided eTag flag is not valid anymore.";
	private static final String MISSING_ETAG_MESSAGE = "Optimistic Lock Error (428): ETag has not been sent but it is a prerequisite for optimistic lock mechanism.";
	private static final Logger LOG = Logger.getLogger(RequestErrorHandlerImpl.class);
	protected static final String NO_SERVER_MESSAGE = "No message available";

	private CPSCache cache;

	@Override
	public void processUpdateConfigurationError(final HttpException ex, final String configId) throws ConfigurationEngineException
	{
		logRequestError("cps", ex);
		cleanUpCache(configId);
		checkNotFound(ex);
		checkClientException412(ex);
		checkClientException428(ex);
		throw new ConfigurationEngineException("Update configuration failed, please see server log for more details", ex);
	}

	@Override
	public CPSConfiguration processCreateDefaultConfigurationError(final HttpException ex)
	{
		logRequestError("cps", ex);
		throw new IllegalStateException("Create default configuration failed, please see server log for more details", ex);
	}

	@Override
	public CPSConfiguration processGetConfigurationError(final HttpException ex, final String configId)
			throws ConfigurationEngineException
	{
		logRequestError("cps", ex);
		cleanUpCache(configId);
		checkNotFound(ex);
		throw new ConfigurationEngineException("Get configuration failed, please see server log for more details", ex);
	}

	@Override
	public void processDeleteConfigurationError(final HttpException ex)
	{
		logRequestError("cps", ex);
		ifNotFoundThrowIllegalState(ex);
		try
		{
			checkClientException412(ex);
		}
		catch (final ConfigurationEngineException cex)
		{
			throw new IllegalStateException("Delete configuration failed, please see server log for more details", cex);
		}
		checkClientException428(ex);
		throw new IllegalStateException("Delete configuration failed, please see server log for more details", ex);
	}

	@Override
	public CPSExternalConfiguration processGetExternalConfigurationError(final HttpException ex, final String configId)
			throws ConfigurationEngineException
	{
		logRequestError("cps", ex);
		checkNotFound(ex);
		throw new ConfigurationEngineException("Get external configuration failed, please see server log for more details", ex);
	}

	@Override
	public CPSConfiguration processCreateRuntimeConfigurationFromExternalError(final HttpException ex)
	{
		logRequestError("cps", ex);
		throw new IllegalStateException(
				"Create runtime configuration from external configuration failed, please see server log for more details", ex);
	}

	@Override
	public PricingDocumentResult processCreatePricingDocumentError(final HttpException ex) throws PricingEngineException
	{
		logRequestError("pricing", ex);
		throw new PricingEngineException("Create pricing document failed, please see server log for more details", ex);
	}

	@Override
	public boolean processHasKbError(final HttpException ex)
	{
		logRequestError("cps", ex);
		throw new IllegalStateException("Finding kb request failed, please see server log for more details", ex);
	}

	protected void checkNotFound(final HttpException ex) throws ConfigurationEngineException
	{
		if (ex instanceof NotFoundException)
		{
			throw new ConfigurationNotFoundException(NOT_FOUND_MESSAGE, ex);
		}
	}

	protected void ifNotFoundThrowIllegalState(final HttpException ex)
	{
		if (ex instanceof NotFoundException)
		{
			final ConfigurationNotFoundException cause = new ConfigurationNotFoundException(NOT_FOUND_MESSAGE, ex);
			throw new IllegalStateException(NOT_FOUND_MESSAGE, cause);
		}
	}

	protected void checkClientException412(final HttpException ex) throws ConfigurationEngineException
	{
		if (ex instanceof ClientException && ex.getCode() == 412)
		{
			throw new ConfigurationEngineException(OUTDATED_MESSAGE, ex);
		}
	}


	protected void checkClientException428(final HttpException ex)
	{
		if (ex instanceof ClientException && ex.getCode() == 428)
		{
			throw new IllegalStateException(MISSING_ETAG_MESSAGE, ex);
		}
	}


	protected void logRequestError(final String serviceName, final HttpException ex)
	{
		LOG.error("While calling the " + serviceName + " service the following server error occured:" + getServerMessage(ex));
	}

	protected String getServerMessage(final HttpException ex)
	{
		final Observable<String> serverMessage = ex.getServerMessage();
		if (serverMessage == null)
		{
			return NO_SERVER_MESSAGE;
		}
		return serverMessage.subscribeOn(Schedulers.io()).toBlocking().first();
	}

	@Override
	public PricingDocumentResult processCreatePricingDocumentRuntimeException(final RuntimeException ex)
			throws PricingEngineException
	{
		final Throwable cause = ex.getCause();
		if (cause instanceof TimeoutException)
		{
			LOG.error(PRICING_TIMEOUT_ERROR_MESSAGE, cause);
			throw new PricingEngineException(PRICING_TIMEOUT_ERROR_MESSAGE, cause);
		}
		throw ex;
	}

	@Override
	public void processConfigurationRuntimeException(final RuntimeException e, final String configId)
			throws ConfigurationEngineException
	{
		cleanUpCache(configId);
		final Throwable cause = e.getCause();
		if (cause instanceof TimeoutException)
		{
			LOG.error(CONFIGURATION_TIMEOUT_ERROR_MESSAGE, cause);
			throw new ConfigurationEngineException(CONFIGURATION_TIMEOUT_ERROR_MESSAGE, cause);
		}
		throw e;
	}

	protected void cleanUpCache(final String configId)
	{
		getCache().removeConfiguration(configId);
		getCache().removeCookies(configId);
	}

	protected CPSCache getCache()
	{
		return cache;
	}

	@Required
	public void setCache(final CPSCache cache)
	{
		this.cache = cache;
	}

}
