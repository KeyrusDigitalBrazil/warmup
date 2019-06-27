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

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.customercouponservices.model.CouponNotificationModel;
import de.hybris.platform.customercouponservices.model.CouponNotificationProcessModel;
import de.hybris.platform.customercouponservices.model.CustomerCouponModel;
import de.hybris.platform.customercouponservices.strategies.CouponNotificationProcessContextStrategy;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class CouponNotificationProcessContextStrategyTest
{
	private final String SITE_NAME = "testSite";
	private final String COUPON_ID = "customercoupon1";
	private final String CUSTOMER_ID = "customer1";
	private final String LANAGUAGE_ISO_EN = "en";

	private CouponNotificationProcessContextStrategy couponNotificationProcessContextStrategy;

	private CouponNotificationProcessModel couponNotificationProcess;


	@Before
	public void prepare()
	{
		couponNotificationProcessContextStrategy = new CouponNotificationProcessContextStrategy();
		couponNotificationProcess = new CouponNotificationProcessModel();
		final BaseSiteModel baseSite = new BaseSiteModel();
		baseSite.setUid(SITE_NAME);
		final CouponNotificationModel couponNotificaiton = new CouponNotificationModel();
		couponNotificaiton.setBaseSite(baseSite);
		final CustomerModel customer = new CustomerModel();
		customer.setUid(CUSTOMER_ID);
		couponNotificaiton.setCustomer(customer);
		final CustomerCouponModel customerCoupon = new CustomerCouponModel();
		customerCoupon.setCouponId(COUPON_ID);
		couponNotificaiton.setCustomerCoupon(customerCoupon);
		final LanguageModel language = new LanguageModel();
		language.setIsocode(LANAGUAGE_ISO_EN);
		couponNotificaiton.setLanguage(language);

		couponNotificationProcess.setCouponNotification(couponNotificaiton);
	}

	@Test
	public void testGetCmsSite()
	{
		final BaseSiteModel baseSite = couponNotificationProcessContextStrategy.getCmsSite(couponNotificationProcess);

		assertEquals(SITE_NAME, baseSite.getUid());
	}

}
