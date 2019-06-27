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
import de.hybris.platform.commerceservices.order.CommerceQuoteService;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.sap.c4c.quote.constants.C4cquoteConstants;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.model.ModelService;


/**
 * Event Listener for {@link C4CQuoteBuyerOrderPlacedEvent} which updates the quote state, attaches the latest quote
 * snapshot to the order placed and sends the quote to datahub.
 */
public class C4CQuoteBuyerOrderPlacedEventListener extends AbstractEventListener<C4CQuoteBuyerOrderPlacedEvent>
{

	private ModelService modelService;
	private CommerceQuoteService commerceQuoteService;
	private BusinessProcessService businessProcessService;
	private static final Logger LOG = Logger.getLogger(C4CQuoteBuyerOrderPlacedEventListener.class);

	@Override
	protected void onEvent(final C4CQuoteBuyerOrderPlacedEvent event)
	{

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Received C4CQuoteBuyerOrderPlacedEvent..");
		}

		final OrderModel orderModel = event.getOrder();
		final QuoteModel quoteModel = getCommerceQuoteService().createQuoteSnapshotWithState(event.getQuote(),
				QuoteState.BUYER_ORDERED);
		getModelService().refresh(orderModel);
		orderModel.setQuoteReference(quoteModel);
		getModelService().save(orderModel);

		getModelService().refresh(quoteModel);
		quoteModel.setOrderId(orderModel.getCode());
		getModelService().save(quoteModel);

		final Map<String, Object> contextParams = new HashMap<String, Object>();

		final QuoteProcessModel quoteOrderedProcessModel = (QuoteProcessModel) getBusinessProcessService().createProcess(
				"quoteOrderPlacedProcess" + "-" + event.getQuote().getCode() + "-" + event.getQuote().getStore().getUid() + "-"
						+ System.currentTimeMillis(), C4cquoteConstants.C4C_QUOTE_ORDER_PLACED_PROCESS, contextParams);

		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format("Created business process for C4CQuoteBuyerOrderPlacedEvent. Process code : [%s] ...",
					quoteOrderedProcessModel.getCode()));
		}

		quoteOrderedProcessModel.setQuoteCode(quoteModel.getCode());
		getModelService().save(quoteOrderedProcessModel);

		businessProcessService.startProcess(quoteOrderedProcessModel);


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

	protected CommerceQuoteService getCommerceQuoteService()
	{
		return commerceQuoteService;
	}

	@Required
	public void setCommerceQuoteService(final CommerceQuoteService commerceQuoteService)
	{
		this.commerceQuoteService = commerceQuoteService;
	}

	public BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	public void setBusinessProcessService(BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}

}
