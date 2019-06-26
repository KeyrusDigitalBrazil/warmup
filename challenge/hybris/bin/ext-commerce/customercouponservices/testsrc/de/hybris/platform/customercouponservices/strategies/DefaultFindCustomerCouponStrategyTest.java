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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.couponservices.model.AbstractCouponModel;
import de.hybris.platform.customercouponservices.model.CustomerCouponModel;
import de.hybris.platform.customercouponservices.strategies.impl.DefaultFindCustomerCouponStrategy;

import java.lang.reflect.Method;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@UnitTest
public class DefaultFindCustomerCouponStrategyTest
{
	private DefaultFindCustomerCouponStrategy strategy;

	private AbstractCouponModel absCouponModel;

	private CustomerCouponModel couponModel;

	@Before
	public void init()
	{

		strategy = new DefaultFindCustomerCouponStrategy();
		absCouponModel = new AbstractCouponModel();
		couponModel = new CustomerCouponModel();

	}

	@Test
	public void testCouponValidation()
	{
		try{
			Method method = strategy.getClass().getDeclaredMethod("couponValidation", new Class[]
			{ AbstractCouponModel.class });
			method.setAccessible(true);

			Optional res = (Optional) method.invoke(strategy, absCouponModel);
			Assert.assertEquals(Optional.empty(), res);

			res = (Optional) method.invoke(strategy, couponModel);


		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

}
