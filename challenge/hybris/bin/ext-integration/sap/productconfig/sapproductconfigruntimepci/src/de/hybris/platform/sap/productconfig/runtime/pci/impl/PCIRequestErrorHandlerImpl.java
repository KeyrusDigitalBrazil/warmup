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
package de.hybris.platform.sap.productconfig.runtime.pci.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsDocument;
import de.hybris.platform.sap.productconfig.runtime.pci.PCIRequestErrorHandler;

import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

import com.hybris.charon.exp.HttpException;

import rx.Observable;
import rx.schedulers.Schedulers;


/**
 * Default implementaion of the {@link PCIRequestErrorHandler}.
 */
public class PCIRequestErrorHandlerImpl implements PCIRequestErrorHandler
{

	private static final Logger LOG = Logger.getLogger(PCIRequestErrorHandlerImpl.class);
	protected static final String NO_SERVER_MESSAGE = "No server message available";

	protected AnalyticsDocument createEmptyAnalyticsDocument(final AnalyticsDocument analyticsDocumentInput)
	{
		final AnalyticsDocument anaDoc = new AnalyticsDocument();
		anaDoc.setRootItem(null);
		anaDoc.setRootProduct(analyticsDocumentInput.getRootProduct());
		return anaDoc;
	}



	@Override
	public AnalyticsDocument processCreateAnalyticsDocumentHttpError(final HttpException ex,
			final AnalyticsDocument analyticsDocumentInput)
	{
		LOG.error("analytics service failed with the following server side error: " + getServerMessage(ex), ex);
		return createEmptyAnalyticsDocument(analyticsDocumentInput);
	}

	protected String getServerMessage(final HttpException ex)
	{
		final Observable<String> serverMessage = ex.getServerMessage();
		if (serverMessage != null)
		{
			return serverMessage.subscribeOn(Schedulers.io()).toBlocking().first();
		}
		else
		{
			return NO_SERVER_MESSAGE;
		}
	}

	@Override
	public AnalyticsDocument processCreateAnalyticsDocumentRuntimeException(final RuntimeException ex,
			final AnalyticsDocument analyticsDocumentInput)
	{
		final Throwable cause = ex.getCause();
		if (cause instanceof TimeoutException)
		{
			LOG.error("Timeout exception", cause);
			return createEmptyAnalyticsDocument(analyticsDocumentInput);
		}
		throw ex;
	}

}
