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
import de.hybris.platform.sap.saprevenuecloudorder.pojo.Bills;
import de.hybris.platform.sap.saprevenuecloudorder.util.SapRevenueCloudSubscriptionUtil;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.subscriptionfacades.data.SubscriptionBillingData;

/**
 * Populate DTO {@link SubscriptionBillingData} with data from {@link Bills}.
 *
 * @param <SOURCE> source class
 * @param <TARGET> target class
 */

public class DefaultSAPRevenueCloudSubscriptionBillsPopulator implements Populator<Bills,SubscriptionBillingData> 
{
	private PriceDataFactory priceDataFactory;
	private CommonI18NService commonI18NService;
	
	@Override
	public void populate(Bills source, SubscriptionBillingData target) throws ConversionException
	{
		validateParameterNotNullStandardMessage("source", source);
		validateParameterNotNullStandardMessage("target", target);
		
		final PriceData priceData = getPriceDataFactory().create(PriceDataType.BUY, BigDecimal.valueOf(source.getTotalAmount()),
				 getCommonI18NService().getCurrentCurrency().getIsocode());
		 target.setSubscriptionBillDate(SapRevenueCloudSubscriptionUtil.stringToDate(source.getBillingDate()));
		 target.setPrice(priceData);
		 target.setBillingDate(source.getBillingDate());
		 target.setBillingId(((Integer)source.getDocumentNumber()).toString());
		 target.setItems(((Integer)source.getBillItems().size()).toString());
	}
	
	public CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	public void setCommonI18NService(CommonI18NService commonI18NService) 
	{
		this.commonI18NService = commonI18NService;
	}

	public PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}

	public void setPriceDataFactory(PriceDataFactory priceDataFactory) 
	{
		this.priceDataFactory = priceDataFactory;
	}
}
