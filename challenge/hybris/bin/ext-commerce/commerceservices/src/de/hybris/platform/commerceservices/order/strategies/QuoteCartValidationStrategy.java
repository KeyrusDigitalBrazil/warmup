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
package de.hybris.platform.commerceservices.order.strategies;


import de.hybris.platform.core.model.order.AbstractOrderModel;


/**
 * Strategy to help validate if quote related cart is modified or not compared to the quote
 */
public interface QuoteCartValidationStrategy
{
	/**
	 * Verifies that there are no differences in<br/>
	 * <br/>
	 * 1) Total Price<br/>
	 * 2) Total Discounts <br/>
	 * 3) Order Entries and Quantities<br/>
	 * <br/>
	 * for the given abstract orders.
	 *
	 * @param order1
	 *           abstract order to compare
	 * @param order2
	 *           abstract order to compare
	 * @return true if there are no differences in the inspected attributes between the two orders, false otherwise
	 * @throws IllegalArgumentException
	 *            if any of the abstract orders is null
	 */
	boolean validate(AbstractOrderModel order1, AbstractOrderModel order2);
}
