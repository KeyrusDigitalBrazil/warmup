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
package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v1



import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.commerceservices.storefinder.impl.DefaultStoreFinderService
import de.hybris.platform.core.Registry
import de.hybris.platform.storelocator.GeoWebServiceWrapper
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.TestUtil
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.markers.CollectOutputFromTest

import java.text.DecimalFormatSymbols

import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.junit.experimental.categories.Category


@Category(CollectOutputFromTest.class)
@ManualTest
class StoresTests extends BaseWSTest {

	private static GeoWebServiceWrapper originalServiceWrapper;

	@BeforeClass
	public static void initGeoServiceWrapper() {
		final DefaultStoreFinderService storeFinderService = Registry.getApplicationContext().getBean("defaultStoreFinderService", DefaultStoreFinderService.class);
		originalServiceWrapper = Registry.getApplicationContext().getBean("geoServiceWrapper",GeoWebServiceWrapper.class);
		GeoWebServiceWrapper webServiceWrapper = Registry.getApplicationContext().getBean("mockedGeoServiceWrapper",GeoWebServiceWrapper.class);

		// set mock
		storeFinderService.setGeoWebServiceWrapper(webServiceWrapper);

	}

	@AfterClass
	public static void restoreGeoServiceWrapper() {
		if (originalServiceWrapper != null) {
			final DefaultStoreFinderService storeFinderService = Registry.getApplicationContext().getBean("defaultStoreFinderService", DefaultStoreFinderService.class);
			storeFinderService.setGeoWebServiceWrapper(originalServiceWrapper);
		}
	}

