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
package de.hybris.platform.sap.sapinvoiceservices.services.impl;


import static org.junit.Assert.assertNotNull;
import junit.framework.Assert;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.configuration.SAPConfigurationService;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapinvoicebol.businessobject.SapInvoiceBO;
import de.hybris.platform.sap.sapinvoiceservices.services.SapInvoiceBOFactory;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;



/**
 *
 */
@UnitTest
public class InvoiceServiceImplTest
{
	InvoiceServiceImpl classUnderTest;
	
	@Mock
	SapInvoiceBOFactory sapInvoiceBOFactory;
	
	@Mock
	SAPConfigurationService configurationService;
	
	@Mock
	SapInvoiceBO sapInvoiceBO;
	
	final String invoiceDocumentNumber = "0090012503";
	byte[] sampleByteArray=invoiceDocumentNumber.getBytes();
	
	@Before
	public void init()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new InvoiceServiceImpl();
		classUnderTest.setSapInvoiceBOFactory(sapInvoiceBOFactory);
	}
	
	@Test
	public void getPDFDataTest() throws BackendException
	{
		Mockito.when(classUnderTest.getSapInvoiceBOFactory().getSapInvoiceBO()).thenReturn(sapInvoiceBO);
		Mockito.when(classUnderTest.getSapInvoiceBOFactory().getSapInvoiceBO().getPDF(invoiceDocumentNumber)).thenReturn(sampleByteArray);
		Assert.assertEquals(classUnderTest.getPDFData(invoiceDocumentNumber), sampleByteArray);

	}
	
	
	@Test
	public void testGetSapInvoiceBOFactory()
	{
		assertNotNull(classUnderTest.getSapInvoiceBOFactory());
	}
	
	
}
