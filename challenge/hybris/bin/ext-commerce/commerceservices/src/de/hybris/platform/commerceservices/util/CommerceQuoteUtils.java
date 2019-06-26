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
package de.hybris.platform.commerceservices.util;

import de.hybris.platform.commerceservices.constants.CommerceServicesConstants;
import de.hybris.platform.commerceservices.order.OrderQuoteDiscountValuesAccessor;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.util.DiscountValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Some helper methods for the Quote.
 */
public class CommerceQuoteUtils
{
	private OrderQuoteDiscountValuesAccessor orderQuoteDiscountValuesAccessor;

	/**
	 * Remove existing quote discounts from {@link AbstractOrderModel}
	 *
	 * @param abstractOrderModel
	 *           abstract order model
	 * @return Remaining discount values of the abstract order model
	 */
	public List<DiscountValue> removeExistingQuoteDiscount(final AbstractOrderModel abstractOrderModel)
	{
		final List<DiscountValue> discountList = new ArrayList<>();
		for (final DiscountValue discount : abstractOrderModel.getGlobalDiscountValues())
		{
			if (!CommerceServicesConstants.QUOTE_DISCOUNT_CODE.equals(discount.getCode()))
			{
				discountList.add(discount);
			}
		}
		abstractOrderModel.setGlobalDiscountValues(discountList);
		getOrderQuoteDiscountValuesAccessor().setQuoteDiscountValues(abstractOrderModel, Collections.emptyList());
		return discountList;
	}

	protected OrderQuoteDiscountValuesAccessor getOrderQuoteDiscountValuesAccessor()
	{
		return orderQuoteDiscountValuesAccessor;
	}

	@Required
	public void setOrderQuoteDiscountValuesAccessor(final OrderQuoteDiscountValuesAccessor orderQuoteDiscountValuesAccessor)
	{
		this.orderQuoteDiscountValuesAccessor = orderQuoteDiscountValuesAccessor;
	}

}
