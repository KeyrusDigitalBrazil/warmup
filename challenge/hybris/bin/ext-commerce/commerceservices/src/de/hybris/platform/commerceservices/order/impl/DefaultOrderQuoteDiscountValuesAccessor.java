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

import de.hybris.platform.commerceservices.order.OrderQuoteDiscountValuesAccessor;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.util.DiscountValue;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Default implementation of {@link OrderQuoteDiscountValuesAccessor} transforming the string representation of quote
 * discounts to/from a list of {@link DiscountValue}.
 */
public class DefaultOrderQuoteDiscountValuesAccessor implements OrderQuoteDiscountValuesAccessor
{
	@Override
	public List<DiscountValue> getQuoteDiscountValues(final AbstractOrderModel order)
	{
		validateParameterNotNullStandardMessage("order", order);

		final String discountValuesString = order.getQuoteDiscountValuesInternal();
		final Collection<DiscountValue> discountValues = DiscountValue.parseDiscountValueCollection(discountValuesString);
		if (discountValues == null)
		{
			return Collections.EMPTY_LIST;
		}
		return discountValues instanceof List ? (List) discountValues : new LinkedList(discountValues);
	}

	@Override
	public void setQuoteDiscountValues(final AbstractOrderModel order, final List<DiscountValue> discountValues)
	{
		validateParameterNotNullStandardMessage("order", order);
		validateParameterNotNullStandardMessage("discountValues", discountValues);

		final String discountValuesString = DiscountValue.toString(discountValues);
		order.setQuoteDiscountValuesInternal(discountValuesString);
		order.setCalculated(Boolean.FALSE);
	}
}
