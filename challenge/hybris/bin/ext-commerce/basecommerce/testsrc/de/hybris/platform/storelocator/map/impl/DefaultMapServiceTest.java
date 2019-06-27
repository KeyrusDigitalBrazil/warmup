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
package de.hybris.platform.storelocator.map.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.storelocator.GPS;
import de.hybris.platform.storelocator.exception.GeoLocatorException;
import de.hybris.platform.storelocator.impl.DefaultGPS;
import de.hybris.platform.storelocator.location.DistanceAwareLocation;
import de.hybris.platform.storelocator.location.Location;
import de.hybris.platform.storelocator.map.Map;
import de.hybris.platform.storelocator.map.utils.MapBounds;
import de.hybris.platform.storelocator.route.RouteService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.assertj.core.data.Offset;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class DefaultMapServiceTest
{
	private static final String MAP_TITLE = "WORLD MAP";

	@InjectMocks
	private DefaultMapService mapService;
	@Mock
	private RouteService routeService;
	private GPS centerPosition;
	private GPS southWest;
	private GPS northEast;

	@Before
	public void setUp() throws Exception
	{
		southWest = new DefaultGPS(25, 30);
		northEast = new DefaultGPS(10, 50);
		centerPosition = new DefaultGPS(5, 5);
	}

	@Test
	public void testGetMapBoundsForMap() throws GeoLocatorException
	{
		final GPS farPosition = mock(GPS.class);
		final GPS nearPosition = mock(GPS.class);
		final GPS centerPosition = mock(GPS.class);
		final Location locationFar = mock(DistanceAwareLocation.class);
		final Location locationNear = mock(DistanceAwareLocation.class);
		final List<Location> locations = new ArrayList<Location>();
		locations.add(locationFar);
		locations.add(locationNear);
		final Map map = mock(Map.class);
		given(map.getPointsOfInterest()).willReturn(locations);
		given(map.getGps()).willReturn(centerPosition);
		given(locationFar.getGPS()).willReturn(farPosition);
		given(locationNear.getGPS()).willReturn(nearPosition);
		given(Double.valueOf(farPosition.getDecimalLatitude())).willReturn(Double.valueOf(50.00));
		given(Double.valueOf(nearPosition.getDecimalLatitude())).willReturn(Double.valueOf(10.00));
		given(Double.valueOf(farPosition.getDecimalLongitude())).willReturn(Double.valueOf(30.00));
		given(Double.valueOf(nearPosition.getDecimalLongitude())).willReturn(Double.valueOf(14.00));

		given(farPosition.create(Mockito.anyDouble(), Mockito.anyDouble())).willReturn(farPosition);
		given(nearPosition.create(Mockito.anyDouble(), Mockito.anyDouble())).willReturn(nearPosition);
		final MapBounds mapBoundsData = mapService.getMapBoundsForMap(map);
		Assert.assertNotNull(mapBoundsData.getNorthEast());
		Assert.assertEquals(farPosition, mapBoundsData.getNorthEast());
		Assert.assertEquals(farPosition, mapBoundsData.getSouthWest());
	}

	@Test
	public void testGetMapBoundsNoPOS() throws GeoLocatorException
	{
		final Map map = mock(Map.class);
		given(map.getPointsOfInterest()).willReturn(Collections.EMPTY_LIST);

		final MapBounds mapBoundsData = mapService.getMapBoundsForMap(map);
		Assert.assertNull(mapBoundsData);
	}

	@Test
	public void shouldRecalculateBoundsAgainstMapCenter() throws GeoLocatorException
	{
		//when
		final MapBounds mapBounds = mapService.recalculateBoundsAgainstMapCenter(southWest, northEast, centerPosition);
		//then
		assertThat(mapBounds.getNorthEast().getDecimalLatitude()).isCloseTo(25, Offset.offset(Double.valueOf(0.01)));
		assertThat(mapBounds.getNorthEast().getDecimalLongitude()).isCloseTo(50, Offset.offset(Double.valueOf(0.01)));
		assertThat(mapBounds.getSouthWest().getDecimalLatitude()).isCloseTo(-15, Offset.offset(Double.valueOf(0.01)));
		assertThat(mapBounds.getSouthWest().getDecimalLongitude()).isCloseTo(-40, Offset.offset(Double.valueOf(0.01)));
	}


	@Test
	public void shouldConfigureDefaultMapRadius() throws Exception
	{
		//when
		final Map map = mapService.getMap(centerPosition, MAP_TITLE);
		//then
		assertThat(map.getRadius()).isEqualTo(DefaultMapService.DEFAULT_RADIUS);
	}

	@Test
	public void shouldSetMapTitle() throws Exception
	{
		//when
		final Map map = mapService.getMap(centerPosition, MAP_TITLE);
		//then
		assertThat(map.getTitle()).isEqualTo(MAP_TITLE);
	}



}
