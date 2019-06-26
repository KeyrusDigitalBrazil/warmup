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
package de.hybris.platform.customercouponservices.strategies.impl;

import static java.util.Optional.empty;

import de.hybris.platform.couponservices.model.AbstractCouponModel;
import de.hybris.platform.couponservices.strategies.impl.AbstractFindCouponStrategy;
import de.hybris.platform.customercouponservices.model.CustomerCouponModel;

import java.util.Optional;


/**
 * Validates CustomerCouponModel, otherwise customer coupon can't be applied
 */
public class DefaultFindCustomerCouponStrategy extends AbstractFindCouponStrategy
{

	@Override
	protected Optional<AbstractCouponModel> couponValidation(final AbstractCouponModel coupon)
	{
		return (coupon instanceof CustomerCouponModel) ? super.couponValidation(coupon) : empty();
	}

	@Override
	protected String getCouponId(final String couponCode)
	{
		return couponCode;
	}
}
