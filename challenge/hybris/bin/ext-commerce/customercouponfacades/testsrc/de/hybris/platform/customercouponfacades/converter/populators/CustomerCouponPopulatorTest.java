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
package de.hybris.platform.customercouponfacades.converter.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.customercouponfacades.customercoupon.data.CustomerCouponData;
import de.hybris.platform.customercouponservices.CustomerCouponService;
import de.hybris.platform.customercouponservices.model.CustomerCouponModel;
import de.hybris.platform.promotionengineservices.model.PromotionSourceRuleModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;


/**
 * Unit test for {@link CustomerCouponPopulator}
 */
@UnitTest
public class CustomerCouponPopulatorTest
{

	private static final String COUPON_EXPIRE_NOTIFICATIONS_DAYS = "coupon.expire.notification.days";
	private static final String COUPON_NAME = "testname";
	private static final String COUPON_ID = "TEST";
	private static final String DESCRIPTION = "description";
	private static final String STATUS = "Effective";
	private static final String ROOT_CATEGORY = "coupon.rootcategory";
	private static final String ROOT_CATEGORY_CONFIG_DATA = "1";
	private static final int ZERO = 0;
	private static final String STATUS_PRESESSION = "PreSession";
	private static final String STATUS_EFFECTIVE = "Effective";
	private static final String STATUS_EXPIRESOON = "ExpireSoon";

	@Spy
	private final CustomerCouponPopulator populator = new CustomerCouponPopulator();

	@Mock
	private CustomerCouponModel source;

	@Mock
	private CustomerCouponService customerCouponService;

	@Mock
	private PromotionSourceRuleModel ruleModel;

	private Locale locale;
	private CustomerCouponData target;
	private Date startDate;
	private Date endDate;


	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);
		target = new CustomerCouponData();
		locale = new Locale("en");
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 2);
		startDate = calendar.getTime();
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 10);
		endDate = calendar.getTime();

		Mockito.doReturn(ROOT_CATEGORY_CONFIG_DATA).when(populator).getSolrRootCategory();
		Mockito.doReturn(2).when(populator).getCouponExpireNotificationDays();

		Mockito.doReturn(true).when(customerCouponService).getCouponNotificationStatus(Mockito.anyString());
		final List<PromotionSourceRuleModel> promotionSourceRule = new ArrayList<>();

		Mockito.doReturn(promotionSourceRule).when(customerCouponService).getPromotionSourceRuleForCouponCode(Mockito.anyString());

		populator.setCustomerCouponService(customerCouponService);
		Mockito.when(source.getCouponId()).thenReturn(COUPON_ID);
		Mockito.when(source.getStartDate()).thenReturn(startDate);
		Mockito.when(source.getEndDate()).thenReturn(endDate);
		Mockito.when(source.getName()).thenReturn(COUPON_NAME);
		Mockito.when(source.getDescription()).thenReturn(DESCRIPTION);
		Mockito.when(source.getActive()).thenReturn(true);
	}

	@Test
	public void testPopulator()
	{
		populator.populate(source, target);

		Assert.assertEquals(COUPON_ID, target.getCouponId());
		Assert.assertEquals(COUPON_ID, target.getCouponCode());
		Assert.assertEquals(STATUS, target.getStatus());
		Assert.assertEquals(COUPON_NAME, target.getName());
		Assert.assertEquals(DESCRIPTION, target.getDescription());
		Assert.assertEquals(true, target.isActive());
		Assert.assertEquals(startDate, target.getStartDate());
		Assert.assertEquals(endDate, target.getEndDate());
		Assert.assertEquals(false, target.isBindingAnyProduct());
	}

	@Test
	public void testPopulaterWithList(){

		final List<PromotionSourceRuleModel> promotionSourceRule = new ArrayList<>();
		promotionSourceRule.add(ruleModel);

		Mockito.when(customerCouponService.getPromotionSourceRuleForCouponCode(Mockito.anyString()))
				.thenReturn(promotionSourceRule);
		Mockito.when(customerCouponService.countProductOrCategoryForPromotionSourceRule(Mockito.anyString())).thenReturn(0);

		//test start date and end date is null
		Mockito.when(source.getStartDate()).thenReturn(null);
		Mockito.when(source.getEndDate()).thenReturn(null);

		populator.populate(source, target);
		Assert.assertEquals(STATUS_EFFECTIVE, target.getStatus());

		//test start date after now
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 10);
		startDate = calendar.getTime();
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 10);
		endDate = calendar.getTime();
		Mockito.when(source.getStartDate()).thenReturn(startDate);
		Mockito.when(source.getEndDate()).thenReturn(endDate);

		populator.populate(source, target);
		Assert.assertEquals(STATUS_PRESESSION, target.getStatus());

		//test expire soon
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 10);
		startDate = calendar.getTime();
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
		endDate = calendar.getTime();

		Mockito.when(source.getStartDate()).thenReturn(startDate);
		Mockito.when(source.getEndDate()).thenReturn(endDate);

		populator.populate(source, target);
		Assert.assertEquals(STATUS_PRESESSION, target.getStatus());


	}
}
