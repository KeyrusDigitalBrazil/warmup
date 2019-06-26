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
package com.sap.hybris.sapquoteintegration.events;

import de.hybris.platform.commerceservices.model.process.QuoteProcessModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 *
 */
public class SapCpiQuoteBuyerSubmitEventListener extends AbstractEventListener<SapCpiQuoteBuyerSubmitEvent>
{
	private ModelService modelService;
	private BusinessProcessService businessProcessService;
	private static final Logger LOG = Logger.getLogger(SapCpiQuoteBuyerSubmitEventListener.class);

	@Override
	protected void onEvent(final SapCpiQuoteBuyerSubmitEvent event)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Received QuoteBuyerSubmitEvent..");
		}

		final Map<String, Object> contextParams = new HashMap<String, Object>();
		contextParams.put("QUOTE_USER_TYPE", event.getQuoteUserType());

		final QuoteProcessModel quoteBuyerProcessModel = (QuoteProcessModel) getBusinessProcessService()
				.createProcess("sapQuoteBuyerProcess" + "-" + event.getQuote().getCode() + "-" + event.getQuote().getStore().getUid()
						+ "-" + System.currentTimeMillis(), "sap-cpi-quote-buyer-submit-process", contextParams);

		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format("Created business process for QuoteBuyerSubmitEvent. Process code : [%s] ...",
					quoteBuyerProcessModel.getCode()));
		}

		final QuoteModel quoteModel = event.getQuote();
		quoteBuyerProcessModel.setQuoteCode(quoteModel.getCode());
		getModelService().save(quoteBuyerProcessModel);
		// start the business process
		getBusinessProcessService().startProcess(quoteBuyerProcessModel);

	}

	protected BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	@Required
	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
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

}
