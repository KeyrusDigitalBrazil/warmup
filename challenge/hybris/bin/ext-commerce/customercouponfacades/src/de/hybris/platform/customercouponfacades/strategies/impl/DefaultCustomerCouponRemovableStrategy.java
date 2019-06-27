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
package de.hybris.platform.customercouponfacades.strategies.impl;

import de.hybris.platform.customercouponfacades.strategies.CustomerCouponRemovableStrategy;
import de.hybris.platform.customercouponservices.CustomerCouponService;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link CustomerCouponRemovableStrategy}
 */
public class DefaultCustomerCouponRemovableStrategy implements CustomerCouponRemovableStrategy
{

	private CustomerCouponService customerCouponService;


	@Override
	public boolean checkRemovable(final String couponCode)
	{
		return getCustomerCouponService()
				.getCouponForCode(couponCode)
				.map(coupon -> Boolean.valueOf(coupon.getEndDate() != null
						&& coupon.getEndDate().after(Calendar.getInstance().getTime()))).orElse(Boolean.FALSE).booleanValue();
	}


	protected CustomerCouponService getCustomerCouponService()
	{
		return customerCouponService;
	}

	@Required
	public void setCustomerCouponService(final CustomerCouponService customerCouponService)
	{
		this.customerCouponService = customerCouponService;
	}

}
