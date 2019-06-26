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
package de.hybris.platform.sap.productconfig.runtime.ssc.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.sapmodel.services.SalesAreaService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@SuppressWarnings("javadoc")
@UnitTest
public class DefaultPricingConfigurationParameterTestSSC
{
	private static final String PRICING_PROCEDURE_EXAMPLE = "PRICING_PROCEDURE";
	private static final String BASE_PRICE_EXAMPLE = "BASE_PRICE";
	private static final String SELECTED_OPTIONS_EXAMPLE = "SELECTED_OPTION";

	private DefaultPricingConfigurationParameterSSC pricingParameterSSC;

	@Mock
	protected SalesAreaService commonSalesAreaService;

	@Mock
	private BaseStoreService baseStoreService;

	@Mock
	private BaseStoreModel baseStore;

	@Mock
	private SAPConfigurationModel sapConfiguration;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		Mockito.when(baseStore.getSAPConfiguration()).thenReturn(sapConfiguration);
		Mockito.when(sapConfiguration.getSapproductconfig_condfunc_baseprice()).thenReturn(BASE_PRICE_EXAMPLE);
		Mockito.when(sapConfiguration.getSapproductconfig_condfunc_selectedoptions()).thenReturn(SELECTED_OPTIONS_EXAMPLE);
		Mockito.when(sapConfiguration.getSapproductconfig_pricingprocedure()).thenReturn(PRICING_PROCEDURE_EXAMPLE);

		pricingParameterSSC = new DefaultPricingConfigurationParameterSSC();
		pricingParameterSSC.setCommonSalesAreaService(commonSalesAreaService);
		pricingParameterSSC.setBaseStoreService(baseStoreService);
	}

	@Test
	public void testGetPricingProcedure()
	{

		final String priceProcedure = pricingParameterSSC.getPricingProcedure();
		assertEquals(PRICING_PROCEDURE_EXAMPLE, priceProcedure);
	}

	@Test
	public void testGetCondFuncForBasePrice()
	{
		final String condFuncBasePrice = pricingParameterSSC.getTargetForBasePrice();
		assertEquals(BASE_PRICE_EXAMPLE, condFuncBasePrice);
	}

	@Test
	public void testGetCondFuncForSelectedOptions()
	{

		final String condFuncSelectedOptions = pricingParameterSSC.getTargetForSelectedOptions();
		assertEquals(SELECTED_OPTIONS_EXAMPLE, condFuncSelectedOptions);
	}

	@Test
	public void testGetPricingProcedureValueNotDefined()
	{
		Mockito.when(sapConfiguration.getSapproductconfig_pricingprocedure()).thenReturn(null);

		final String priceProcedure = pricingParameterSSC.getPricingProcedure();
		assertNull(priceProcedure);
	}

	@Test
	public void testGetCondFuncForBasePriceValueNotDefined()
	{
		Mockito.when(sapConfiguration.getSapproductconfig_condfunc_baseprice()).thenReturn(null);
		final String condFuncBasePrice = pricingParameterSSC.getTargetForBasePrice();
		assertNull(condFuncBasePrice);
	}

	@Test
	public void testGetCondFuncForSelectedOptionsValueNotDefined()
	{
		Mockito.when(sapConfiguration.getSapproductconfig_condfunc_selectedoptions()).thenReturn(null);
		final String condFuncSelectedOptions = pricingParameterSSC.getTargetForSelectedOptions();
		assertNull(condFuncSelectedOptions);
	}

}
