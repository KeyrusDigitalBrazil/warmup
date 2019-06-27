/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package com.sap.hybris.sapquoteintegration.events;

import de.hybris.platform.commerceservices.model.process.QuoteProcessModel;
import de.hybris.platform.commerceservices.order.CommerceQuoteService;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.OrderModel;
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
public class SapCpiQuoteOrderPlacedEventListener extends AbstractEventListener<SapCpiQuoteOrderPlacedEvent>
{

	private ModelService modelService;
	private CommerceQuoteService commerceQuoteService;
	private BusinessProcessService businessProcessService;
	private static final Logger LOG = Logger.getLogger(SapCpiQuoteOrderPlacedEventListener.class);

	@Override
	protected void onEvent(final SapCpiQuoteOrderPlacedEvent event)
	{

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Received SapCpiQuoteOrderPlacedEvent..");
		}

		final OrderModel orderModel = event.getOrder();
		final QuoteModel quoteModel = getCommerceQuoteService().createQuoteSnapshotWithState(event.getQuote(),
				QuoteState.BUYER_ORDERED);
		getModelService().refresh(orderModel);
		orderModel.setQuoteReference(quoteModel);
		getModelService().save(orderModel);

		getModelService().refresh(quoteModel);
		quoteModel.setOrderCode(orderModel.getCode());
		getModelService().save(quoteModel);

		final Map<String, Object> contextParams = new HashMap<String, Object>();

		final QuoteProcessModel quoteOrderedProcessModel = (QuoteProcessModel) getBusinessProcessService().createProcess(
				"quoteOrderPlacedProcess" + "-" + event.getQuote().getCode() + "-" + event.getQuote().getStore().getUid() + "-"
						+ System.currentTimeMillis(),
				"sap-cpi-quote-completed-process", contextParams);

		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format("Created business process for SapCpiQuoteOrderPlacedEvent. Process code : [%s] ...",
					quoteOrderedProcessModel.getCode()));
		}

		quoteOrderedProcessModel.setQuoteCode(quoteModel.getCode());
		quoteOrderedProcessModel.setOrderCode(orderModel.getCode());
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

	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}

}