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
package de.hybris.platform.customercouponservices.daos.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.customercouponservices.daos.CouponNotificationDao;
import de.hybris.platform.customercouponservices.model.CouponNotificationModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Integration test for {@link DefaultCustomerCouponNotificationDao}
 */
@IntegrationTest
public class DefaultCustomerCouponNotificationDaoTest extends ServicelayerTransactionalTest
{
	private static final String COUPON_ID = "customerCouponCode3";
	private static final String CUSTOMER_UID1 = "keenreviewer1@hybris.com";
	private static final String CUSTOMER_UID2 = "keenreviewer3@hybris.com";

	@Resource(name = "couponNotificationDao")
	private CouponNotificationDao couponNotificationDao;

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "modelService")
	private ModelService modelService;


	@Before
	public void prepare() throws ImpExException
	{

		importCsv("/customercouponservices/test/DefaultCustomerCouponServiceTest.impex", "UTF-8");
	}

	@Test
	public void testFindCouponNotificationByCouponCode()
	{
		final List<CouponNotificationModel> result = couponNotificationDao.findCouponNotificationByCouponCode(COUPON_ID);

		Assert.assertEquals(1, result.size());
	}

	@Test
	public void testFindCouponNotificationForCustomer_customerWithNotification()
	{
		final CustomerModel customer = (CustomerModel) userService.getUserForUID(CUSTOMER_UID1);
		final List<CouponNotificationModel> result = couponNotificationDao.findCouponNotificationsForCustomer(customer);
		Assert.assertEquals(1, result.size());
	}

	@Test
	public void testFindCouponNotificationForCustomer_customerWithoutNotification()
	{
		final CustomerModel customer = modelService.create(CustomerModel.class);
		customer.setUid(CUSTOMER_UID2);
		modelService.save(customer);
		final List<CouponNotificationModel> result = couponNotificationDao.findCouponNotificationsForCustomer(customer);
		Assert.assertEquals(0, result.size());
	}

}
