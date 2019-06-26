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
package de.hybris.platform.sap.productconfig.runtime.cps.populator.impl;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingDocumentInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingItemInput;
import de.hybris.platform.sap.productconfig.runtime.cps.pricing.PricingConfigurationParameterCPS;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@SuppressWarnings("javadoc")
@UnitTest
public class AbstractPricingDocumentInputPopulatorTest
{

	private static final String CURRENCY = "EUR";
	private static final String PRICING_PROCEDURE = "WECNUS";
	private AbstractPricingDocumentInputPopulator classUnderTest;
	private PricingDocumentInput pricingDocumentInput;
	@Mock
	private PricingConfigurationParameterCPS pricingConfigurationParameter;
	@Mock
	private CommonI18NService i18NService;


	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new PricingDocumentInputKBPopulator();
		classUnderTest.setPricingConfigurationParameter(pricingConfigurationParameter);
		Mockito.when(pricingConfigurationParameter.retrieveCurrencyIsoCode(Mockito.any())).thenReturn(CURRENCY);
		Mockito.when(pricingConfigurationParameter.getPricingProcedure()).thenReturn(PRICING_PROCEDURE);
		classUnderTest.setI18NService(i18NService);
		pricingDocumentInput = new PricingDocumentInput();
		pricingDocumentInput.setItems(new ArrayList<PricingItemInput>());
	}


	@Test
	public void testFillCoreAttributes()
	{
		classUnderTest.fillCoreAttributes(pricingDocumentInput);
		assertEquals(CURRENCY, pricingDocumentInput.getDocCurrency());
		assertEquals(CURRENCY, pricingDocumentInput.getLocCurrency());
		assertEquals(PRICING_PROCEDURE, pricingDocumentInput.getPricingProcedure());
		assertEquals(Boolean.valueOf(PricingDocumentInputPopulator.GROUP_CONDITION),
				Boolean.valueOf(pricingDocumentInput.isGroupCondition()));
		assertEquals(Boolean.valueOf(PricingDocumentInputPopulator.ITEM_CONDITION_REQUIRED),
				Boolean.valueOf(pricingDocumentInput.isItemConditionsRequired()));

	}



}
