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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.storelocator.impl.DefaultGPS;
import de.hybris.platform.storelocator.map.Map;
import de.hybris.platform.storelocator.map.impl.DefaultMap;

import org.junit.Test;


/**
 * 
 */
public class MapTest extends AbstractGeocodingTest
{


	/**
	 * Test case checks if IMap can be instantiated and its fields filled with suitable data
	 * 
	 * @throws Exception
	 */
	@Test
	public void testIMapInstantiation() throws Exception
	{
		final String title = "test title";
		final Map map = DefaultMap.create(new DefaultGPS().create(18.09876, 56.5678), 3, title);
		assertNotNull("Map was null", map);
		//kml represents the google map overlays represented in a xml format
		//assertNotNull("Map's inner kml element was null", map.getKml());
		assertTrue("Map's radius was supposed to be 3", 3 == map.getRadius());
		assertEquals("Map's title not as expected", title, map.getTitle());
		assertNull(map.getPointsOfInterest());

	}


}
