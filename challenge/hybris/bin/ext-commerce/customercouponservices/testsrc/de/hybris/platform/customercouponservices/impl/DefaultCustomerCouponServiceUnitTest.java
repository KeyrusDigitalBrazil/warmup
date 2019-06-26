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
package de.hybris.platform.customercouponservices.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.couponservices.dao.CouponDao;
import de.hybris.platform.customercouponservices.daos.CouponNotificationDao;
import de.hybris.platform.customercouponservices.daos.CustomerCouponDao;
import de.hybris.platform.customercouponservices.model.CouponNotificationModel;
import de.hybris.platform.customercouponservices.model.CustomerCouponModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import junit.framework.Assert;


/**
 * Unit test for {@link DefaultCustomerCouponService}
 */
@UnitTest
public class DefaultCustomerCouponServiceUnitTest
{
	private DefaultCustomerCouponService customerCouponService;

	private static final String COUPON_ID = "TESTID";

	private List couponList;

	@Mock
	private UserService userSerivce;
	@Mock
	private BaseSiteService baseSiteService;
	@Mock
	private ModelService modelService;
	@Mock
	private CouponDao couponDao;
	@Mock
	private CustomerCouponDao customerCouponDao;
	@Mock
	private CouponNotificationDao couponNotificationDao;
	@Mock
	private CommonI18NService commonI18NService;
	@Mock
	private CouponNotificationModel couponNotificationModel;
	@Mock
	private CustomerModel Customer;


	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);
		customerCouponService = new DefaultCustomerCouponService();

		final CustomerModel customerModel = new CustomerModel();
		customerModel.setCustomerID("abc");
		customerModel.setName("lilei");
		final CustomerModel customerTest = new CustomerModel();
		customerModel.setCustomerID("abcd");
		customerModel.setName("hanmeimei");
		Mockito.doReturn(customerModel).when(userSerivce).getCurrentUser();
		final BaseSiteModel baseSite = new BaseSiteModel();
		baseSite.setUid("electronics");
		Mockito.doReturn(baseSite).when(baseSiteService).getCurrentBaseSite();
		
		couponList = new ArrayList();

		customerCouponService.setBaseSiteService(baseSiteService);
		customerCouponService.setUserService(userSerivce);
		customerCouponService.setModelService(modelService);
		customerCouponService.setCouponNotificationDao(couponNotificationDao);
		customerCouponService.setCustomerCouponDao(customerCouponDao);

		final CustomerCouponModel customerCoupon = new CustomerCouponModel();
		customerCoupon.setCouponId(COUPON_ID);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -10);
		customerCoupon.setStartDate(cal.getTime());
		customerCoupon.setEndDate(cal.getTime());
		final LanguageModel language = new LanguageModel();
		language.setIsocode("en");
		customerCouponService.setCommonI18NService(commonI18NService);

		couponNotificationModel.setCustomer(customerTest);
		couponNotificationModel.setCustomerCoupon(customerCoupon);
		final List<CouponNotificationModel> couponNotificationList = new ArrayList<CouponNotificationModel>();
		couponNotificationList.add(couponNotificationModel);

		Mockito.when(couponNotificationModel.getCustomer()).thenReturn(Customer);
		Mockito.when(Customer.getCustomerID()).thenReturn("abc");

		Mockito.when(commonI18NService.getCurrentLanguage()).thenReturn(language);
		Mockito.when(couponNotificationDao.findCouponNotificationByCouponCode(COUPON_ID)).thenReturn(couponNotificationList);

		Mockito.doReturn(customerCoupon).when(couponDao).findCouponById(Mockito.anyString());
		Mockito.when(customerCouponDao.findAssignableCoupons(Mockito.any(), Mockito.anyString())).thenReturn(couponList);
		Mockito.when(customerCouponDao.findAssignedCouponsByCustomer(Mockito.any(), Mockito.anyString())).thenReturn(couponList);

		customerCouponService.setCouponDao(couponDao);
	}

	@Test
	public void testSaveCouponNotification()
	{
		customerCouponService.saveCouponNotification(COUPON_ID);

		Mockito.verify(modelService, Mockito.times(1)).save(Mockito.any());
	}

	@Test
	public void testGetCoupon()
	{
		final CustomerModel customerModel = new CustomerModel();
		Assert.assertEquals(couponList, customerCouponService.getAssignableCustomerCoupons(customerModel, COUPON_ID));
		Assert.assertEquals(couponList, customerCouponService.getAssignedCustomerCouponsForCustomer(customerModel, COUPON_ID));

	}

}
