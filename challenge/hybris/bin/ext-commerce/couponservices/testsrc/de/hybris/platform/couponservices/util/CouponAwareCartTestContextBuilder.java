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
package de.hybris.platform.couponservices.util;

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.ruleengineservices.ruleengine.impl.CartTestContextBuilder;

import java.util.Set;


public class CouponAwareCartTestContextBuilder extends CartTestContextBuilder
{
	public CartTestContextBuilder withCouponCodes(final Set<String> couponCodes)
	{
		final CartModel cartModel = getCartModel();
		cartModel.setAppliedCouponCodes(couponCodes);
		return this;
	}
}
