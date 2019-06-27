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
package de.hybris.platform.sap.productconfig.runtime.cps.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.constants.SapproductconfigruntimecpsConstants;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.common.CPSContextInfo;
import de.hybris.platform.sap.productconfig.runtime.cps.pricing.PricingConfigurationParameterCPS;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationParameterB2B;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@SuppressWarnings("javadoc")
@UnitTest
public class CPSContextSupplierImplTest
{
	private static final String PRODUCT_CODE = "PRODUCT_CODE";

	private static final String CUSTOMER_NUMBER = "CUSTOMER_NUMBER";
	private static final String COUNTRY_CODE = "COUNTRY_CODE";

	private static final String SALES_ORGANISATION = "SALES_ORGANISATION";
	private static final String DISTRIBUTION_CHANNEL = "DISTRIBUTION_CHANNEL";
	private static final String DIVISION = "DIVISION";


	private CPSContextSupplierImpl classUnderTest;

	@Mock
	private PricingConfigurationParameterCPS pricingConfigurationParameter;

	@Mock
	private ConfigurationParameterB2B configurationParameterB2B;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new CPSContextSupplierImpl();
		classUnderTest.setPricingConfigurationParameter(pricingConfigurationParameter);
		classUnderTest.setConfigurationParameterB2B(configurationParameterB2B);
		Mockito.when(Boolean.valueOf(configurationParameterB2B.isSupported())).thenReturn(Boolean.TRUE);
		Mockito.when(configurationParameterB2B.getCustomerNumber()).thenReturn(CUSTOMER_NUMBER);
		Mockito.when(configurationParameterB2B.getCountrySapCode()).thenReturn(COUNTRY_CODE);
		Mockito.when(pricingConfigurationParameter.getSalesOrganization()).thenReturn(SALES_ORGANISATION);
		Mockito.when(pricingConfigurationParameter.getDistributionChannelForConditions()).thenReturn(DISTRIBUTION_CHANNEL);
		Mockito.when(pricingConfigurationParameter.getDivisionForConditions()).thenReturn(DIVISION);
	}

	@Test
	public void testRetrieveContext()
	{
		final List<CPSContextInfo> context = classUnderTest.retrieveContext(PRODUCT_CODE);
		assertNotNull(context);
		assertEquals(11, context.size());

		assertEquals(SapproductconfigruntimecpsConstants.CONTEXT_ATTRIBUTE_VBAK_KUNNR, context.get(0).getName());
		assertEquals(CUSTOMER_NUMBER, context.get(0).getValue());
		assertEquals(SapproductconfigruntimecpsConstants.CONTEXT_ATTRIBUTE_VBPA_AG_KUNNR, context.get(1).getName());
		assertEquals(CUSTOMER_NUMBER, context.get(1).getValue());
		assertEquals(SapproductconfigruntimecpsConstants.CONTEXT_ATTRIBUTE_VBPA_RG_KUNNR, context.get(2).getName());
		assertEquals(CUSTOMER_NUMBER, context.get(2).getValue());

		assertEquals(SapproductconfigruntimecpsConstants.CONTEXT_ATTRIBUTE_VBPA_AG_LAND1, context.get(3).getName());
		assertEquals(COUNTRY_CODE, context.get(3).getValue());
		assertEquals(SapproductconfigruntimecpsConstants.CONTEXT_ATTRIBUTE_VBPA_RG_LAND1, context.get(4).getName());
		assertEquals(COUNTRY_CODE, context.get(4).getValue());

		assertEquals(SapproductconfigruntimecpsConstants.CONTEXT_ATTRIBUTE_VBAK_VKORG, context.get(5).getName());
		assertEquals(SALES_ORGANISATION, context.get(5).getValue());
		assertEquals(SapproductconfigruntimecpsConstants.CONTEXT_ATTRIBUTE_VBAK_VTWEG, context.get(6).getName());
		assertEquals(DISTRIBUTION_CHANNEL, context.get(6).getValue());
		assertEquals(SapproductconfigruntimecpsConstants.CONTEXT_ATTRIBUTE_VBAK_SPART, context.get(7).getName());
		assertEquals(DIVISION, context.get(7).getValue());

		assertEquals(SapproductconfigruntimecpsConstants.CONTEXT_ATTRIBUTE_VBAK_ERDAT, context.get(8).getName());
		assertEquals(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), context.get(8).getValue());

		assertEquals(SapproductconfigruntimecpsConstants.CONTEXT_ATTRIBUTE_VBAP_MATNR, context.get(9).getName());
		assertEquals(PRODUCT_CODE, context.get(9).getValue());
		assertEquals(SapproductconfigruntimecpsConstants.CONTEXT_ATTRIBUTE_VBAP_KWMENG, context.get(10).getName());
		assertEquals("1", context.get(10).getValue());
	}

	@Test
	public void testRetrieveContextWithoutConfigurationParameters() {
		classUnderTest.setConfigurationParameterB2B(null);
		final List<CPSContextInfo> context = classUnderTest.retrieveContext(PRODUCT_CODE);

		assertNotNull(context);
		assertEquals(6, context.size());
	}

	@Test
	public void testRetrieveContextConfigurationParametersNotSupported() {
		Mockito.when(Boolean.valueOf(configurationParameterB2B.isSupported())).thenReturn(Boolean.FALSE);
		final List<CPSContextInfo> context = classUnderTest.retrieveContext(PRODUCT_CODE);

		assertNotNull(context);
		assertEquals(6, context.size());
	}
}
