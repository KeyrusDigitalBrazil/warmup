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
package de.hybris.platform.subscriptionservices.subscription;

import de.hybris.platform.core.model.product.ProductModel;

/**
 * Service interface holding subscription-related operations.
 */
public interface SubscriptionProductService
{
	/**
	 * Determines whether given product is subscription-capable.
	 *
	 * @param product product to check
	 * @return true if it is a subscription product
	 */
	boolean isSubscription(ProductModel product);
}
