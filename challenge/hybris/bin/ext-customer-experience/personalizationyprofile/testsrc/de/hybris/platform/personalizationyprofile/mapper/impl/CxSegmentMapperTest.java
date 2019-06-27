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
import de.hybris.platform.personalizationyprofile.yaas.Profile;
import de.hybris.platform.personalizationyprofile.yaas.Segment;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


@UnitTest
public class CxSegmentMapperTest extends AbstractCxConsumptionLayerMapperTest
{
	private static final String SEGMENT_1 = "c1";
	private static final String SEGMENT_2 = "c2";

	public CxSegmentMapper segmentMapper = new CxSegmentMapper();
	@Mock
	CxConfigurationService cxConfigurationService;

	@Override
	@Before
	public void init()
	{
		super.init();
		segmentMapper.setConfigurationService(configurationService);
		segmentMapper.setCxConfigurationService(cxConfigurationService);
		segmentMapper.setPrefix(" ");

		Mockito.doReturn(BigDecimal.ZERO).when(cxConfigurationService).getMinAffinity();
	}

	@Test
	public void testNullSource()
	{
		//given
		final Profile source = null;

		//when
		segmentMapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(0, target.getSegments().size());
	}


	@Test
	public void testMissingSegments()
	{
		//given
		final Profile source = new Profile();

		//when
		segmentMapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(0, target.getSegments().size());
	}

	@Test
	public void testEmptySegments()
	{
		//given
		final Profile source = createProfile(new HashMap<>());

		//when
		segmentMapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(0, target.getSegments().size());
	}

	@Test
	public void testSingleSgment()
	{
		//given
		final HashMap<String, Segment> segmentMap = new HashMap<>();
		segmentMap.put(SEGMENT_1, new Segment());
		final Profile source = createProfile(segmentMap);

		//when
		segmentMapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(1, target.getSegments().size());
		assertAffinityForSegment(SEGMENT_1, "10", target);
	}

	@Test
	public void testMultipleSgment()
	{
		//given
		final HashMap<String, Segment> segmentMap = new HashMap<>();
		segmentMap.put(SEGMENT_1, new Segment());
		segmentMap.put(SEGMENT_2, new Segment());
		final Profile source = createProfile(segmentMap);

		//when
		segmentMapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(2, target.getSegments().size());
		assertAffinityForSegment(SEGMENT_1, "10", target);
		assertAffinityForSegment(SEGMENT_2, "10", target);
	}

	@Test
	public void testMultipleSgmentWithPrefix()
	{
		//given
		final String prefix = "pre";
		segmentMapper.setPrefix(prefix);

		final HashMap<String, Segment> segmentMap = new HashMap<>();
		segmentMap.put(SEGMENT_1, new Segment());
		segmentMap.put(SEGMENT_2, new Segment());
		final Profile source = createProfile(segmentMap);

		//when
		segmentMapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(2, target.getSegments().size());
		assertAffinityForSegment(prefix + " " + SEGMENT_1, "10", target);
		assertAffinityForSegment(prefix + " " + SEGMENT_2, "10", target);
	}

	@Test
	public void testRequiredFieldsOn()
	{
		//when
		final Set<String> requiredFields = segmentMapper.getRequiredFields();


		//then
		Assert.assertNotNull(requiredFields);
		Assert.assertEquals(1, requiredFields.size());
	}

	@Test
	public void testRequiredFieldsOff()
	{
		//given
		Mockito.doReturn(Boolean.FALSE).when(configuration).getBoolean(segmentMapper.getEnabledProperty(), true);

		//when
		final Set<String> requiredFields = segmentMapper.getRequiredFields();

		//then
		Assert.assertNotNull(requiredFields);
		Assert.assertEquals(Collections.emptySet(), requiredFields);
	}

	@Test
	public void testMapperOff()
	{
		//given
		Mockito.doReturn(Boolean.FALSE).when(configuration).getBoolean(segmentMapper.getEnabledProperty(), true);

		final HashMap<String, Segment> segmentMap = new HashMap<>();
		segmentMap.put(SEGMENT_1, new Segment());
		final Profile source = createProfile(segmentMap);

		//when
		segmentMapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(0, target.getSegments().size());
	}

	protected Profile createProfile(final HashMap<String, Segment> segments)
	{
		final Profile profile = createProfile();
		profile.setSegments(segments);
		return profile;
	}

}
