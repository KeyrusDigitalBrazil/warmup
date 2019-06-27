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
/**
 * 
 */
package com.hybris.ymkt.segmentation.services;

import java.io.IOException;
import java.util.Optional;

import org.apache.olingo.odata2.api.edm.EdmException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.hybris.ymkt.common.odata.ODataService;
import com.hybris.ymkt.common.user.UserContextService;
import com.hybris.ymkt.segmentation.dto.SAPInitiative;

/**
 * 
 */
public class InitiativeServiceTest
{	
	@Mock
	UserContextService userContextService;
	
	@Mock
	ODataService oDataService;
	
	@InjectMocks
	InitiativeService initiativeService = new InitiativeService();
	
	@Mock
	InitiativeService initiativeServiceMock;
	
	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testGetInitiative() throws IOException, EdmException
	{
		final SAPInitiative initiativeResult = new SAPInitiative();
		initiativeResult.setId("12345");
		
		Mockito.when(initiativeServiceMock.getInitiative(Mockito.anyString())).thenReturn(Optional.of(initiativeResult));
		
		final Optional<SAPInitiative> initiative = initiativeServiceMock.getInitiative("12345");
		
		Assert.assertTrue(initiative.isPresent());
		Assert.assertEquals("12345", initiative.get().getId());
	}

	@Test
	public void testGetInteractionContactId() throws IOException
	{
		Mockito.when(userContextService.getUserId()).thenReturn("test@hybris.com");
		
		Assert.assertEquals("test@hybris.com", initiativeService.getInteractionContactId());
	}
	
}
