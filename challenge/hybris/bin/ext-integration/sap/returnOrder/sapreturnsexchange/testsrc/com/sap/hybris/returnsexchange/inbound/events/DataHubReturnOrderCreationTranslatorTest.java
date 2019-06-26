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
package com.sap.hybris.returnsexchange.inbound.events;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Mockito.*;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.jalo.security.JaloSecurityException;
import de.hybris.platform.sap.orderexchange.constants.DataHubInboundConstants;
import  com.sap.hybris.returnsexchange.inbound.DataHubInboundOrderHelper;


@SuppressWarnings("javadoc")
@UnitTest
public class DataHubReturnOrderCreationTranslatorTest
{

	@InjectMocks
	private DataHubReturnOrderCreationTranslator classUnderTest;
	@Mock
	private Item processedItem;

	@Mock
	private DataHubInboundOrderHelper orderHubService;

	@Before
	public void setUp() throws JaloInvalidParameterException, JaloSecurityException
	{
		classUnderTest = new DataHubReturnOrderCreationTranslator();
		processedItem = 	Mockito.mock(Item.class);
		orderHubService = 	Mockito.mock(DataHubInboundOrderHelper.class);
		classUnderTest.setInboundHelper(orderHubService);
		Mockito.when(processedItem.getAttribute(DataHubInboundConstants.CODE)).thenReturn("0815");
	}

	@Test
	public void testPerformConfirmImportNull() throws ImpExException, JaloInvalidParameterException, JaloSecurityException
	{
		classUnderTest.performImport(null, processedItem);
		Mockito.verify(orderHubService).processOrderConfirmationFromDataHub("0815");
	}

	@Test
	public void testPerformConfirmImportIgnore() throws ImpExException, JaloInvalidParameterException, JaloSecurityException
	{
		classUnderTest.performImport("ignore", processedItem);
				Mockito.verify(orderHubService).processOrderConfirmationFromDataHub("0815");
	}
}
