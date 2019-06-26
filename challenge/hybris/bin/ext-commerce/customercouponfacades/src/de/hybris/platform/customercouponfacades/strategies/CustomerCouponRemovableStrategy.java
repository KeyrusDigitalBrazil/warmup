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
package de.hybris.platform.customercouponfacades.strategies;


/**
 * Checks if the specific customer coupon can be removed the when removing it
 */
public interface CustomerCouponRemovableStrategy
{

	/**
	 * Checks if the specific customer coupon can be removed from the current customer
	 *
	 * @param couponCode
	 *           the coupon code
	 * @return true if the coupon can be removed and false otherwise
	 */
	default boolean checkRemovable(final String couponCode)
	{
		return true;
	}

}
