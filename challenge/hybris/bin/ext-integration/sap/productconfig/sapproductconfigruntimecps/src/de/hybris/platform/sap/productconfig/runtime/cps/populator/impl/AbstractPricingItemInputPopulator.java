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

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.productconfig.runtime.cps.constants.SapproductconfigruntimecpsConstants;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.AccessDate;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.AlternateProductUnit;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.Attribute;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingItemInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.ProductInfo;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSPricingQuantity;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationParameterB2B;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingConfigurationParameter;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigurationRetrievalOptions;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;


/**
 * Provides re-use functions for CPS pricing populators. Used to create the input for a cps pricing call.
 */
public class AbstractPricingItemInputPopulator
{

	protected static final String X = "X";
	protected static final String EMPTY_STRING = "";
	protected static final boolean STATISTICAL = false;
	private PricingConfigurationParameter pricingConfigurationParameter;
	private ConfigurationParameterB2B configurationParameterB2B;
	private ProductService productService;
	private CommonI18NService i18NService;

	protected AccessDate createAccessDate(final String name, final String value)
	{
		final AccessDate accessDate = new AccessDate();
		accessDate.setName(name);
		accessDate.setValue(value);
		return accessDate;
	}

	protected Attribute createAttribute(final String name, final String... values)
	{
		final Attribute att = new Attribute();
		att.setName(name);
		att.setValues(new ArrayList<String>());
		for (final String value : values)
		{
			att.getValues().add(value);
		}
		return att;
	}

	protected void fillPricingAttributes(final String id, final PricingItemInput target)
	{
		target.setAttributes(new ArrayList<>());
		target.getAttributes().add(createAttribute(SapproductconfigruntimecpsConstants.PRICING_ATTRIBUTE_SALES_ORG,
				getPricingConfigurationParameter().getSalesOrganization()));
		target.getAttributes().add(createAttribute(SapproductconfigruntimecpsConstants.PRICING_ATTRIBUTE_DIST_CHANNEL,
				getPricingConfigurationParameter().getDistributionChannelForConditions()));
		target.getAttributes().add(createAttribute(SapproductconfigruntimecpsConstants.PRICING_ATTRIBUTE_DIVISION,
				getPricingConfigurationParameter().getDivisionForConditions()));
		target.getAttributes().add(createAttribute(SapproductconfigruntimecpsConstants.PRICING_ATTRIBUTE_DIVISION_ITEM,
				getPricingConfigurationParameter().getDivisionForConditions()));

		final CurrencyModel currencyModel = i18NService.getCurrentCurrency();
		final String currency = pricingConfigurationParameter.retrieveCurrencySapCode(currencyModel);
		target.getAttributes().add(createAttribute(SapproductconfigruntimecpsConstants.PRICING_ATTRIBUTE_CURRENCY, currency));

		if (getConfigurationParameterB2B() != null && getConfigurationParameterB2B().isSupported())
		{
			target.getAttributes().add(createAttribute(SapproductconfigruntimecpsConstants.PRICING_ATTRIBUTE_CUSTOMER_NUMBER,
					getConfigurationParameterB2B().getCustomerNumber()));
			target.getAttributes().add(createAttribute(SapproductconfigruntimecpsConstants.PRICING_ATTRIBUTE_COUNTRY,
					getConfigurationParameterB2B().getCountrySapCode()));
			target.getAttributes().add(createAttribute(SapproductconfigruntimecpsConstants.PRICING_ATTRIBUTE_CUSTOMER_PRICE_GROUP,
					getConfigurationParameterB2B().getCustomerPriceGroup()));
		}
		target.getAttributes().add(createAttribute(SapproductconfigruntimecpsConstants.PRICING_ATTRIBUTE_MATERIAL_NUMBER, id));
		target.getAttributes().add(createAttribute(SapproductconfigruntimecpsConstants.PRICING_ATTRIBUTE_PRSFD, X));
	}

	protected void fillAccessDates(final PricingItemInput target, final ConfigurationRetrievalOptions context)
	{
		target.setAccessDateList(new ArrayList<>());
		final String currentDateAsString = getPricingDateAsString(context);
		target.getAccessDateList()
				.add(createAccessDate(SapproductconfigruntimecpsConstants.ACCESS_DATE_PRICE_DATE, currentDateAsString));
	}

	protected String getPricingDateAsString(final ConfigurationRetrievalOptions context)
	{
		if (context != null && context.getPricingDate() != null)
		{
			return context.getPricingDate().toInstant().toString().substring(0, 10);
		}
		return Instant.now().toString().substring(0, 10);
	}

	protected ProductInfo createProductInfo(final String uom)
	{
		final ProductInfo productInfo = new ProductInfo();
		productInfo.setProductId(EMPTY_STRING);
		productInfo.setBaseUnit(uom);
		productInfo.setAlternateProductUnits(new ArrayList<AlternateProductUnit>());
		return productInfo;
	}

	/**
	 * @return PricingConfigurationParameter, reflecting the backoffice customizing used for pricing
	 */
	public PricingConfigurationParameter getPricingConfigurationParameter()
	{
		return pricingConfigurationParameter;
	}

	/**
	 * @param pricingConfigurationParameter
	 *           Backoffice customizing used for pricing
	 */
	public void setPricingConfigurationParameter(final PricingConfigurationParameter pricingConfigurationParameter)
	{
		this.pricingConfigurationParameter = pricingConfigurationParameter;
	}

	/**
	 * @return Pricing attributes needed in B2B context
	 */
	public ConfigurationParameterB2B getConfigurationParameterB2B()
	{
		return configurationParameterB2B;
	}

	/**
	 * @param configurationParameterB2B
	 *           Pricing attributes needed in B2B context
	 */
	public void setConfigurationParameterB2B(final ConfigurationParameterB2B configurationParameterB2B)
	{
		this.configurationParameterB2B = configurationParameterB2B;
	}

	protected CPSPricingQuantity createQty(final BigDecimal value, final String uom)
	{
		final CPSPricingQuantity qty = new CPSPricingQuantity();
		qty.setUnit(uom);
		qty.setValue(value);
		return qty;
	}

	protected void fillCoreAttributes(final String id, final CPSPricingQuantity quantity, final PricingItemInput target)
	{
		target.setItemId(id);
		target.setQuantity(quantity);
		target.setProductDetails(createProductInfo(quantity.getUnit()));
		target.setStatistical(STATISTICAL);
	}

	protected ProductService getProductService()
	{
		return productService;
	}

	/**
	 * @param productService
	 *           product service
	 */
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	/**
	 * @return the i18NService
	 */
	protected CommonI18NService getI18NService()
	{
		return i18NService;
	}

	/**
	 * @param i18nService
	 *           the i18NService to set
	 */
	public void setI18NService(final CommonI18NService i18nService)
	{
		i18NService = i18nService;
	}
}
