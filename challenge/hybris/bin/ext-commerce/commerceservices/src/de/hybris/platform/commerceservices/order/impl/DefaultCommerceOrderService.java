/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.commerceservices.order.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.commerceservices.order.CommerceOrderService;
import de.hybris.platform.commerceservices.order.dao.CommerceOrderDao;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.QuoteModel;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation for {@link CommerceOrderService}.
 */
public class DefaultCommerceOrderService implements CommerceOrderService
{

	private CommerceOrderDao commerceOrderDao;

	@Override
	public OrderModel getOrderForQuote(final QuoteModel quoteModel)
	{
		validateParameterNotNullStandardMessage("QuoteModel", quoteModel);
		return getCommerceOrderDao().findOrderByQuote(quoteModel);
	}

	protected CommerceOrderDao getCommerceOrderDao()
	{
		return commerceOrderDao;
	}

	@Required
	public void setCommerceOrderDao(final CommerceOrderDao commerceOrderDao)
	{
		this.commerceOrderDao = commerceOrderDao;
	}
}
