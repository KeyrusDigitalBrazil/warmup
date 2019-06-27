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

import de.hybris.platform.sap.productconfig.runtime.cps.CharonPricingFacade;
import de.hybris.platform.sap.productconfig.runtime.cps.RequestErrorHandler;
import de.hybris.platform.sap.productconfig.runtime.cps.client.PricingClient;
import de.hybris.platform.sap.productconfig.runtime.cps.client.PricingClientBase;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingDocumentInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingDocumentResult;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingEngineException;
import de.hybris.platform.yaasconfiguration.service.YaasServiceFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import rx.Scheduler;
import rx.schedulers.Schedulers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hybris.charon.exp.HttpException;


/**
 * Default implementation of {@link CharonPricingFacade}. Traces REST input as JSON
 */
public class CharonPricingFacadeImpl implements CharonPricingFacade
{
	private static final Logger LOG = Logger.getLogger(CharonPricingFacadeImpl.class);

	private PricingClientBase clientSetExternally = null;
	private RequestErrorHandler requestErrorHandler;
	private final Scheduler scheduler = Schedulers.io();
	private ObjectMapper objectMapper;
	private YaasServiceFactory yaasServiceFactory;
	private final CPSTimer timer = new CPSTimer();

	protected ObjectMapper getObjectMapper()
	{
		if (objectMapper == null)
		{
			objectMapper = new ObjectMapper();
		}
		return objectMapper;
	}

	@Override
	public PricingDocumentResult createPricingDocument(final PricingDocumentInput pricingInput) throws PricingEngineException
	{
		if (LOG.isDebugEnabled())
		{
			traceJsonRequestBody("Input for Pricing REST call: ", pricingInput);
		}
		try
		{
			timer.start("configPricing");
			final PricingDocumentResult pricingResult = getClient().createPricingDocument(pricingInput).subscribeOn(getScheduler())
					.toBlocking().first();
			timer.stop();
			if (LOG.isDebugEnabled())
			{
				traceJsonRequestBody("Output of Pricing REST call: ", pricingResult);
			}
			return pricingResult;
		}
		catch (final HttpException ex)
		{
			return getRequestErrorHandler().processCreatePricingDocumentError(ex);
		}
		catch (final RuntimeException ex)
		{
			return getRequestErrorHandler().processCreatePricingDocumentRuntimeException(ex);
		}
	}



	protected void traceJsonRequestBody(final String prefix, final Object obj)
	{
		try
		{
			LOG.debug(prefix + getObjectMapper().writeValueAsString(obj));
		}
		catch (final JsonProcessingException e)
		{
			LOG.warn("Could not trace " + prefix, e);
		}
	}

	protected PricingClientBase getClient()
	{
		if (clientSetExternally != null)
		{
			return clientSetExternally;
		}
		else
		{
			return getYaasServiceFactory().lookupService(PricingClient.class);
		}
	}


	/**
	 * Sets Charon client from outside (only for test purposes)
	 *
	 * @param newClient
	 *           Charon client representing REST calls for pricing
	 */
	public void setClient(final PricingClientBase newClient)
	{
		clientSetExternally = newClient;
	}

	protected Scheduler getScheduler()
	{
		return scheduler;
	}

	protected RequestErrorHandler getRequestErrorHandler()
	{
		return requestErrorHandler;
	}

	/**
	 * @param requestErrorHandler
	 *           For wrapping the http errors we receive from the REST service call
	 */
	@Required
	public void setRequestErrorHandler(final RequestErrorHandler requestErrorHandler)
	{
		this.requestErrorHandler = requestErrorHandler;
	}

	protected YaasServiceFactory getYaasServiceFactory()
	{
		return yaasServiceFactory;
	}

	/**
	 * @param yaasServiceFactory
	 *           the YaasServiceFactory to set
	 */
	@Required
	public void setYaasServiceFactory(final YaasServiceFactory yaasServiceFactory)
	{
		this.yaasServiceFactory = yaasServiceFactory;
	}


}
