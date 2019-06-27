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
package com.hybris.ymkt.segmentation.services;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.b2b.services.B2BUnitService;

import java.io.IOException;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.hybris.ymkt.common.user.UserContextService;
import com.hybris.ymkt.segmentation.dto.SAPInitiative;

/**
 *
 */
public class InitiativeServiceB2BTest
{
	@Mock
	B2BCustomerService b2bCustomerService;
	
	@Mock 
	B2BUnitService b2bUnitService;
	
	@Mock
	B2BCustomerModel b2bCustomerModel;
	
	@Mock
	B2BUnitModel b2bUnitModel;
	
	@Mock
	UserContextService userContextService;
	
	@InjectMocks
	InitiativeServiceB2B initiativeServiceB2B = new InitiativeServiceB2B();
	
	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testGetInteractionContactId() throws IOException
	{
		initiativeServiceB2B.setFilterOnB2BUnit(false);
		
		Mockito.when(b2bCustomerService.getCurrentB2BCustomer()).thenReturn(b2bCustomerModel);
		Mockito.when(b2bCustomerModel.getCustomerID()).thenReturn("testb2b@hybris.com");
		
		Assert.assertEquals("testb2b@hybris.com", initiativeServiceB2B.getInteractionContactId());
	}
	
	@Test
	public void testGetInteractionContactIdWithFilter() throws IOException
	{
		initiativeServiceB2B.setFilterOnB2BUnit(true);
		
		Mockito.when(b2bUnitModel.getUid()).thenReturn("unit-uid-123");
		Mockito.when(b2bUnitService.getParent(b2bCustomerModel)).thenReturn(b2bUnitModel);
		Mockito.when(b2bCustomerService.getCurrentB2BCustomer()).thenReturn(b2bCustomerModel);
		
		Assert.assertEquals("unit-uid-123", initiativeServiceB2B.getInteractionContactId());
	}
}
