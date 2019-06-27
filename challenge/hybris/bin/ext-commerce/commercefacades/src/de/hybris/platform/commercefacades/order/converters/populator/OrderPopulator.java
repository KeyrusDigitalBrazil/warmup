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

import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.core.model.order.OrderModel;

import org.springframework.util.Assert;


/**
 * Converter implementation for {@link de.hybris.platform.core.model.order.OrderModel} as source and
 * {@link de.hybris.platform.commercefacades.order.data.OrderData} as target type.
 */
public class OrderPopulator extends AbstractOrderPopulator<OrderModel, OrderData>
{

	@Override
	public void populate(final OrderModel source, final OrderData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		addCommon(source, target);
		addDetails(source, target);
		addTotals(source, target);
		addEntries(source, target);
		addPromotions(source, target);
		addDeliveryAddress(source, target);
		addDeliveryMethod(source, target);
		addPaymentInformation(source, target);
		checkForGuestCustomer(source, target);
		addDeliveryStatus(source, target);
		addPrincipalInformation(source, target);

		if (source.getQuoteReference() != null)
		{
			target.setQuoteCode(source.getQuoteReference().getCode());
		}
	}


	protected void addDetails(final OrderModel source, final OrderData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");
		target.setCreated(source.getDate());
		target.setStatus(source.getStatus());
		target.setStatusDisplay(source.getStatusDisplay());
		if (null != source.getPlacedBy())
		{
			target.setPlacedBy(source.getPlacedBy().getUid());
		}
	}
}
