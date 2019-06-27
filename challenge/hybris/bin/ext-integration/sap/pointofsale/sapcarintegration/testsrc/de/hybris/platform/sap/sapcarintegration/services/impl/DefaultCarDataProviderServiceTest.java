/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.sapcarintegration.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import de.hybris.bootstrap.annotations.UnitTest;

import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.sap.core.test.SapcoreSpringJUnitTest;
import de.hybris.platform.sap.sapcarintegration.services.CarDataProviderService;

@UnitTest
@ContextConfiguration(locations =
{"classpath:test/sapcarintegration-test-spring.xml"})
public class DefaultCarDataProviderServiceTest extends SapcoreSpringJUnitTest{

	@Resource
	private CarDataProviderService carDataProviderService;
	
	@Test
	public void testReadOrders() {
		
		String customerNumber  = "0000001000";
		
		ODataFeed feed = carDataProviderService.readHeaderFeed(customerNumber, new PaginationData());
		
		final List<ODataEntry> foundEntries = feed.getEntries();
		
		assertNotNull(foundEntries);
		
		assertTrue(foundEntries.size() > 0);
		
	}
	
	@Test
	public void testReadOrder() {
		
		String storeId = "R100";
		int transactionIndex = 8;
		String customerNumber = "0000001000";
		String businessDayDate = "20140505";
		
		
		ODataFeed feed = carDataProviderService.readHeaderFeed(businessDayDate, storeId, transactionIndex, customerNumber);
		
		assertNotNull(feed);
		
		final List<ODataEntry> foundEntries = feed.getEntries();
		
		assertNotNull(foundEntries.iterator().next());
		
		assertEquals(foundEntries.iterator().next().getProperties().get("RetailStoreID") , "R100");
		
	}
	
	
	
	
	@Test
	public void testReadLocation() {
		
		String location  = "R100";
		
		ODataFeed feed = carDataProviderService.readLocaltionFeed(location);
		
		assertNotNull(feed);
		
		final List<ODataEntry> foundEntries = feed.getEntries();
		
		assertNotNull(foundEntries.iterator().next().getProperties().get("Location"));
		
		assertEquals(foundEntries.iterator().next().getProperties().get("Location"), "R100");
		
	}
	
	@Test
	public void testReadOrderItems() throws ParseException {
		
		
		String businessDaydate = "20140505";
		String storeId = "R100";
		int transactionIndex = 8;
		String customerNumber = "0000001000";
		
		
		ODataFeed feed = carDataProviderService.readItemFeed(businessDaydate, storeId, transactionIndex, customerNumber);
		
		assertNotNull(feed);
		
		final List<ODataEntry> foundEntries = feed.getEntries();
		
		assertNotNull(foundEntries.iterator().next());
		
		assertEquals(foundEntries.iterator().next().getProperties().get("RetailStoreID"), "R100");
		
		
	}
	
	
}
