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
package de.hybris.platform.storelocator.pos;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.GeoWebServiceWrapper;
import de.hybris.platform.storelocator.PointOfServiceDao;
import de.hybris.platform.storelocator.exception.GeoLocatorException;
import de.hybris.platform.storelocator.exception.LocationInstantiationException;
import de.hybris.platform.storelocator.exception.PointOfServiceDaoException;
import de.hybris.platform.storelocator.location.Location;
import de.hybris.platform.storelocator.location.impl.DefaultLocationMapService;
import de.hybris.platform.storelocator.location.impl.DefaultLocationService;
import de.hybris.platform.storelocator.location.impl.DistanceUnawareLocation;
import de.hybris.platform.storelocator.map.MapService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.storelocator.pos.impl.DefaultPointOfServiceService;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;


/**
 * Test class for {@link PointOfServiceService}.
 */
@UnitTest
public class PointOfServiceTest
{
	private static final double RADIUS_MAX = 100D;
	private static final double RADIUS_STEP = 20D;

	@Mock
	private GeoWebServiceWrapper geoServiceWrapper;

	@Mock
	private PointOfServiceDao pointOfServiceDao;

	@Mock
	private MapService mapService;

	@Spy
	private final DefaultLocationService locationService = new DefaultLocationService();

	private final DefaultPointOfServiceService pointOfServiceService = new DefaultPointOfServiceService();

	private final DefaultLocationMapService locationMapService = new DefaultLocationMapService();



	@Before
	public void setUp() throws GeoLocatorException, LocationInstantiationException
	{
		MockitoAnnotations.initMocks(this);

		locationMapService.setGeoServiceWrapper(geoServiceWrapper);
		locationMapService.setMapService(mapService);
		locationMapService.setRadiusMax(RADIUS_MAX);
		locationMapService.setRadiusStep(RADIUS_STEP);

		locationService.setMapService(mapService);
		locationMapService.setLocationService(locationService);
		pointOfServiceService.setPointOfServiceDao(pointOfServiceDao);
		locationService.setLocationMapService(locationMapService);

		final BaseStoreModel baseStore = new BaseStoreModel();
		baseStore.setUid("littleShop");


		final PointOfServiceModel pos = new PointOfServiceModel();
		pos.setName("myPos");
		pos.setBaseStore(baseStore);

		final Location location = new DistanceUnawareLocation(pos);
		final ArrayList<Location> locations = new ArrayList<Location>();
		locations.add(location);

		final ArrayList<Location> locationsNearby = new ArrayList<Location>();
		locationsNearby.add(location);
		for (int i = 0; i < 9; i++)
		{
			locationsNearby.add(new DistanceUnawareLocation(pos));
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetPointOfServiceForNameWhenNameIsNull()
	{
		pointOfServiceService.getPointOfServiceForName(null);
	}


	@Test(expected = UnknownIdentifierException.class)
	public void testGetPointOfServiceForNameWhenNameNotFound() throws PointOfServiceDaoException
	{
		Mockito.when(pointOfServiceDao.getPosByName("notExistingPOS")).thenThrow(new PointOfServiceDaoException());
		pointOfServiceService.getPointOfServiceForName("notExistingPOS");
	}

	@Test
	public void testGetPointOfServiceForName() throws PointOfServiceDaoException
	{
		final PointOfServiceModel pointOfServiceModel = Mockito.mock(PointOfServiceModel.class);
		Mockito.when(pointOfServiceDao.getPosByName("existingPOS")).thenReturn(pointOfServiceModel);
		final PointOfServiceModel result = pointOfServiceService.getPointOfServiceForName("existingPOS");

		Assert.assertEquals(pointOfServiceModel, result);
	}

}
