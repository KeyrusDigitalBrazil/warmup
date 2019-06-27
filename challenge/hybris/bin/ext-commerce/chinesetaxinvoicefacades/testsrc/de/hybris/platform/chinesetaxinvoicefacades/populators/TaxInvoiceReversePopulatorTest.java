/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.chinesetaxinvoicefacades.populators;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.chinesetaxinvoicefacades.data.TaxInvoiceData;
import de.hybris.platform.chinesetaxinvoiceservices.enums.InvoiceCategory;
import de.hybris.platform.chinesetaxinvoiceservices.enums.InvoiceRecipientType;
import de.hybris.platform.chinesetaxinvoiceservices.model.TaxInvoiceModel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class TaxInvoiceReversePopulatorTest
{

	private TaxInvoiceReversePopulator populator;
	private TaxInvoiceModel target;

	@Mock
	private TaxInvoiceData source;
	private final InvoiceRecipientType recipientType = InvoiceRecipientType.INDIVIDUAL;
	private final InvoiceCategory invoiceCategory = InvoiceCategory.GENERAL;

	private static final String RECIPIENT = "hybris";

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);

		populator = new TaxInvoiceReversePopulator();
		target = new TaxInvoiceModel();
		given(source.getCategory()).willReturn(invoiceCategory.getCode());
		given(source.getRecipient()).willReturn(RECIPIENT);
		given(source.getRecipientType()).willReturn(recipientType.getCode());
	}

	@Test
	public void testPopulate()
	{
		populator.populate(source, target);

		assertEquals(recipientType, target.getRecipientType());
		assertEquals(invoiceCategory, target.getCategory());
		assertEquals(RECIPIENT, target.getRecipient());
		
		source.setCategory("");
		populator.populate(source, target);

		assertEquals(target.getCategory(), InvoiceCategory.GENERAL);
		
		source.setRecipientType("");
		populator.populate(source, target);

		assertEquals(target.getRecipientType(), InvoiceRecipientType.INDIVIDUAL);
		
	}

	@Test
	public void testPopulateWithNullCategory()
	{
		source.setCategory(null);
		populator.populate(source, target);

		assertEquals(target.getCategory(), InvoiceCategory.GENERAL);

	}
}
