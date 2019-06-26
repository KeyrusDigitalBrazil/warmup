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
package de.hybris.platform.sap.sapinvoiceaddon.document.dao.impl;

import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.sap.sapinvoiceaddon.model.SapB2BDocumentModel;
import de.hybris.platform.sap.sapinvoiceaddon.test.util.SapInvoiceTestsUtil;
import de.hybris.platform.servicelayer.ServicelayerTest;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.springframework.util.StringUtils;
import org.junit.Ignore;


@Ignore
public class B2BInvoiceDaoImplTest extends ServicelayerTest
{
	@Resource
	private B2BInvoiceDaoImpl b2bInvoiceDaoImpl;

	private String impexContent;
	private List<String> invoiceDocumentNumbers;
	private static final String PATH = "/sapinvoiceaddon/test/";
	private static final String IMPEX_TEST_FILE = "testSapB2BDocument.impex";
	private static final int INVOICE_NUMBER_INDEX = 1;
	private static final String INEXISTANT_INVOICE = "inexistInvoice";


	@Before
	public void setUp() throws Exception
	{
		importCsv(PATH + IMPEX_TEST_FILE, "UTF-8");
		impexContent = SapInvoiceTestsUtil.impexFileToString(PATH + IMPEX_TEST_FILE);
		invoiceDocumentNumbers = SapInvoiceTestsUtil.getInvoiceNumbersFromImpex(impexContent, INVOICE_NUMBER_INDEX);

	}


	@Test
	public void testGetEmployeesInUserGroup()
	{
		SapB2BDocumentModel sapInvoiceDocument;
		String invoiceNumFromDao;
		for (final String invoiceDocumentNumber : invoiceDocumentNumbers)
		{
			sapInvoiceDocument = b2bInvoiceDaoImpl.findInvoiceByDocumentNumber(invoiceDocumentNumber);
			invoiceNumFromDao = SapInvoiceTestsUtil.sapB2BDocumentToInvoiceNum(sapInvoiceDocument);
			assertTrue("Dao should return same SapB2bDocument as those in impex file",
					SapInvoiceTestsUtil.compareIds(invoiceDocumentNumber, invoiceNumFromDao));

		}
		//Testing inexistant invoiceNumber
		sapInvoiceDocument = b2bInvoiceDaoImpl.findInvoiceByDocumentNumber(INEXISTANT_INVOICE);
		assertTrue("Dao should not return any SapB2bDocument for invoiceNumber that does not exits " + INEXISTANT_INVOICE,
				StringUtils.isEmpty(sapInvoiceDocument));
	}

}
