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
import de.hybris.platform.personalizationyprofile.mapper.affinity.impl.CxConsumptionLayerSumAffinityStrategy;
import de.hybris.platform.personalizationyprofile.yaas.Affinity;
import de.hybris.platform.personalizationyprofile.yaas.Profile;

import java.math.BigDecimal;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@UnitTest
public class CxProductConsumptionLayerMapperTest extends AbstractCxConsumptionLayerMapperTest
{
	private static final String PRODUCT_1 = "p1";
	private static final String PRODUCT_2 = "p2";

	public CxProductConsumptionLayerMapper productMapper = new CxProductConsumptionLayerMapper();


	@Override
	@Before
	public void init()
	{
		super.init();
		productMapper.setConfigurationService(configurationService);
		productMapper.setAffinityStrategy(new CxConsumptionLayerSumAffinityStrategy());
	}

	@Test
	public void testMissingProfile()
	{
		//given
		final Profile source = null;

		//when
		productMapper.populate(source, target);

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
		productMapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(0, target.getSegments().size());
	}

	@Test
	public void testMissingAffinities()
	{
		//given
		final Profile source = createProfile(null);
		source.getInsights().setAffinities(null);

		//when
		productMapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(0, target.getSegments().size());
	}

	@Test
	public void testMissingProducts()
	{
		//given
		final Profile source = createProfile(null);

		//when
		productMapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(0, target.getSegments().size());
	}

	@Test
	public void testEmptyProducts()
	{
		//given
		final Profile source = createProfile(new HashMap<>());

		//when
		productMapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(0, target.getSegments().size());
	}

	@Test
	public void testMissingProductsAffinity()
	{
		//given
		final Profile source = createProfile(new HashMap<>());
		final HashMap<String, Affinity> productAffinity = new HashMap<>();
		productAffinity.put(PRODUCT_1, null);

		//when
		productMapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(0, target.getSegments().size());
	}

	@Test
	public void testMissingProductsAffinityValues()
	{
		//given
		final HashMap<String, Affinity> productAffinity = new HashMap<>();
		productAffinity.put(PRODUCT_1, createAffinity(null, null));
		final Profile source = createProfile(productAffinity);

		//when
		productMapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(1, target.getSegments().size());
		assertAffinityForSegment(productMapper.getSegmentCode(PRODUCT_1), "0", target);
	}

	@Test
	public void testMissingProductsAffinityScoreValue()
	{
		//given
		final HashMap<String, Affinity> productAffinity = new HashMap<>();
		productAffinity.put(PRODUCT_1, createAffinity(null, BigDecimal.valueOf(10)));
		final Profile source = createProfile(productAffinity);

		//when
		productMapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(1, target.getSegments().size());
		assertAffinityForSegment(productMapper.getSegmentCode(PRODUCT_1), "10", target);
	}

	@Test
	public void testMissingProductsAffinityRecentScoreValue()
	{
		//given
		final HashMap<String, Affinity> productAffinity = new HashMap<>();
		productAffinity.put(PRODUCT_1, createAffinity(BigDecimal.valueOf(10), null));
		final Profile source = createProfile(productAffinity);

		//when
		productMapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(1, target.getSegments().size());
		assertAffinityForSegment(productMapper.getSegmentCode(PRODUCT_1), "10", target);
	}



	@Test
	public void testSingleProductAffinity()
	{
		//given
		final HashMap<String, Affinity> productAffinity = new HashMap<>();
		productAffinity.put(PRODUCT_1, createAffinity(BigDecimal.valueOf(10), BigDecimal.valueOf(10)));
		final Profile source = createProfile(productAffinity);

		//when
		productMapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(1, target.getSegments().size());
		assertAffinityForSegment(productMapper.getSegmentCode(PRODUCT_1), "20", target);
	}

	@Test
	public void testMultipleProductAffinity()
	{
		//given
		final HashMap<String, Affinity> productAffinity = new HashMap<>();
		productAffinity.put(PRODUCT_1, createAffinity(BigDecimal.valueOf(10), BigDecimal.valueOf(10)));
		productAffinity.put(PRODUCT_2, createAffinity(BigDecimal.valueOf(0), BigDecimal.valueOf(10)));
		final Profile source = createProfile(productAffinity);

		//when
		productMapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(2, target.getSegments().size());
		assertAffinityForSegment(productMapper.getSegmentCode(PRODUCT_1), "20", target);
		assertAffinityForSegment(productMapper.getSegmentCode(PRODUCT_2), "10", target);
	}



	protected Profile createProfile(final HashMap<String, Affinity> productAffinities)
	{
		final Profile profile = createProfile();
		profile.getInsights().getAffinities().setProducts(productAffinities);
		return profile;
	}
}
