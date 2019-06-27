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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.sap.productconfig.runtime.cps.constants.SapproductconfigruntimecpsConstants;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.AccessDate;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.Attribute;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingItemInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.ProductInfo;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSPricingQuantity;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationParameterB2B;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingConfigurationParameter;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigurationRetrievalOptions;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@SuppressWarnings("javadoc")
@UnitTest
public class AbstractPricingItemInputPopulatorTest
{

	private static final String CUSTOMER_NUMBER = "4711";
	private static final String MATERIAL_NUMBER = "12523";
	private static final String SALES_ORG = "TheSalesOrg";
	private static final String DIST_CHAN = "TheDistributionChannel";
	private static final String UOM = "XXX";
	private static final String UOM_ST = "PCE";
	private static final String CURRENCY_USD = "USD";
	private static final String ITEM_ID = "ITEM_ID";
	private AbstractPricingItemInputPopulator classUnderTest;
	private PricingItemInput target;
	@Mock
	private PricingConfigurationParameter pricingConfigurationParameter;
	@Mock
	private ConfigurationParameterB2B configurationParameterB2B;

	@Mock
	private CommonI18NService i18NService;


	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new PricingItemInputKBProductPopulator();
		target = new PricingItemInput();
		classUnderTest.setPricingConfigurationParameter(pricingConfigurationParameter);
		classUnderTest.setConfigurationParameterB2B(configurationParameterB2B);
		classUnderTest.setI18NService(i18NService);
		Mockito.when(pricingConfigurationParameter.getSalesOrganization()).thenReturn(SALES_ORG);
		Mockito.when(pricingConfigurationParameter.getDistributionChannelForConditions()).thenReturn(DIST_CHAN);
		Mockito.when(configurationParameterB2B.getCustomerNumber()).thenReturn(CUSTOMER_NUMBER);
		Mockito.when(Boolean.valueOf(configurationParameterB2B.isSupported())).thenReturn(Boolean.TRUE);
		final UnitModel unitModel = new UnitModel();
		unitModel.setSapCode(UOM_ST);
		Mockito.when(pricingConfigurationParameter.retrieveUnitIsoCode(unitModel)).thenReturn(UOM_ST);
		final ProductModel product = new ProductModel();
		product.setUnit(unitModel);
		final CurrencyModel currencyModel = new CurrencyModel();
		currencyModel.setSapCode(CURRENCY_USD);
		currencyModel.setIsocode(CURRENCY_USD);
		Mockito.when(i18NService.getCurrentCurrency()).thenReturn(currencyModel);
	}


	@Test
	public void testFillCoreAttributes()
	{
		classUnderTest.fillCoreAttributes(ITEM_ID, classUnderTest.createQty(BigDecimal.TEN, UOM), target);
		assertEquals(ITEM_ID, target.getItemId());
		assertEquals(0, target.getQuantity().getValue().compareTo(BigDecimal.TEN));
		assertEquals(UOM, target.getQuantity().getUnit());
		assertNotNull(target.getProductDetails().getAlternateProductUnits());
		assertEquals(0, target.getProductDetails().getAlternateProductUnits().size());
		assertEquals(UOM, target.getProductDetails().getBaseUnit());
	}

	@Test
	public void testCreateQty()
	{
		final CPSPricingQuantity qtyResult = classUnderTest.createQty(BigDecimal.ONE, UOM_ST);
		assertNotNull(qtyResult);
		assertEquals(0, qtyResult.getValue().compareTo(BigDecimal.ONE));
		assertEquals(UOM_ST, qtyResult.getUnit());
	}

	@Test
	public void testCreateProductInfo()
	{
		final ProductInfo result = classUnderTest.createProductInfo(UOM_ST);
		assertNotNull(result);
		assertNotNull(result.getAlternateProductUnits());
		assertEquals(UOM_ST, result.getBaseUnit());
	}


	@Test
	public void testFillAttributes()
	{
		classUnderTest.fillPricingAttributes(MATERIAL_NUMBER, target);
		assertFalse(target.getAttributes().isEmpty());
		assertEquals(10, target.getAttributes().size());
		assertTrue(isAttributePresent(SapproductconfigruntimecpsConstants.PRICING_ATTRIBUTE_SALES_ORG, target.getAttributes()));
		assertTrue(isAttributePresent(SapproductconfigruntimecpsConstants.PRICING_ATTRIBUTE_DIST_CHANNEL, target.getAttributes()));
		assertTrue(isAttributePresent(SapproductconfigruntimecpsConstants.PRICING_ATTRIBUTE_DIVISION, target.getAttributes()));
		assertTrue(isAttributePresent(SapproductconfigruntimecpsConstants.PRICING_ATTRIBUTE_DIVISION_ITEM, target.getAttributes()));
		assertTrue(
				isAttributePresent(SapproductconfigruntimecpsConstants.PRICING_ATTRIBUTE_CUSTOMER_NUMBER, target.getAttributes()));
		assertTrue(isAttributePresent(SapproductconfigruntimecpsConstants.PRICING_ATTRIBUTE_COUNTRY, target.getAttributes()));
		assertTrue(isAttributePresent(SapproductconfigruntimecpsConstants.PRICING_ATTRIBUTE_CUSTOMER_PRICE_GROUP,
				target.getAttributes()));
		assertTrue(isAttributePresent(SapproductconfigruntimecpsConstants.PRICING_ATTRIBUTE_CURRENCY, target.getAttributes()));
		assertTrue(
				isAttributePresent(SapproductconfigruntimecpsConstants.PRICING_ATTRIBUTE_MATERIAL_NUMBER, target.getAttributes()));
		assertTrue(isAttributePresent(SapproductconfigruntimecpsConstants.PRICING_ATTRIBUTE_PRSFD, target.getAttributes()));
	}

	protected boolean isAttributePresent(final String name, final List<Attribute> attributes)
	{
		for (final Attribute att : attributes)
		{
			if (name.equals(att.getName()))
			{
				return true;
			}
		}
		return false;
	}


	@Test
	public void testCreateAttribute()
	{
		final Attribute result = classUnderTest.createAttribute("name", "value1", "value2");
		assertNotNull(result);
		assertEquals("name", result.getName());
		assertEquals(2, result.getValues().size());
		assertEquals("value1", result.getValues().get(0));
		assertEquals("value2", result.getValues().get(1));
	}

	@Test
	public void testCreateAccessDate()
	{
		final AccessDate result = classUnderTest.createAccessDate("name", "value");
		assertNotNull(result);
		assertEquals("name", result.getName());
		assertEquals("value", result.getValue());
	}

	@Test
	public void testFillAccessDates()
	{
		classUnderTest.fillAccessDates(target, null);
		assertEquals(1, target.getAccessDateList().size());
		assertTrue(isDatePresent(SapproductconfigruntimecpsConstants.ACCESS_DATE_PRICE_DATE, target.getAccessDateList()));
	}

	@Test
	public void testGetCurrentDateAsStringLength()
	{
		final String dateAsString = classUnderTest.getPricingDateAsString(null);
		assertEquals("Length must be 10", 10, dateAsString.length());
	}

	@Test
	public void testGetCurrentDateAsStringYear()
	{
		final String dateAsString = classUnderTest.getPricingDateAsString(null);
		final Calendar calendar = new GregorianCalendar();
		final int yearAsInt = calendar.get(Calendar.YEAR);
		assertEquals("Year must match", String.valueOf(yearAsInt), dateAsString.substring(0, 4));
	}

	@Test
	public void testGetCurrentDateWithContext()
	{
		//01.01.1970 00:00
		final Date pricingDate = new Date(0);
		final ConfigurationRetrievalOptions context = new ConfigurationRetrievalOptions();
		context.setPricingDate(pricingDate);
		final String dateAsString = classUnderTest.getPricingDateAsString(context);
		final Calendar calendar = new GregorianCalendar();
		assertEquals("Year 1970 must match", "1970", dateAsString.substring(0, 4));
		assertEquals("Month 01 must match", "01", dateAsString.substring(5, 7));
		assertEquals("Day 01 must match", "01", dateAsString.substring(8, 10));
	}

	protected boolean isDatePresent(final String name, final List<AccessDate> dates)
	{
		for (final AccessDate date : dates)
		{
			if (name.equals(date.getName()))
			{
				return true;
			}
		}
		return false;
	}





}
