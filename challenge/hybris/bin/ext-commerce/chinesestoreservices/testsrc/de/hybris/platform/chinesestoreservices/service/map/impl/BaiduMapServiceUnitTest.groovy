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
package de.hybris.platform.chinesestoreservices.service.map.impl;

import static org.mockito.Mockito.when;

import de.hybris.platform.chinesestoreservices.map.impl.BaiduMapTools;
import de.hybris.platform.chinesestoreservices.map.impl.BaiduMapsServiceWrapper;
import spock.lang.Specification;
import de.hybris.platform.storelocator.GPS;
import de.hybris.platform.storelocator.data.AddressData;
import de.hybris.platform.storelocator.location.Location;
import de.hybris.platform.storelocator.data.MapLocationData;
import de.hybris.platform.storelocator.impl.DefaultGPS;
import de.hybris.platform.storelocator.exception.GeoServiceWrapperException;

import de.hybris.bootstrap.annotations.UnitTest
import org.junit.Test;

@UnitTest
public class BaiduMapServiceUnitTest extends Specification
{
	
	private static final String BAIDU_GEOCODING_URL_DEFAULT = "http://api.map.baidu.com/geocoder/v2/";

   private BaiduMapTools baiduMapTools;
	
	private Location location;
	private MapLocationData locationData;
	private GPS gps;

	private AddressData address = new AddressData();
	
	private BaiduMapsServiceWrapper wrapper;
		
	def setup()
	{
		
		location = Mock();
		baiduMapTools = Mock();
		locationData = Mock();
		baiduMapTools.setBaseUrl(BAIDU_GEOCODING_URL_DEFAULT);
		
		wrapper = new BaiduMapsServiceWrapper();
		wrapper.setBaiduMapTools(baiduMapTools);
	}

	@Test
	def "testGeocodeAddress"()
	{
		given:
		def gps= Mock(GPS){
			getDecimalLatitude() >> 0.0D;
			getDecimalLongitude() >> 0.0D;
		}
		baiduMapTools.geocodeAddress(address) >> gps;
		
		when:
		GPS result = wrapper.geocodeAddress(address);
								
		then:
		result != null;			
	}
	
	@Test
	def "testGeocodeAddress with null address"()
	{
		BaiduMapTools maptool = new BaiduMapTools();
		when:
		maptool.geocodeAddress(null);
		
		then:
		thrown(NullPointerException);		
	}
	
	
	@Test
	def "testGeocodeAddressbyLocation"()
	{
					
		when:
		wrapper.geocodeAddress(location);
		
		then:
      1 * baiduMapTools.geocodeAddress(location);
      1 * baiduMapTools.setBaseUrl(BAIDU_GEOCODING_URL_DEFAULT);

	}
		

	@Test
	def "testGetAddressQuery with null address"()
	{
		BaiduMapTools maptool = new BaiduMapTools();
		when:
		maptool.getAddressQuery(null);
		
		then:
		thrown(NullPointerException);
		
	}
	
	@Test
	def "testGetAddressQuery"()
	{
	  address.setCity("上海");
     address.setStreet("街道1");
	  BaiduMapTools maptool = new BaiduMapTools();
		
		when:
		def addressStr = maptool.getAddressQuery(address);
		
		then:
		addressStr == "上海+街道1";
		
	}
}
