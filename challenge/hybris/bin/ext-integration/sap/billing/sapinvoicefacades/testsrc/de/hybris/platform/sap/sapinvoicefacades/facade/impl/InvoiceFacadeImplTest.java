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
package de.hybris.platform.sap.sapinvoicefacades.facade.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import junit.framework.Assert;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapinvoicefacades.exception.UnableToRetrieveInvoiceException;
import de.hybris.platform.sap.sapinvoiceservices.services.InvoiceService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 *
 */
@UnitTest
public class InvoiceFacadeImplTest
{
	InvoiceFacadeImpl classUnderTest;

	@Mock
	InvoiceService invoiceService;


	final String invoiceDocumentNumber = "0090012503";
	byte[] sampleByteArray = invoiceDocumentNumber.getBytes();

	@Before
	public void init()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new InvoiceFacadeImpl();
		classUnderTest.setInvoiceService(invoiceService);
	}

	@Test
	public void generatePdfTest() throws BackendException, UnableToRetrieveInvoiceException
	{
		Mockito.when(invoiceService.getPDFData(invoiceDocumentNumber)).thenReturn(sampleByteArray);
		Assert.assertEquals(classUnderTest.generatePdf(invoiceDocumentNumber), sampleByteArray);
	}

	@Test
	public void generatePdfExceptionTest() throws UnableToRetrieveInvoiceException
	{
		try{
		Mockito.when(invoiceService.getPDFData(invoiceDocumentNumber)).thenThrow(new BackendException(invoiceDocumentNumber));
		}
		catch(BackendException e)
		{
			fail(" caught backend exception: "+e);
		}	
		
	}

	@Test
	public void testInvoiceService()
	{
		assertNotNull(classUnderTest.getInvoiceService());
	}

}
