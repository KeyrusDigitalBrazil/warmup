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
package de.hybris.platform.sap.saprevenuecloudorder.populators;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import java.math.BigDecimal;

import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.subscriptionfacades.data.UsageChargeEntryData;
import de.hybris.platform.subscriptionservices.model.UsageChargeEntryModel;

public class DefaultSAPRevenueCloudFixedPriceUsageChargeEntryPopulator<SOURCE extends UsageChargeEntryModel, TARGET extends UsageChargeEntryData>
		implements Populator<SOURCE, TARGET> 
{
	private PriceDataFactory priceDataFactory;
	private CommonI18NService commonI18NService;

	@Override
	public void populate(final SOURCE source, final TARGET target) throws ConversionException
	{
		validateParameterNotNullStandardMessage("source", source);
		validateParameterNotNullStandardMessage("target", target);
		
		double price = 0d;
		if (source.getFixedPrice() != null) {
			price = source.getFixedPrice();
		}

		CurrencyModel currency = source.getCurrency();
		if (currency == null) {
			currency = getCommonI18NService().getCurrentCurrency();
		}

		final PriceData priceData = getPriceDataFactory().create(PriceDataType.BUY, BigDecimal.valueOf(price),
				currency);
		target.setFixedPrice(priceData);

	}

	/**
	 * @return the priceDataFactory
	 */
	public PriceDataFactory getPriceDataFactory() 
	{
		return priceDataFactory;
	}

	/**
	 * @param priceDataFactory
	 *            the priceDataFactory to set
	 */
	public void setPriceDataFactory(PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
	}

	/**
	 * @return the commonI18NService
	 */
	public CommonI18NService getCommonI18NService() 
	{
		return commonI18NService;
	}

	/**
	 * @param commonI18NService
	 *            the commonI18NService to set
	 */
	public void setCommonI18NService(CommonI18NService commonI18NService) 
	{
		this.commonI18NService = commonI18NService;
	}
}
