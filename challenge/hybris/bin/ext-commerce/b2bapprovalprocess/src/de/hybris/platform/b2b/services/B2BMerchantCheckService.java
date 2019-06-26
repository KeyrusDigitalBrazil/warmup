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
package de.hybris.platform.b2b.services;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BMerchantCheckResultModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import java.util.Set;


/**
 * The Interface B2BMerchantCheckService. A service for evaluating different strategies or rules determined by the
 * merchant against orders for a customer. Different strategies may include things such as credit limits.
 * 
 * @spring.bean b2bMerchantCheckService
 */
public interface B2BMerchantCheckService
{
	/**
	 * Evaluates the order for a customer to determine the merchant checks such as credit limits, order thresholds,
	 * budget limits.
	 * 
	 * @param order
	 *           the order being evaluated
	 * @param customer
	 *           the customer of the order
	 * @return results of the evaluations against the order
	 */
	public Set<B2BMerchantCheckResultModel> evaluateMerchantChecks(final AbstractOrderModel order, final B2BCustomerModel customer);

}
