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
package de.hybris.platform.commercefacades.promotion.converters.populator;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.promotion.data.PromotionRestrictionData;
import de.hybris.platform.promotions.model.AbstractPromotionRestrictionModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Test suite for {@link PromotionRestrictionPopulator}
 */
@UnitTest
public class PromotionRestrictionPopulatorTest
{
	private static final String RESTRICTION_TYPE = "restrictionType";
	private static final String DESCRIPTION = "desc";
	private PromotionRestrictionPopulator promotionRestrictionPopulator;
	@Mock
	private AbstractPromotionRestrictionModel promotionModel;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		promotionRestrictionPopulator = new PromotionRestrictionPopulator();
		given(promotionModel.getRestrictionType()).willReturn(RESTRICTION_TYPE);
		given(promotionModel.getRenderedDescription()).willReturn(DESCRIPTION);
	}

	@Test
	public void testPopulate()
	{
		final PromotionRestrictionData promotionData = new PromotionRestrictionData();
		promotionRestrictionPopulator.populate(promotionModel, promotionData);

		Assert.assertEquals(RESTRICTION_TYPE, promotionData.getRestrictionType());
		Assert.assertEquals(DESCRIPTION, promotionData.getDescription());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPopulateWithNullSource()
	{
		final PromotionRestrictionData promotionData = new PromotionRestrictionData();
		promotionRestrictionPopulator.populate(null, promotionData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPopulateWithNullTarget()
	{
		promotionRestrictionPopulator.populate(promotionModel, null);
	}

}
