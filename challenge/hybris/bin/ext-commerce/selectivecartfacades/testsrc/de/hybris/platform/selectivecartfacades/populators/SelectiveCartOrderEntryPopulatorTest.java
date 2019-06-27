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
package de.hybris.platform.selectivecartfacades.populators;

import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.selectivecartservices.enums.CartSourceType;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * Junit test suite for {@link SelectiveCartOrderEntryPopulator}
 */
@UnitTest
public class SelectiveCartOrderEntryPopulatorTest
{
	SelectiveCartOrderEntryPopulator selectiveCartOrderEntryPopulator;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		selectiveCartOrderEntryPopulator = new SelectiveCartOrderEntryPopulator();
	}

	@Test
	public void testPopulateWithCreationTime()
	{
		final Date date = new Date();

		final AbstractOrderEntryModel abstractOrderEntryModel1 = mock(AbstractOrderEntryModel.class);
		Mockito.when(abstractOrderEntryModel1.getCreationtime()).thenReturn(date);
		final OrderEntryData orderEntryData1 = new OrderEntryData();

		selectiveCartOrderEntryPopulator.populate(abstractOrderEntryModel1, orderEntryData1);

		Assert.assertEquals(date, orderEntryData1.getAddToCartTime());
		Assert.assertEquals(CartSourceType.STOREFRONT, orderEntryData1.getCartSourceType());
	}

	@Test
	public void testPopulateWithAddToCartTime()
	{
		final Date date = new Date();

		final AbstractOrderEntryModel abstractOrderEntryModel2 = mock(AbstractOrderEntryModel.class);
		Mockito.when(abstractOrderEntryModel2.getAddToCartTime()).thenReturn(date);
		final OrderEntryData orderEntryData2 = new OrderEntryData();

		selectiveCartOrderEntryPopulator.populate(abstractOrderEntryModel2, orderEntryData2);

		Assert.assertEquals(date, orderEntryData2.getAddToCartTime());
		Assert.assertEquals(CartSourceType.STOREFRONT, orderEntryData2.getCartSourceType());
	}
}
