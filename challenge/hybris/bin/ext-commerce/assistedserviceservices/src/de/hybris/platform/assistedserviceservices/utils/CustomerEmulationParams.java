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
package de.hybris.platform.assistedserviceservices.utils;

import java.io.Serializable;

/**
 * Class represents deep link parameters for saving in session.
 */
public class CustomerEmulationParams implements Serializable
{
	private final String userId;
	private final String cartId;
	private final String orderId;

	public CustomerEmulationParams(final String userId, final String cartId)
	{
		this.userId = userId;
		this.cartId = cartId;
		this.orderId = null;
	}

	public CustomerEmulationParams(final String userId, final String cartId, final String orderId)
	{
		this.userId = userId;
		this.cartId = cartId;
		this.orderId = orderId;
	}

	/**
	 * @return the userId
	 */
	public String getUserId()
	{
		return userId;
	}

	/**
	 * @return the cartId
	 */
	public String getCartId()
	{
		return cartId;
	}

	/**
	 * @return the orderId
	 */
	public String getOrderId()
	{
		return orderId;
	}
}