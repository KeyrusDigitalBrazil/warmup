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
package com.sap.hybris.returnsexchange.outbound.impl;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.sap.hybris.returnsexchange.constants.ReturnOrderEntryCsvColumns;

import de.hybris.bootstrap.annotations.UnitTest;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class DefaultCancelReturnOrderEntryContributorTest {
	
	@InjectMocks
	DefaultCancelReturnOrderEntryContributor classUnderTest = new DefaultCancelReturnOrderEntryContributor();
	
	private Set<String> columns;
		
	@Before
	public void setUp()
	{
		//Nothing to do
	}
	@Test
	public void testGetColumns() 
	{
		columns = classUnderTest.getColumns();		
		assertTrue(columns.contains(ReturnOrderEntryCsvColumns.REASON_CODE_FOR_RETURN_CANCELLATION));
	}

}
