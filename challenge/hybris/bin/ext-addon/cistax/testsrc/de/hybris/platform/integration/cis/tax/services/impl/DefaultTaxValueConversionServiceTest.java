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
package de.hybris.platform.integration.cis.tax.services.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.hybris.cis.client.tax.models.CisTaxLine;
import com.hybris.cis.client.tax.models.CisTaxValue;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.integration.cis.tax.service.TaxValueConversionService;
import de.hybris.platform.integration.cis.tax.service.impl.DefaultTaxValueConversionService;
import de.hybris.platform.util.TaxValue;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.BDDMockito.given;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTaxValueConversionServiceTest
{
	private TaxValueConversionService taxValueConversionService;
	private List<CisTaxLine> taxLines;

	@Before
	public void setUp()
	{
		taxValueConversionService = new DefaultTaxValueConversionService();
		stubTaxLines();
	}

	public void stubTaxLines()
	{
		taxLines = new ArrayList<CisTaxLine>();
		final CisTaxLine taxLine = Mockito.mock(CisTaxLine.class);

		final CisTaxValue taxValue = Mockito.mock(CisTaxValue.class);
		given(taxValue.getLevel()).willReturn("State");
		given(taxValue.getValue()).willReturn(BigDecimal.valueOf(12.75d));
		given(taxLine.getTaxValues()).willReturn(Collections.singletonList(taxValue));

		final CisTaxLine shippingTaxLine = Mockito.mock(CisTaxLine.class);
		final CisTaxValue shippingTaxValue = Mockito.mock(CisTaxValue.class);
		given(shippingTaxValue.getLevel()).willReturn("Shipping");
		given(shippingTaxValue.getValue()).willReturn(BigDecimal.valueOf(1.75d));
		given(shippingTaxLine.getTaxValues()).willReturn(Collections.singletonList(shippingTaxValue));

		taxLines.add(taxLine);
		taxLines.add(shippingTaxLine);
	}

	@Test
	public void shouldCalculateTaxValues()
	{
		final CisTaxLine taxLine = Mockito.mock(CisTaxLine.class);
		final CisTaxValue taxValue = Mockito.mock(CisTaxValue.class);
		given(taxValue.getLevel()).willReturn("State");
		given(taxValue.getValue()).willReturn(BigDecimal.valueOf(1.75d));

		given(taxLine.getTaxValues()).willReturn(Collections.singletonList(taxValue));
		final List<TaxValue> taxValues = taxValueConversionService
				.getShippingTaxes(Collections.singletonList(taxLine), "USD", true);

		Assert.assertNotNull(taxValues);
		Assert.assertNotNull(taxValues.get(0));
		Assert.assertEquals(Double.valueOf(1.75d), Double.valueOf(taxValues.get(0).getValue()));
		Assert.assertTrue(taxValues.get(0).getCurrencyIsoCode().equals("USD"));
	}

	@Test
	public void shouldGetShippingTaxes()
	{
		final List<TaxValue> taxValues = taxValueConversionService.getShippingTaxes(taxLines, "USD", true);

		Assert.assertNotNull(taxValues);
		Assert.assertNotNull(taxValues.get(0));
		Assert.assertEquals(Double.valueOf(1.75d), Double.valueOf(taxValues.get(0).getValue()));
		Assert.assertTrue(taxValues.get(0).getCurrencyIsoCode().equals("USD"));
	}

	@Test
	public void shouldNotGetShippingTaxes()
	{
		final List<TaxValue> taxValues = taxValueConversionService.getShippingTaxes(taxLines, "USD", false);

		Assert.assertNull(taxValues);
	}
}
