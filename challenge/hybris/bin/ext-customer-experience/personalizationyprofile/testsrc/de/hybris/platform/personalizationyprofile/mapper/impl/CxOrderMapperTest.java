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
package de.hybris.platform.personalizationyprofile.mapper.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.personalizationservices.configuration.CxConfigurationService;
import de.hybris.platform.personalizationservices.model.config.CxConfigModel;
import de.hybris.platform.personalizationyprofile.yaas.OrderMetrics;
import de.hybris.platform.personalizationyprofile.yaas.Profile;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.google.common.collect.ImmutableMap;


@UnitTest
public class CxOrderMapperTest extends AbstractCxConsumptionLayerMapperTest
{
	private static final String SEGMENT1 = "Bronze";
	private static final String SEGMENT2 = "Silver";
	private static final String SEGMENT3 = "Gold";

	private static final BigDecimal SEGMENT1_VALUE = BigDecimal.valueOf(1000);
	private static final BigDecimal SEGMENT2_VALUE = BigDecimal.valueOf(2000);
	private static final BigDecimal SEGMENT3_VALUE = BigDecimal.valueOf(3250);

	public CxOrderMapper mapper = new CxOrderMapper();

	@Mock
	CxConfigurationService cxConfigurationService;

	@Mock
	CxConfigModel configModel;

	@Override
	@Before
	public void init()
	{
		super.init();
		mapper.setCxConfigurationService(cxConfigurationService);
		mapper.setConfigurationService(configurationService);

		Mockito.when(cxConfigurationService.getConfiguration()).thenReturn(Optional.of(configModel));
		Mockito.when(configModel.getOrderMapperSegmentMap())
				.thenReturn(ImmutableMap.of(SEGMENT1, SEGMENT1_VALUE, SEGMENT2, SEGMENT2_VALUE, SEGMENT3, SEGMENT3_VALUE));

	}

	@Test
	public void testMissingProfile()
	{
		//given
		final Profile source = null;

		//when
		mapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(0, target.getSegments().size());
	}


	@Test
	public void testMissingInsights()
	{
		//given
		final Profile source = new Profile();

		//when
		mapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(0, target.getSegments().size());
	}

	@Test
	public void testMissingMetrics()
	{
		//given
		final Profile source = createProfile(null);
		source.getInsights().setMetrics(null);

		//when
		mapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(0, target.getSegments().size());
	}

	@Test
	public void testMissingOrders()
	{
		//given
		final Profile source = createProfile(null);

		//when
		mapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(0, target.getSegments().size());
	}


	@Test
	public void testMissingAllOrderValue()
	{
		//given
		final OrderMetrics orderMetrics = createOrderMetrics(null, null, null, null);
		final Profile source = createProfile(orderMetrics);

		//when
		mapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(0, target.getSegments().size());
	}

	@Test
	public void testMissingOrderValueSum()
	{
		//given
		final OrderMetrics orderMetrics = createOrderMetrics(null, 2, BigDecimal.valueOf(250), BigDecimal.valueOf(300));
		final Profile source = createProfile(orderMetrics);

		//when
		mapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(0, target.getSegments().size());
	}

	@Test
	public void testAssignmentToBronze()
	{
		//given
		final OrderMetrics orderMetrics = createOrderMetrics(BigDecimal.valueOf(1100), 2, BigDecimal.valueOf(250),
				BigDecimal.valueOf(300));
		final Profile source = createProfile(orderMetrics);

		//when
		mapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(1, target.getSegments().size());

		Assert.assertEquals(SEGMENT1, target.getSegments().get(0).getCode());
		Assert.assertEquals(BigDecimal.valueOf(110000, 5), target.getSegments().get(0).getAffinity());
	}

	@Test
	public void testAssignmentToGold()
	{
		//given
		final OrderMetrics orderMetrics = createOrderMetrics(BigDecimal.valueOf(7535), 2, BigDecimal.valueOf(250),
				BigDecimal.valueOf(300));
		final Profile source = createProfile(orderMetrics);

		//when
		mapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(1, target.getSegments().size());

		Assert.assertEquals(SEGMENT3, target.getSegments().get(0).getCode());
		Assert.assertEquals(BigDecimal.valueOf(231846, 5), target.getSegments().get(0).getAffinity());
	}

	protected Profile createProfile(final OrderMetrics orderMetrics)
	{
		final Profile profile = createProfile();
		profile.getInsights().getMetrics().setOrders(orderMetrics);
		return profile;
	}

	protected OrderMetrics createOrderMetrics(final BigDecimal allOrdersValuesSum, final Integer allOrdersCount,
			final BigDecimal avgOrderValue, final BigDecimal avgOrderValueAllCustomers)
	{
		final OrderMetrics result = new OrderMetrics();

		result.setAllOrdersCount(allOrdersCount);
		result.setAllOrdersValuesSum(allOrdersValuesSum);
		result.setAvgOrderValue(avgOrderValue);
		result.setAvgOrderValueAllCustomers(avgOrderValueAllCustomers);

		return result;
	}

}
