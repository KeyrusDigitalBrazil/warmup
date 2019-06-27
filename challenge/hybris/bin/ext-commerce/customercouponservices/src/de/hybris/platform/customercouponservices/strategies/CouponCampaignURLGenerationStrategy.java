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
package de.hybris.platform.customercouponservices.strategies;

import de.hybris.platform.customercouponservices.model.CustomerCouponModel;


/**
 * Generates the customer coupon campaign URL when creating a new customer coupon
 */
public interface CouponCampaignURLGenerationStrategy
{

	/**
	 * Generates the customer coupon campaign URL
	 * 
	 * @param coupon
	 *           the customer coupon model
	 * @return the campaign URL of the coupon
	 */
	String generate(CustomerCouponModel coupon);
}
