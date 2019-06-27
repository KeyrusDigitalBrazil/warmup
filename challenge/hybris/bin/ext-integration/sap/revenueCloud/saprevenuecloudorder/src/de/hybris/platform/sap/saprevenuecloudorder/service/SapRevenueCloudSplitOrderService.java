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
package de.hybris.platform.sap.saprevenuecloudorder.service;

import java.math.BigDecimal;
import java.util.Map;

import de.hybris.platform.core.model.order.CartModel;

/**
 * 
 * Methods to handle authorization amount split 
 *
 */
public interface SapRevenueCloudSplitOrderService {
	
	/**
	 * Splits the authorization amount split in the cart for different target systems
	 * 
	 * @param cart
	 * 
	 * @return authorizationAmountMap<Target,Amount>
	 */
	
	Map<String,BigDecimal> getAuthorizationAmountListFromCart(final CartModel cart);

}
