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
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


@UnitTest
public class CxCategoryConsumptionLayerMapperTest extends AbstractCxConsumptionLayerMapperTest
{
	private static final String CATEGORY_1 = "c1";
	private static final String CATEGORY_2 = "c2";

	public CxCategoryConsumptionLayerMapper categoryMapper = new CxCategoryConsumptionLayerMapper();


	@Override
	@Before
	public void init()
	{
		super.init();
		categoryMapper.setConfigurationService(configurationService);
		categoryMapper.setAffinityStrategy(new CxConsumptionLayerSumAffinityStrategy());
	}

	@Test
	public void testMissingProfile()
	{
		//given
		final Profile source = null;

		//when
		categoryMapper.populate(source, target);

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
		categoryMapper.populate(source, target);

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
		categoryMapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(0, target.getSegments().size());
	}

	@Test
	public void testMissingCategories()
	{
		//given
		final Profile source = createProfile(null);

		//when
		categoryMapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(0, target.getSegments().size());
	}

	@Test
	public void testEmptyCategories()
	{
		//given
		final Profile source = createProfile(new HashMap<>());

		//when
		categoryMapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(0, target.getSegments().size());
	}

	@Test
	public void testMissingCategoriesAffinityValues()
	{
		//given
		final HashMap<String, Affinity> productAffinity = new HashMap<>();
		productAffinity.put(CATEGORY_1, createAffinity(null, null));
		final Profile source = createProfile(productAffinity);

		//when
		categoryMapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(1, target.getSegments().size());
		assertAffinityForSegment(categoryMapper.getSegmentCode(CATEGORY_1), "0", target);
	}

	@Test
	public void testMissingCategoriesAffinityScoreValue()
	{
		//given
		final HashMap<String, Affinity> productAffinity = new HashMap<>();
		productAffinity.put(CATEGORY_1, createAffinity(null, BigDecimal.valueOf(10)));
		final Profile source = createProfile(productAffinity);

		//when
		categoryMapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(1, target.getSegments().size());
		assertAffinityForSegment(categoryMapper.getSegmentCode(CATEGORY_1), "10", target);
	}

	@Test
	public void testMissingCategoriesAffinityRecentScoreValue()
	{
		//given
		final HashMap<String, Affinity> productAffinity = new HashMap<>();
		productAffinity.put(CATEGORY_1, createAffinity(BigDecimal.valueOf(10), null));
		final Profile source = createProfile(productAffinity);

		//when
		categoryMapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(1, target.getSegments().size());
		assertAffinityForSegment(categoryMapper.getSegmentCode(CATEGORY_1), "10", target);
	}

	@Test
	public void testSingleCategoryAffinity()
	{
		//given
		final HashMap<String, Affinity> categoryAffinity = new HashMap<>();
		categoryAffinity.put(CATEGORY_1, createAffinity(BigDecimal.valueOf(10), BigDecimal.valueOf(10)));
		final Profile source = createProfile(categoryAffinity);

		//when
		categoryMapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(1, target.getSegments().size());
		assertAffinityForSegment(categoryMapper.getSegmentCode(CATEGORY_1), "20", target);
	}

	@Test
	public void testMultipleCategoryInput()
	{
		//given
		final HashMap<String, Affinity> categoryAffinity = new HashMap<>();
		categoryAffinity.put(CATEGORY_1, createAffinity(BigDecimal.valueOf(10), BigDecimal.valueOf(10)));
		categoryAffinity.put(CATEGORY_2, createAffinity(BigDecimal.valueOf(0), BigDecimal.valueOf(10)));
		final Profile source = createProfile(categoryAffinity);

		//when
		categoryMapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(2, target.getSegments().size());
		assertAffinityForSegment(categoryMapper.getSegmentCode(CATEGORY_1), "20", target);
		assertAffinityForSegment(categoryMapper.getSegmentCode(CATEGORY_2), "10", target);
	}

	@Test
	public void testRequiredFieldsOn()
	{
		//when
		final Set<String> requiredFields = categoryMapper.getRequiredFields();


		//then
		Assert.assertNotNull(requiredFields);
		Assert.assertEquals(1, requiredFields.size());
	}

	@Test
	public void testRequiredFieldsOff()
	{
		//given
		Mockito.doReturn(Boolean.FALSE).when(configuration).getBoolean(categoryMapper.getEnabledProperty(), true);

		//when
		final Set<String> requiredFields = categoryMapper.getRequiredFields();

		//then
		Assert.assertNotNull(requiredFields);
		Assert.assertEquals(Collections.emptySet(), requiredFields);
	}

	@Test
	public void testMapperOff()
	{
		//given
		Mockito.doReturn(Boolean.FALSE).when(configuration).getBoolean(categoryMapper.getEnabledProperty(), true);

		final HashMap<String, Affinity> categoryAffinity = new HashMap<>();
		categoryAffinity.put(CATEGORY_1, createAffinity(BigDecimal.valueOf(10), BigDecimal.valueOf(10)));
		final Profile source = createProfile(categoryAffinity);

		//when
		categoryMapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(0, target.getSegments().size());
	}

	protected Profile createProfile(final HashMap<String, Affinity> categoryAffinities)
	{
		final Profile profile = createProfile();
		profile.getInsights().getAffinities().setCategories(categoryAffinities);
		return profile;
	}
}
