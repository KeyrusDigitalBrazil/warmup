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
package de.hybris.platform.storelocator;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.storelocator.location.Location;

import org.junit.Before;
import org.junit.Test;


/**
 * 
 */
public class LocationServiceTest extends AbstractGeocodingTest
{



	private Location location = null;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		location = createTestLocation("testLocation", "Nymphenburger Strasse", "86", "80636", "Muenchen", "DE");
	}

	@Test
	public void testCreateAndDeleteLocation() throws Exception
	{
		final boolean passed = getLocationService().saveOrUpdateLocation(location);
		assertTrue("Save Location failed", passed);
		assertNotNull(getLocationService().getLocationByName(location.getName()));
		assertTrue("Delete Location failed", getLocationService().deleteLocation(location));
		assertNull(getLocationService().getLocationByName(location.getName()));
	}


}