	@Test
	void testGetStoresInMunichJSON() {
		def con = testUtil.getSecureConnection("/stores?query=munich", 'GET', 'JSON', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.stores.size() == 0
		assert response.pagination.totalResults == 0
		assert response.pagination.totalPages == 0
	}

	@Test
	void testGetStoresInChoshiJSON() {
		def con = testUtil.getSecureConnection("/stores?query=choshi&radius=500", 'GET', 'JSON', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.stores.size == 1
		assert response.pagination.pageSize == 10
		assert response.pagination.totalResults == 1
		assert response.pagination.totalPages == 1

		def store = response.stores[0]
		assert store.formattedDistance == '0 km'
		assert store.openingHours == null
		assert store.geoPoint != null
		assert store.name == "WS-Choshi"
		assert store.address.country.name == "Japan"
		assert store.address.country.isocode == "JP"
		assert store.address.town == "Choshi"
		assert store.address.line1 == "Chiba-ken Choshi-shi"

		def storeFeatures = store.features.collect { it.key.toString() }
		assert storeFeatures.containsAll([
			'sundayWorkshops',
			'creche',
			'buyOnlinePickupInStore'
		])

		assert response.locationText == "choshi"
	}

	@Test
	void testGetStoresInTokioWithRadiusJSON() {
		def con = testUtil.getSecureConnection("/stores?query=tokyo&radius=1000", 'GET', 'JSON', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.stores.size == 10
		assert response.pagination.pageSize == 10
		assert response.pagination.totalResults == 11
		assert response.pagination.totalPages == 2
	}

	@Test
	void testGetStoresInTokioAndThenChangeAPageJSON() {

		def con = testUtil.getSecureConnection("/stores?query=tokyo&radius=1000", 'GET', 'JSON', HttpURLConnection.HTTP_OK)
		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.stores.size == 10
		assert response.pagination.pageSize == 10
		assert response.pagination.currentPage == 0
		assert response.pagination.totalResults == 11
		assert response.pagination.totalPages == 2

		con = testUtil.getSecureConnection("/stores?query=tokyo&radius=1000&currentPage=1", 'GET', 'JSON', HttpURLConnection.HTTP_OK)
		response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.stores.size == 1
		assert response.pagination.currentPage == 1
		assert response.pagination.pageSize == 10
		assert response.pagination.totalResults == 11
		assert response.pagination.totalPages == 2
	}

	@Test
	void testGetStoresInChoshiWithOptionsJSON() {
		def con = testUtil.getSecureConnection("/stores?query=Choshi&options=HOURS&radius=500", 'GET', 'JSON', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.stores.size == 1
		assert response.stores[0].formattedDistance == '0 km'
		assert response.stores[0].openingHours != null
		assert response.pagination.pageSize == 10
		assert response.pagination.totalResults == 1
		assert response.pagination.totalPages == 1
	}

	@Test
	void testGetStoresByLatAndLongitudeJSON() {
		def con = testUtil.getSecureConnection("/stores?longitude=139.69&latitude=35.65&radius=4500", 'GET', 'JSON', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedJSONSlurper(con, false, false)
		char decimalSeparator = new DecimalFormatSymbols().getDecimalSeparator();
		assert response.stores.size == 10
		assert response.stores[0].formattedDistance == '4'+decimalSeparator+'4 km'
		assert response.stores[1].formattedDistance == '4'+decimalSeparator+'4 km'
		assert response.pagination.pageSize == 10
		assert response.pagination.totalResults == 11
		assert response.pagination.totalPages == 2
	}

	@Test
	void testGetStoresByLatAndLongitudeAndThenChangeAPageJSON() {

		def con = testUtil.getSecureConnection("/stores?longitude=139.69&latitude=35.65&radius=4500", 'GET', 'JSON', HttpURLConnection.HTTP_OK)
		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.stores.size == 10
		assert response.pagination.pageSize == 10
		assert response.pagination.currentPage == 0
		assert response.pagination.totalResults == 11
		assert response.pagination.totalPages == 2

		con = testUtil.getSecureConnection("/stores?longitude=139.69&latitude=35.65&radius=4500&currentPage=1", 'GET', 'JSON', HttpURLConnection.HTTP_OK)
		response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.stores.size == 1
		assert response.pagination.currentPage == 1
		assert response.pagination.pageSize == 10
		assert response.pagination.totalResults == 11
		assert response.pagination.totalPages == 2
	}

	@Test
	void testGetStoresByLatAndLongitudeWithOptionsJSON() {
		def con = testUtil.getSecureConnection("/stores?longitude=140.8064&latitude=35.7409&options=HOURS&radius=500", 'GET', 'JSON', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.stores.size == 1
		assert response.stores[0].formattedDistance == '0 km'
		assert response.stores[0].openingHours != null
		assert response.pagination.pageSize == 10
		assert response.pagination.totalResults == 1
		assert response.pagination.totalPages == 1
	}

	@Test
	void testGetStoresByLatAndLongitudeWithAccuracyJSON() {
		def con = testUtil.getSecureConnection("/stores?longitude=139.69&latitude=35.65&radius=4000&accuracy=500", 'GET', 'JSON', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedJSONSlurper(con, false, false)
		char decimalSeparator = new DecimalFormatSymbols().getDecimalSeparator();
		assert response.stores.size == 10
		assert response.stores[0].formattedDistance == '4'+decimalSeparator+'4 km'
		assert response.stores[1].formattedDistance == '4'+decimalSeparator+'4 km'
		assert response.pagination.pageSize == 10
		assert response.pagination.totalResults == 11
		assert response.pagination.totalPages == 2
	}

	@Test
	void testGetAllStoresJSON() {
		def con = testUtil.getSecureConnection("/stores", 'GET', 'JSON', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.stores.size == 10
		assert response.pagination.pageSize == 10
		assert response.pagination.totalResults == 49
		assert response.pagination.totalPages == 5
	}

	@Test
	void testGetSpecificStoreJSON() {
		def con = testUtil.getSecureConnection("/stores/WS-Nakano", 'GET', 'JSON', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedJSONSlurper(con, false, false)
		assert response.name == 'WS-Nakano'
		assert response.geoPoint.latitude == 35.6894875
		assert response.geoPoint.longitude == 139.6917064
	}

	@Test
	void testGetSpecificStoreXML() {
		def con = testUtil.getSecureConnection("/stores/WS-Nakano", 'GET', 'XML', HttpURLConnection.HTTP_OK)

		def response = testUtil.verifiedXMLSlurper(con)
		assert response.name == 'WS-Nakano'
		assert response.geoPoint.latitude == 35.6894875
		assert response.geoPoint.longitude == 139.6917064
	}
}