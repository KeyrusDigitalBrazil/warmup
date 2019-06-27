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
package de.hybris.platform.customercouponservices.redemption.strategies.impl;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.couponservices.redemption.strategies.CouponRedemptionStrategy;
import de.hybris.platform.customercouponservices.daos.CustomerCouponDao;
import de.hybris.platform.customercouponservices.model.CustomerCouponModel;

import org.springframework.beans.factory.annotation.Required;


/**
 * Checks if customer coupon is redeemable when placing an order
 */
public class DefaultCustomerCouponRedemptionStrategy implements CouponRedemptionStrategy<CustomerCouponModel>
{
	private CustomerCouponDao customerCouponDao;

	@Override
	public boolean isRedeemable(final CustomerCouponModel coupon, final AbstractOrderModel abstractOrder, final String couponCode)
	{
		return isCouponRedeemable(coupon, abstractOrder.getUser(), couponCode);
	}

	@Override
	public boolean isCouponRedeemable(final CustomerCouponModel coupon, final UserModel user, final String couponCode)
	{
		return getCustomerCouponDao().checkCustomerCouponAvailableForCustomer(couponCode, (CustomerModel) user);
	}

	protected CustomerCouponDao getCustomerCouponDao()
	{
		return customerCouponDao;
	}

	@Required
	public void setCustomerCouponDao(final CustomerCouponDao customerCouponDao)
	{
		this.customerCouponDao = customerCouponDao;
	}

}
