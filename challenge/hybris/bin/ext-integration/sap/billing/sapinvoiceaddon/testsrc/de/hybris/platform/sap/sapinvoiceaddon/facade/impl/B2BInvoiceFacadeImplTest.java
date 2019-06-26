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
package de.hybris.platform.sap.sapinvoiceaddon.facade.impl;

import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.accountsummaryaddon.document.data.B2BDocumentData;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.sapinvoiceaddon.document.service.B2BInvoiceService;
import de.hybris.platform.sap.sapinvoiceaddon.exception.SapInvoiceException;
import de.hybris.platform.sap.sapinvoiceaddon.model.SapB2BDocumentModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;



/**
 *
 */
@UnitTest
public class B2BInvoiceFacadeImplTest
{
	@Mock
	B2BInvoiceFacadeImpl classUnderTest;
	@Mock
	B2BInvoiceService b2BInvoiceService;
	@Mock
	B2BCommerceUnitService b2bCommerceUnitService;
	@Mock
	BaseStoreService baseStoreService;

	@Mock
	private AbstractPopulatingConverter<SapB2BDocumentModel, B2BDocumentData> b2bInvoiceDocumentConverter;
	final String invoiceDocumentNumber = "0090012503";

	@Before
	public void init()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new B2BInvoiceFacadeImpl();
		classUnderTest.setB2BInvoiceService(b2BInvoiceService);
		classUnderTest.setB2bInvoiceDocumentConverter(b2bInvoiceDocumentConverter);
		classUnderTest.setB2BCommerceUnitService(b2bCommerceUnitService);
		classUnderTest.setBaseStoreService(baseStoreService);
		final SapB2BDocumentModel sapB2BDocumentModel = new SapB2BDocumentModel();
		sapB2BDocumentModel.setDocumentNumber(invoiceDocumentNumber);
		final B2BDocumentData b2BDocumentData = new B2BDocumentData();
		b2BDocumentData.setDocumentNumber(invoiceDocumentNumber);
		given(b2bInvoiceDocumentConverter.convert(sapB2BDocumentModel)).willReturn(b2BDocumentData);
	}


	@Test
	public void test() throws SapInvoiceException
	{

		Mockito.when(b2BInvoiceService.getInvoiceForDocumentNumber(invoiceDocumentNumber)).thenReturn(
				createSapB2BDocumentModel(invoiceDocumentNumber));

		//Mockito.when(classUnderTest.determaineSalesAreaUnitID()).thenReturn("sample");
		Mockito.when(b2bCommerceUnitService.getRootUnit()).thenReturn(createB2BModel());
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(getBaseStoreModel());
		Assert.assertEquals(invoiceDocumentNumber, classUnderTest.getOrderForCode(invoiceDocumentNumber).getDocumentNumber());

	}

	/**
	 * @return
	 */
	private BaseStoreModel getBaseStoreModel()
	{
		final BaseStoreModel bs = new BaseStoreModel();
		final SAPConfigurationModel sp = new SAPConfigurationModel();
		sp.setSapcommon_distributionChannel("01");
		sp.setSapcommon_division("01");
		sp.setSapcommon_salesOrganization("0001");
		bs.setSAPConfiguration(sp);

		return bs;
	}




	@Test(expected = SapInvoiceException.class)
	public void testNullDocument() throws SapInvoiceException
	{
		final String invoiceDocumentNumber = "0090012503";
		Mockito.when(b2BInvoiceService.getInvoiceForDocumentNumber(invoiceDocumentNumber)).thenReturn(
				createNullSapB2BDocumentModel());

		Mockito.when(classUnderTest.getOrderForCode(invoiceDocumentNumber)).thenReturn(null);
	}

	@Test
	public void testB2BInvoiceService()
	{
		assertNotNull(classUnderTest.getB2BInvoiceService());
	}

	@Test
	public void testB2bInvoiceDocumentConverter()
	{
		assertNotNull(classUnderTest.getB2bInvoiceDocumentConverter());
	}

	private B2BDocumentData createB2BDocumentData()
	{
		final B2BDocumentData b2bDocumentData = new B2BDocumentData();
		return b2bDocumentData;
	}

	private SapB2BDocumentModel createSapB2BDocumentModel(final String invoiceDocumentNumber)
	{
		final SapB2BDocumentModel b2bDocumentModel = new SapB2BDocumentModel();
		final B2BUnitModel b2bUnitModel = new B2BUnitModel();
		b2bUnitModel.setUid("123_0001_01_01");
		b2bDocumentModel.setDocumentNumber(invoiceDocumentNumber);
		b2bDocumentModel.setUnit(b2bUnitModel);
		return b2bDocumentModel;
	}

	private SapB2BDocumentModel createNullSapB2BDocumentModel()
	{
		return null;
	}

	private B2BUnitModel createB2BModel()
	{
		final B2BUnitModel parentUnit = new B2BUnitModel();
		parentUnit.setUid("123");
		return parentUnit;
	}
}
