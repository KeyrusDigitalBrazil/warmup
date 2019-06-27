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
package de.hybris.platform.commercefacades.order.converters.populator;

import de.hybris.platform.commercefacades.quote.data.QuoteData;
import de.hybris.platform.commerceservices.order.CommerceOrderService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.QuoteModel;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


public class QuotePopulator extends AbstractOrderPopulator<QuoteModel, QuoteData>
{
	private CommerceOrderService commerceOrderService;

	@Override
	public void populate(final QuoteModel source, final QuoteData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		addCommon(source, target);
		addDetails(source, target);
		addTotals(source, target);
		addEntries(source, target);
		addPromotions(source, target);
		addComments(source, target);
		addQuoteInfo(source, target);
		target.setHasCart(Boolean.valueOf(source.getCartReference() != null));
	}

	protected void addQuoteInfo(final QuoteModel source, final QuoteData target)
	{
		final OrderModel orderFromQuote = getCommerceOrderService().getOrderForQuote(source);
		if (orderFromQuote != null)
		{
			target.setOrderCode(orderFromQuote.getCode());
		}
	}

	protected void addDetails(final QuoteModel source, final QuoteData target)
	{
		target.setCode(source.getCode());
		target.setVersion(source.getVersion());
		target.setExpirationTime(source.getExpirationTime());
		target.setState(source.getState());
		target.setCreationTime(source.getCreationtime());
		target.setUpdatedTime(source.getModifiedtime());
		target.setPreviousEstimatedTotal(createPrice(source, source.getPreviousEstimatedTotal()));
	}

	protected CommerceOrderService getCommerceOrderService()
	{
		return commerceOrderService;
	}

	@Required
	public void setCommerceOrderService(final CommerceOrderService commerceOrderService)
	{
		this.commerceOrderService = commerceOrderService;
	}
}
