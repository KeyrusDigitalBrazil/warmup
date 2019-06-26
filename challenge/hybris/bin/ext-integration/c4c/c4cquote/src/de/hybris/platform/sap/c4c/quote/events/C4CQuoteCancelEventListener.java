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
package de.hybris.platform.sap.c4c.quote.events;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.commerceservices.model.process.QuoteProcessModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.sap.c4c.quote.constants.C4cquoteConstants;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.model.ModelService;


public class C4CQuoteCancelEventListener extends AbstractEventListener<C4CQuoteCancelEvent>
{

	private ModelService modelService;
	private BusinessProcessService businessProcessService;
	private static final Logger LOG = Logger.getLogger(C4CQuoteCancelEventListener.class);

	@Override
	protected void onEvent(final C4CQuoteCancelEvent event)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Received C4CQuoteCancelEvent..");
		}

		final Map<String, Object> contextParams = new HashMap<String, Object>();
		contextParams.put(C4cquoteConstants.QUOTE_USER_TYPE, event.getQuoteUserType());

		final QuoteProcessModel quotePostCancellationProcessModel = (QuoteProcessModel) getBusinessProcessService().createProcess(
				"quotePostCancellationProcess" + "-" + event.getQuote().getCode() + "-" + event.getQuote().getStore().getUid() + "-"
						+ System.currentTimeMillis(), C4cquoteConstants.C4C_QUOTE_POST_CANCELLATION_PROCESS, contextParams);

		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format("Created business process for C4CQuoteCancelEvent. Process code : [%s] ...",
					quotePostCancellationProcessModel.getCode()));
		}

		final QuoteModel quoteModel = event.getQuote();
		quotePostCancellationProcessModel.setQuoteCode(quoteModel.getCode());
		getModelService().save(quotePostCancellationProcessModel);
		//start the business process
		getBusinessProcessService().startProcess(quotePostCancellationProcessModel);

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
