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

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import de.hybris.bootstrap.annotations.UnitTest;

import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.sap.core.test.SapcoreSpringJUnitTest;
import de.hybris.platform.sap.sapcarintegration.data.CarOrderHistoryData;
import de.hybris.platform.sap.sapcarintegration.services.CarOrderHistoryService;

@UnitTest
@ContextConfiguration(locations =
{"classpath:test/sapcarintegration-test-spring.xml"})
public class DefaultCarOrderHistoryServiceTest extends SapcoreSpringJUnitTest{

	
	@Resource
	private CarOrderHistoryService carOrderHistoryService;
	
	
	@Test
	public void testReadOrdersForCustomer() throws Exception{
		
		String customerNumber = "0000001000";
		
		List<CarOrderHistoryData> orders = carOrderHistoryService.readOrdersForCustomer(customerNumber, new PaginationData());
		
		assertNotNull(orders);
		
		assertTrue(orders.size() > 0);
		
		
	}
	
	@Test
	public void testReadOrderDetails(){
		
		String storeId = "R100";
		int transactionIndex = 8;
		String customerNumber = "0000001000";
		String businessDayDate = "20140505";
		
		CarOrderHistoryData  order = carOrderHistoryService.readOrderDetails(businessDayDate, storeId, transactionIndex, customerNumber);

		assertNotNull(order);
		
		assertEquals(order.getStore().getStoreId(), "R100");


	}
	
}
