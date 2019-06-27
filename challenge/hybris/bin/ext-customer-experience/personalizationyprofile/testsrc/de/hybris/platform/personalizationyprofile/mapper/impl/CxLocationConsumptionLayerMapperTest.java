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
import de.hybris.platform.personalizationyprofile.yaas.Location;
import de.hybris.platform.personalizationyprofile.yaas.LocationsAffinity;
import de.hybris.platform.personalizationyprofile.yaas.Profile;

import java.math.BigDecimal;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@UnitTest
public class CxLocationConsumptionLayerMapperTest extends AbstractCxConsumptionLayerMapperTest
{
	private static final String COUNTRY1 = "PL";
	private static final String COUNTRY2 = "DE";
	private static final String REGION1 = "SL";
	private static final String REGION2 = "BY";
	private static final String CITY_A = "Katowice";
	private static final String CITY_B = "Gliwice";
	private static final String CITY_C = "München";

	public CxLocationConsumptionLayerMapper mapper = new CxLocationConsumptionLayerMapper();


	@Override
	@Before
	public void init()
	{
		super.init();
		mapper.setConfigurationService(configurationService);
		mapper.setAffinityStrategy(new CxConsumptionLayerSumAffinityStrategy());
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
	public void testMissingAffinities()
	{
		//given
		final Profile source = createProfile(null);
		source.getInsights().setAffinities(null);

		//when
		mapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(0, target.getSegments().size());
	}

	@Test
	public void testMissingAffinityLocationMap()
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
	public void testEmptyAffinityLocationMap()
	{
		//given
		final Profile source = createProfile(new HashMap<>());

		//when
		mapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(0, target.getSegments().size());
	}

	@Test
	public void testMissingAffinityLocationsValues()
	{
		//given
		final HashMap<String, LocationsAffinity> locationsAffinity = new HashMap<>();
		locationsAffinity.put(CITY_A, null);
		final Profile source = createProfile(locationsAffinity);
		//when
		mapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(0, target.getSegments().size());
	}

	@Test
	public void testMissingAffinityLocationFieldValues()
	{
		//given
		final HashMap<String, LocationsAffinity> locationsAffinity = new HashMap<>();
		locationsAffinity.put(CITY_A, createLocationAffinity(null, null, null, null, null, null));
		final Profile source = createProfile(locationsAffinity);
		//when
		mapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(0, target.getSegments().size());
	}

	@Test
	public void testMissingLocation()
	{
		//given
		final HashMap<String, LocationsAffinity> locationsAffinity = new HashMap<>();
		locationsAffinity.put(CITY_A, createLocationAffinity(null, null, null, 1, BigDecimal.valueOf(10), BigDecimal.valueOf(20)));
		final Profile source = createProfile(locationsAffinity);
		//when
		mapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(0, target.getSegments().size());
	}

	@Test
	public void testMissingLocationValues()
	{
		//given
		final HashMap<String, LocationsAffinity> locationsAffinity = new HashMap<>();
		final LocationsAffinity affinity = createLocationAffinity(null, null, null, 1, BigDecimal.valueOf(10),
				BigDecimal.valueOf(20));
		affinity.setLocation(new Location());
		locationsAffinity.put(CITY_A, affinity);
		final Profile source = createProfile(locationsAffinity);
		//when
		mapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(0, target.getSegments().size());
	}

	@Test
	public void testMissingCountry()
	{
		//given
		final HashMap<String, LocationsAffinity> locationsAffinity = new HashMap<>();
		locationsAffinity.put(CITY_A,
				createLocationAffinity(null, REGION1, CITY_A, 1, BigDecimal.valueOf(10), BigDecimal.valueOf(20)));
		final Profile source = createProfile(locationsAffinity);
		//when
		mapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(0, target.getSegments().size());
	}

	@Test
	public void testMissingRegion()
	{
		//given
		final HashMap<String, LocationsAffinity> locationsAffinity = new HashMap<>();
		locationsAffinity.put(CITY_A, createLocationAffinity(COUNTRY1, null, CITY_A, 1, BigDecimal.valueOf(10), null));
		final Profile source = createProfile(locationsAffinity);
		//when
		mapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(1, target.getSegments().size());

		assertAffinityForSegment(mapper.getSegmentCode(COUNTRY1), "10", target);
	}

	@Test
	public void testMissingCity()
	{
		//given
		final HashMap<String, LocationsAffinity> locationsAffinity = new HashMap<>();
		locationsAffinity.put(CITY_A, createLocationAffinity(COUNTRY1, REGION1, null, 1, null, BigDecimal.valueOf(20)));
		final Profile source = createProfile(locationsAffinity);
		//when
		mapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(2, target.getSegments().size());

		assertAffinityForSegment(mapper.getSegmentCode(COUNTRY1), "20", target);
		assertAffinityForSegment(mapper.getSegmentCode(COUNTRY1 + "_" + REGION1), "20", target);
	}

	@Test
	public void testSingleLocationAffinity()
	{
		//given
		final HashMap<String, LocationsAffinity> locationsAffinity = new HashMap<>();
		locationsAffinity.put(CITY_A,
				createLocationAffinity(COUNTRY1, REGION1, CITY_A, 1, BigDecimal.valueOf(10), BigDecimal.valueOf(20)));
		final Profile source = createProfile(locationsAffinity);

		//when
		mapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(3, target.getSegments().size());

		assertAffinityForSegment(mapper.getSegmentCode(COUNTRY1), "30", target);
		assertAffinityForSegment(mapper.getSegmentCode(COUNTRY1 + "_" + REGION1), "30", target);
		assertAffinityForSegment(mapper.getSegmentCode(COUNTRY1 + "_" + REGION1 + "_" + CITY_A), "30", target);
	}

	@Test
	public void testMultipleDistinctLocationInput()
	{
		//given
		final HashMap<String, LocationsAffinity> locationsAffinity = new HashMap<>();
		locationsAffinity.put(CITY_A,
				createLocationAffinity(COUNTRY1, REGION1, CITY_A, 1, BigDecimal.valueOf(10), BigDecimal.valueOf(20)));
		locationsAffinity.put(CITY_C,
				createLocationAffinity(COUNTRY2, REGION2, CITY_C, 3, BigDecimal.valueOf(15), BigDecimal.valueOf(25)));
		final Profile source = createProfile(locationsAffinity);

		//when
		mapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(6, target.getSegments().size());

		assertAffinityForSegment(mapper.getSegmentCode(COUNTRY1), "30", target);
		assertAffinityForSegment(mapper.getSegmentCode(COUNTRY1 + "_" + REGION1), "30", target);
		assertAffinityForSegment(mapper.getSegmentCode(COUNTRY1 + "_" + REGION1 + "_" + CITY_A), "30", target);

		assertAffinityForSegment(mapper.getSegmentCode(COUNTRY2), "40", target);
		assertAffinityForSegment(mapper.getSegmentCode(COUNTRY2 + "_" + REGION2), "40", target);
		assertAffinityForSegment(mapper.getSegmentCode(COUNTRY2 + "_" + REGION2 + "_" + CITY_C), "40", target);
	}

	@Test
	public void testMultipleSimilarLocationInput()
	{
		//given
		final HashMap<String, LocationsAffinity> locationsAffinity = new HashMap<>();
		locationsAffinity.put(CITY_A,
				createLocationAffinity(COUNTRY1, REGION1, CITY_A, 1, BigDecimal.valueOf(10), BigDecimal.valueOf(20)));
		locationsAffinity.put(CITY_B,
				createLocationAffinity(COUNTRY1, REGION1, CITY_B, 3, BigDecimal.valueOf(15), BigDecimal.valueOf(25)));
		locationsAffinity.put(CITY_C,
				createLocationAffinity(COUNTRY2, REGION2, CITY_C, 3, BigDecimal.valueOf(15), BigDecimal.valueOf(25)));
		final Profile source = createProfile(locationsAffinity);

		//when
		mapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(7, target.getSegments().size());

		assertAffinityForSegment(mapper.getSegmentCode(COUNTRY1), "70", target);
		assertAffinityForSegment(mapper.getSegmentCode(COUNTRY1 + "_" + REGION1), "70", target);
		assertAffinityForSegment(mapper.getSegmentCode(COUNTRY1 + "_" + REGION1 + "_" + CITY_A), "30", target);
		assertAffinityForSegment(mapper.getSegmentCode(COUNTRY1 + "_" + REGION1 + "_" + CITY_B), "40", target);

		assertAffinityForSegment(mapper.getSegmentCode(COUNTRY2), "40", target);
		assertAffinityForSegment(mapper.getSegmentCode(COUNTRY2 + "_" + REGION2), "40", target);
		assertAffinityForSegment(mapper.getSegmentCode(COUNTRY2 + "_" + REGION2 + "_" + CITY_C), "40", target);
	}


	@Test
	public void testNullLocationInput()
	{
		//given
		final HashMap<String, LocationsAffinity> locationsAffinity = new HashMap<>();
		locationsAffinity.put(CITY_A, createLocationAffinity(null, null, null, 1, BigDecimal.valueOf(10), BigDecimal.valueOf(20)));
		locationsAffinity.put(CITY_B,
				createLocationAffinity(COUNTRY1, null, null, 3, BigDecimal.valueOf(15), BigDecimal.valueOf(25)));
		locationsAffinity.put(CITY_C,
				createLocationAffinity(COUNTRY2, REGION2, CITY_C, 3, BigDecimal.valueOf(15), BigDecimal.valueOf(25)));
		final Profile source = createProfile(locationsAffinity);

		//when
		mapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(4, target.getSegments().size());

		assertAffinityForSegment(mapper.getSegmentCode(COUNTRY1), "40", target);

		assertAffinityForSegment(mapper.getSegmentCode(COUNTRY2), "40", target);
		assertAffinityForSegment(mapper.getSegmentCode(COUNTRY2 + "_" + REGION2), "40", target);
		assertAffinityForSegment(mapper.getSegmentCode(COUNTRY2 + "_" + REGION2 + "_" + CITY_C), "40", target);
	}

	protected Profile createProfile(final HashMap<String, LocationsAffinity> locationAffinities)
	{
		final Profile profile = createProfile();
		profile.getInsights().getAffinities().setLocations(locationAffinities);
		return profile;
	}

	protected LocationsAffinity createLocationAffinity(final String country, final String region, final String city,
			final Integer count, final BigDecimal score, final BigDecimal recentScore)
	{
		final LocationsAffinity result = new LocationsAffinity();

		result.setScore(score);
		result.setRecentScore(recentScore);
		result.setRecentCount(count);

		if (country != null || region != null || city != null)
		{
			result.setLocation(new Location());
			result.getLocation().setCountryCode(country);
			result.getLocation().setRegionCode(region);
			result.getLocation().setCity(city);
		}
		return result;
	}

}
