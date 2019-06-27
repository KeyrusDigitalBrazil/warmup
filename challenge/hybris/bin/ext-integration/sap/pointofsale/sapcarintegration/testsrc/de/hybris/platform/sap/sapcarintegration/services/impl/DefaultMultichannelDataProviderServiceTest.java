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

import javax.annotation.Resource;
import de.hybris.bootstrap.annotations.UnitTest;

import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.sap.core.test.SapcoreSpringJUnitTest;
import de.hybris.platform.sap.sapcarintegration.services.MultichannelDataProviderService;

@UnitTest
@ContextConfiguration(locations =
{"classpath:test/sapcarintegration-test-spring.xml"})
public class DefaultMultichannelDataProviderServiceTest extends SapcoreSpringJUnitTest{

	@Resource
	private MultichannelDataProviderService multichannelDataProviderService;
	
	@Test
	public void testReadMultiChannelTransactionsFeed(){
		String customerNumber = "0000001000";
		PaginationData paginationData = new PaginationData();
		
		
		ODataFeed feed = multichannelDataProviderService.readMultiChannelTransactionsFeed(customerNumber, paginationData);
		
		assertNotNull(feed);
		
		assertTrue(feed.getEntries().size() > 0);
		
		assertEquals(feed.getEntries().iterator().next().getProperties().get("CustomerNumber"), customerNumber);
		
		
	}

	@Test
	public void testReadSalesDocumentHeaderFeed(){
		
		String customerNumber = "0000001000";
		String transactionNumber = "0000000115"; 
		
		ODataFeed feed = multichannelDataProviderService.readSalesDocumentHeaderFeed(customerNumber, transactionNumber);
		
		assertNotNull(feed);
		
		assertTrue(feed.getEntries().size() > 0);
		
		assertEquals(feed.getEntries().iterator().next().getProperties().get("TransactionNumber"), transactionNumber);
		
		
	}
	
	@Test
	public void testReadSalesDocumentItemFeed(){
		String customerNumber = "0000001000";
		String transactionNumber = "0000000115"; 
		
		ODataFeed feed = multichannelDataProviderService.readSalesDocumentItemFeed(customerNumber, transactionNumber);
		
		assertNotNull(feed);
		
		assertTrue(feed.getEntries().size() > 0);
		
		assertEquals(feed.getEntries().iterator().next().getProperties().get("TransactionNumber"), transactionNumber);
		
		
		
	}

	
}
