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

import de.hybris.bootstrap.annotations.ManualTest;
import de.hybris.platform.storelocator.AbstractGeocodingTest;
import de.hybris.platform.storelocator.GPS;
import de.hybris.platform.storelocator.data.AddressData;
import de.hybris.platform.storelocator.exception.LocationServiceException;
import de.hybris.platform.storelocator.location.Location;
import de.hybris.platform.storelocator.location.LocationMapService;
import de.hybris.platform.storelocator.location.LocationService;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Integration test for {@link PointOfServiceService}
 */
@ManualTest
//manual test: sended request count to external ressouce is limited
public class PointOfServiceServiceIntegrationTest extends AbstractGeocodingTest
{
	@Resource
	private LocationMapService locationMapService;

	@Resource
	private LocationService<Location> locationService;

	private String postalCode;
	private String countryCode;
	private String town;
	private GPS gpsTown;
	private GPS gpsPostalcode;

	@Before
	public void setUp() throws Exception
	{
		postalCode = "80636";
		countryCode = "DE";
		town = "Muenchen";
		final AddressData addressData1 = new AddressData();
		addressData1.setCity(town);
		final AddressData addressData2 = new AddressData();
		addressData2.setCountryCode(countryCode);
		addressData2.setZip(postalCode);
		gpsTown = getGeoServiceWrapper().geocodeAddress(addressData1);
		gpsPostalcode = getGeoServiceWrapper().geocodeAddress(addressData2);
		createCoreData();
		createTestCronJob(Integer.valueOf(20), Integer.valueOf(1));
		createTestPosEntries();
	}

	@Test
	public void testService()
	{
		getGeocodeAddressesJob().perform(getCronJobService().getCronJob("testCronJob"));
		try
		{
			Assert.assertEquals(5, locationService.getLocationsForPostcode(postalCode, countryCode, 5, null).size());
			Assert.assertEquals(5, locationService.getLocationsForTown(town, 5, null).size());
			Assert.assertEquals(5, locationService.getLocationsForPoint(gpsPostalcode, 5, null).size());
			Assert.assertEquals(gpsPostalcode.toDMSString(),
					locationMapService.getMapOfLocationsForPostcode(postalCode, countryCode, 5, null).getGps().toDMSString());
			Assert.assertEquals(gpsTown.toDMSString(),
					locationMapService.getMapOfLocationsForTown(town, 5, null).getGps().toDMSString());
		}
		catch (final LocationServiceException e)
		{
			Assert.fail("Exception in integration test for PointOfServiceService:" + e);
		}
	}
}
