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
package de.hybris.platform.platformbackoffice.labelproviders;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.platformbackoffice.taxdiscountvalueparser.ValueParser;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.DiscountValue;
import de.hybris.platform.util.TaxValue;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class TaxDiscountValueRendererTest extends ServicelayerBaseTest
{

	@Resource(name = "taxValueParser")
	ValueParser<TaxValue> taxValueRenderer;

	@Resource(name = "discountValueParser")
	ValueParser<DiscountValue> discountValueRenderer;

	@Resource
	ModelService modelService;

	@Before
	public void before()
	{
		final CurrencyModel currency = modelService.create(CurrencyModel.class);
		currency.setSymbol("zł");
		currency.setDigits(Integer.valueOf(4));
		currency.setIsocode("PLN");

		modelService.save(currency);
	}

	@Test
	public void absoluteTaxShouldRespectLanguageDigits()
	{
		final String renderedValue = taxValueRenderer.render(new TaxValue("Disc-001", 10.40, true, "PLN"));
		assertThat(renderedValue).isEqualTo("Disc-001 : 10.4000 PLN");
	}

	@Test
	public void absoluteTaxShouldNotShowAppliedValue()
	{
		final String renderedValue = taxValueRenderer.render(new TaxValue("Disc-001", 10.40, true, 21.0, "PLN"));
		assertThat(renderedValue).isEqualTo("Disc-001 : 10.4000 PLN");
	}

	@Test
	public void targetPriceDiscountShouldShowAppliedValue()
	{
		final String renderedValue = discountValueRenderer.render(new DiscountValue("Tgt-Disc-001", 99.99, true, 1.0, "PLN", true));
		assertThat(renderedValue).isEqualTo("Tgt-Disc-001 :  T 99.9900 PLN = 1.0000");
	}

	@Test(expected = IllegalArgumentException.class)
	public void absoluteTaxShouldNotShowAppliedValue2()
	{
		final String renderedValue = taxValueRenderer.render(new TaxValue("Disc-001", 10.40, true, 21.0, null));
		assertThat(renderedValue).isEqualTo("Disc-001 : 10.4000 PLN");
	}

	@Test
	public void invalidCurrencyShouldFallbackToDefault()
	{
		final String renderedValue = taxValueRenderer.render(new TaxValue("Disc-001", 10.40, true, 21.0, "none such"));
		assertThat(renderedValue).isEqualTo("Disc-001 : 10.40");
	}

	@Test
	public void relativeTaxShouldShowAppliedValueWith2Digits()
	{
		final String renderedValue = taxValueRenderer.render(new TaxValue("Disc-001", 10.40, false, 23.12012312, null));
		assertThat(renderedValue).isEqualTo("Disc-001 : 10.40% = 23.12");
	}
}
