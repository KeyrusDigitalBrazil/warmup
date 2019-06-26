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
package de.hybris.platform.commercefacades.coupon.impl;

import de.hybris.platform.commercefacades.coupon.CouponDataFacade;
import de.hybris.platform.commercefacades.coupon.data.CouponData;

import java.util.Optional;


/**
 * Default Implementation of {@link CouponDataFacade} returning an Optional.empty()
 *
 */
public class DefaultCouponDataFacade implements CouponDataFacade
{

	@Override
	public Optional<CouponData> getCouponDetails(final String couponCode)
	{
		return Optional.empty();
	}

}
