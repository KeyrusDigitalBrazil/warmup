/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.assistedservicestorefront.customer360.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.assistedservicepromotionfacades.customer360.CSACouponData;
import de.hybris.platform.assistedservicepromotionfacades.populator.CSACouponDataPopulator;
import de.hybris.platform.couponservices.model.AbstractCouponModel;
import de.hybris.platform.servicelayer.StubLocaleProvider;
import de.hybris.platform.servicelayer.internal.model.impl.LocaleProvider;
import de.hybris.platform.servicelayer.model.ItemModelContextImpl;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;


@UnitTest
public class CSACouponDataPopulatorTest
{
	@InjectMocks
	private final CSACouponDataPopulator populator = new CSACouponDataPopulator();



	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void populateTest()
	{
		final String code = "Test Coupon Code";
		final String name = "Test Coupon Name";
		final AbstractCouponModel csaCouponModel = new AbstractCouponModel();
		final LocaleProvider localeProvider = new StubLocaleProvider(Locale.ENGLISH);
		final ItemModelContextImpl itemModelContext = (ItemModelContextImpl) csaCouponModel.getItemModelContext();

		itemModelContext.setLocaleProvider(localeProvider);
		csaCouponModel.setCouponId(code);
		csaCouponModel.setName(name);


		final CSACouponData csaCouponData = new CSACouponData();

		populator.populate(csaCouponModel, csaCouponData);

		Assert.assertEquals(csaCouponModel.getCouponId(), csaCouponData.getCode());
		Assert.assertEquals(csaCouponModel.getName(), csaCouponData.getName());


	}
}

