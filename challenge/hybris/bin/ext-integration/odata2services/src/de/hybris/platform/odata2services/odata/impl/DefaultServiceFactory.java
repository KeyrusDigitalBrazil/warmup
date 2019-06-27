/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.odata2services.odata.impl;

import de.hybris.platform.odata2services.odata.EdmProviderFactory;
import de.hybris.platform.odata2services.odata.processor.ODataProcessorFactory;

import org.apache.olingo.odata2.api.ODataCallback;
import org.apache.olingo.odata2.api.ODataService;
import org.apache.olingo.odata2.api.ODataServiceFactory;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.processor.ODataSingleProcessor;
import org.springframework.beans.factory.annotation.Required;

public class DefaultServiceFactory extends ODataServiceFactory
{
	private EdmProviderFactory edmProviderFactory;
	private ODataProcessorFactory processorFactory;

	@Override
	public ODataService createService(final ODataContext context)
	{
		final ODataSingleProcessor processor = processorFactory.createProcessor(context);
		return createODataSingleProcessorService(edmProviderFactory.createInstance(context), processor);
	}

	@Override
	public <T extends ODataCallback> T getCallback(final Class<T> callbackInterface)
	{
		if (callbackInterface.isAssignableFrom(CustomODataExceptionAwareErrorCallback.class))
		{
			return (T) new CustomODataExceptionAwareErrorCallback();
		}
		return super.getCallback(callbackInterface);
	}

	protected EdmProviderFactory getEdmProviderFactory()
	{
		return edmProviderFactory;
	}

	@Required
	public void setEdmProviderFactory(final EdmProviderFactory edmProviderFactory)
	{
		this.edmProviderFactory = edmProviderFactory;
	}

	protected ODataProcessorFactory getProcessorFactory()
	{
		return processorFactory;
	}

	@Required
	public void setProcessorFactory(final ODataProcessorFactory factory)
	{
		processorFactory = factory;
	}
}
