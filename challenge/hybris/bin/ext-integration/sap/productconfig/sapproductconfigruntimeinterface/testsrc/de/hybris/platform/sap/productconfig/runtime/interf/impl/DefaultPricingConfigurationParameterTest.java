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
package de.hybris.platform.sap.productconfig.runtime.interf.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.sapmodel.services.SalesAreaService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;



@UnitTest
public class DefaultPricingConfigurationParameterTest
{
	private DefaultPricingConfigurationParameter classUnderTest;

	private static final String SALES_ORGANIZATION_EXAMPLE = "SALES_ORGANIZATION";
	private static final String DISTRIBUTION_CHANNEL_EXAMPLE = "DISTRIBUTION_CHANNEL";
	private static final String DIVISION_EXAMPLE = "DIVISION";
	private static final String CURRENCY_SAP_CODE = "USD";
	private static final String CURRENCY_ISO_CODE = "USD";
	private static final String UNIT_SAP_CODE = "ST";
	private static final String UNIT_ISO_CODE = "PCE";


	@Mock
	protected SalesAreaService commonSalesAreaService;

	@Mock
	protected CurrencyModel currencyModel;

	@Mock
	protected UnitModel unitModel;

	@Mock
	private BaseStoreService baseStoreService;

	@Mock
	private BaseStoreModel baseStore;

	@Mock
	private SAPConfigurationModel sapConfiguration;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		Mockito.when(baseStore.getSAPConfiguration()).thenReturn(sapConfiguration);
		Mockito.when(sapConfiguration.getSapproductconfig_enable_pricing()).thenReturn(Boolean.TRUE);
		Mockito.when(sapConfiguration.getSapproductconfig_show_deltaprices()).thenReturn(Boolean.TRUE);
		Mockito.when(sapConfiguration.getSapproductconfig_show_baseprice_and_options()).thenReturn(Boolean.TRUE);


		classUnderTest = new DefaultPricingConfigurationParameter();
		classUnderTest.setCommonSalesAreaService(commonSalesAreaService);
		classUnderTest.setBaseStoreService(baseStoreService);
	}



	@Test
	public void testGetSalesOrganization()
	{
		when(commonSalesAreaService.getSalesOrganization()).thenReturn(SALES_ORGANIZATION_EXAMPLE);

		final String salesOrganization = classUnderTest.getSalesOrganization();
		assertEquals(SALES_ORGANIZATION_EXAMPLE, salesOrganization);
	}

	@Test
	public void testGetSalesOrganizationValueNotDefined()
	{
		final String salesOrganization = classUnderTest.getSalesOrganization();
		assertNull(salesOrganization);
	}

	@Test
	public void testGetDistributionChannel()
	{
		when(commonSalesAreaService.getDistributionChannelForConditions()).thenReturn(DISTRIBUTION_CHANNEL_EXAMPLE);

		final String distributionChannel = classUnderTest.getDistributionChannelForConditions();
		assertEquals(DISTRIBUTION_CHANNEL_EXAMPLE, distributionChannel);
	}

	@Test
	public void testGetDistributionChannelValueNotDefined()
	{
		final String distributionChannel = classUnderTest.getDistributionChannelForConditions();
		assertNull(distributionChannel);
	}

	@Test
	public void testGetDivision()
	{
		when(commonSalesAreaService.getDivisionForConditions()).thenReturn(DIVISION_EXAMPLE);

		final String division = classUnderTest.getDivisionForConditions();
		assertEquals(DIVISION_EXAMPLE, division);
	}

	@Test
	public void testGetDivisionValueNotDefined()
	{
		final String division = classUnderTest.getDivisionForConditions();
		assertNull(division);
	}

	@Test
	public void testRetrieveCurrencySapCode()
	{
		when(currencyModel.getSapCode()).thenReturn(CURRENCY_SAP_CODE);

		final String currencySapCode = classUnderTest.retrieveCurrencySapCode(currencyModel);
		assertEquals(CURRENCY_SAP_CODE, currencySapCode);

		assertNull(classUnderTest.retrieveCurrencySapCode(null));
	}

	@Test
	public void testRetrieveUnitSapCode()
	{
		when(unitModel.getSapCode()).thenReturn(UNIT_SAP_CODE);

		final String unitSapCode = classUnderTest.retrieveUnitSapCode(unitModel);
		assertEquals(UNIT_SAP_CODE, unitSapCode);

		assertNull(classUnderTest.retrieveUnitSapCode(null));
	}

	@Test
	public void testRetrieveCurrencyIsoCode()
	{
		when(currencyModel.getIsocode()).thenReturn(CURRENCY_ISO_CODE);

		final String currencyIsoCode = classUnderTest.retrieveCurrencyIsoCode(currencyModel);
		assertEquals(CURRENCY_ISO_CODE, currencyIsoCode);

		assertNull(classUnderTest.retrieveCurrencyIsoCode(null));
	}

	@Test
	public void testRetrieveUnitIsoCode()
	{
		when(unitModel.getCode()).thenReturn(UNIT_ISO_CODE);

		final String unitIsoCode = classUnderTest.retrieveUnitIsoCode(unitModel);
		assertEquals(UNIT_ISO_CODE, unitIsoCode);

		assertNull(classUnderTest.retrieveUnitIsoCode(null));
	}

	@Test
	public void testBaseStoreService()
	{
		assertEquals(baseStoreService, classUnderTest.getBaseStoreService());
	}

	@Test(expected = NullPointerException.class)
	public void testGetSAPConfigurationNoBaseStoreService()
	{
		classUnderTest.setBaseStoreService(null);
		classUnderTest.getSAPConfiguration();
	}

	@Test(expected = NullPointerException.class)
	public void testGetSAPConfigurationNoBaseStore()
	{
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(null);
		classUnderTest.getSAPConfiguration();
	}

	@Test(expected = NullPointerException.class)
	public void testGetSAPConfigurationNoSAPConfiguration()
	{
		Mockito.when(baseStore.getSAPConfiguration()).thenReturn(null);
		classUnderTest.getSAPConfiguration();
	}

	@Test
	public void testGetSAPConfiguration()
	{
		assertEquals(sapConfiguration, classUnderTest.getSAPConfiguration());
	}

	@Test
	public void testIsPricingSupported()
	{
		assertTrue(classUnderTest.isPricingSupported());
	}

	@Test
	public void testShowDeltaPrices()
	{
		assertTrue(classUnderTest.showDeltaPrices());
	}

	@Test
	public void testShowBasePriceAndSelectedOptions()
	{
		assertTrue(classUnderTest.showBasePriceAndSelectedOptions());
	}

	@Test
	public void testIsTrue()
	{
		assertTrue(classUnderTest.isTrue(Boolean.TRUE));
		assertFalse(classUnderTest.isTrue(Boolean.FALSE));
	}

	@Test
	public void testIsTrueNull()
	{
		assertFalse(classUnderTest.isTrue(null));
	}

}
