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
import java.util.ArrayList;
import java.util.List;

import com.sap.hybris.saprevenuecloudproduct.model.SAPRatePlanElementModel;
import com.sap.hybris.saprevenuecloudproduct.service.SapRevenueCloudProductService;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.BillItem;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.Charge;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.RatingPeriod;
import de.hybris.platform.sap.saprevenuecloudorder.util.SapRevenueCloudSubscriptionUtil;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.subscriptionfacades.data.SubscriptionBillingData;
import de.hybris.platform.subscriptionfacades.data.UsageChargeData;
import de.hybris.platform.subscriptionfacades.data.UsageUnitData;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;
import de.hybris.platform.subscriptionservices.model.UsageUnitModel;


/**
 * Populate DTO {@link SubscriptionBillingData} with data from {@link BillItem}.
 *
 * @param <SOURCE> source class
 * @param <TARGET> target class
 */

public class DefaultSAPRevenueCloudBillDetailPopulator implements Populator<BillItem,SubscriptionBillingData>
{

	private SapRevenueCloudProductService sapRevenueCloudProductService;
	private PriceDataFactory priceDataFactory; 
	private CommonI18NService commonI18NService;  
	private Populator<ProductModel, ProductData> productUrlPopulator;
	private CMSSiteService cmsSiteService;

	@Override
	public void populate(BillItem source, SubscriptionBillingData target) throws ConversionException 
	{
		validateParameterNotNullStandardMessage("source", source);
		validateParameterNotNullStandardMessage("target", target);
		
		target.setProductCode(source.getProductCode());
		target.setSubscriptionId(source.getSubscriptionDocumentNumber().toString());
		final PriceData totalAmount = getPriceDataFactory().create(PriceDataType.BUY, BigDecimal.valueOf(source.getTotalAmount()),
				 getCommonI18NService().getCurrentCurrency().getIsocode());
		target.setPrice(totalAmount);
		target.setCharges(populateBillingCharges(source));
		final CatalogVersionModel currentCatalog = getCmsSiteService().getCurrentCatalogVersion();
		final SubscriptionPricePlanModel pricePlanModel = getSapRevenueCloudProductService()
				.getSubscriptionPricePlanForId(source.getRatePlanId(), currentCatalog);
		final ProductModel productModel = pricePlanModel.getProduct();
		final ProductData productData = new ProductData();
		getProductUrlPopulator().populate(productModel, productData);
		target.setProductUrl(productData.getUrl());
	}
	
	protected List<UsageChargeData> populateBillingCharges(BillItem billItem)
	{
		List<UsageChargeData> chargeEntries = new ArrayList<>();
		if(null!=billItem.getCharges())
		{
			for(Charge charge : billItem.getCharges())
			{
				UsageChargeData usageChargeData = new UsageChargeData();
				RatingPeriod period = charge.getRatingPeriod();
				usageChargeData.setFromDate(SapRevenueCloudSubscriptionUtil.stringToDate(period.getStart()));
				if(period.getEnd()!=null && !period.getEnd().isEmpty())
				{
					usageChargeData.setToDate(SapRevenueCloudSubscriptionUtil.stringToDate(period.getEnd()));
				}
				SAPRatePlanElementModel planElementModel = getSapRevenueCloudProductService().getRatePlanElementfromId(charge.getMetricId());
				if(null!=planElementModel)
				{
					usageChargeData.setName(planElementModel.getName());
					if("usage".equalsIgnoreCase(planElementModel.getType().getCode()))
					{
						UsageUnitModel unitModel = getSapRevenueCloudProductService().getUsageUnitfromId(charge.getMetricId());
						UsageUnitData unitData = new UsageUnitData();
						if(unitModel!=null)
						{
							if(charge.getConsumedQuantity().getValueWithDecimals()>0)
							{
								unitData.setId(unitModel.getNamePlural());
							}
							else
							{
								unitData.setId(unitModel.getName());
							}
						}
						usageChargeData.setUsage(charge.getConsumedQuantity().getValueWithDecimals());
						usageChargeData.setUsageUnit(unitData);
					}
				}
				final PriceData netAmount = getPriceDataFactory().create(PriceDataType.BUY, new BigDecimal(charge.getAmount()),
						 getCommonI18NService().getCurrentCurrency().getIsocode());
				usageChargeData.setNetAmount(netAmount);
				chargeEntries.add(usageChargeData);
			}
		}
		return chargeEntries;
	}

	public PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}

	public void setPriceDataFactory(PriceDataFactory priceDataFactory) 
	{
		this.priceDataFactory = priceDataFactory;
	}

	public SapRevenueCloudProductService getSapRevenueCloudProductService() 
	{
		return sapRevenueCloudProductService;
	}

	public void setSapRevenueCloudProductService(SapRevenueCloudProductService sapRevenueCloudProductService)
	{
		this.sapRevenueCloudProductService = sapRevenueCloudProductService;
	}
	
	
	public CommonI18NService getCommonI18NService() 
	{
		return commonI18NService;
	}

	public void setCommonI18NService(CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}
	

	public Populator<ProductModel, ProductData> getProductUrlPopulator() {
		return productUrlPopulator;
	}

	public void setProductUrlPopulator(Populator<ProductModel, ProductData> productUrlPopulator) {
		this.productUrlPopulator = productUrlPopulator;
	}

	public CMSSiteService getCmsSiteService() {
		return cmsSiteService;
	}

	public void setCmsSiteService(CMSSiteService cmsSiteService) {
		this.cmsSiteService = cmsSiteService;
	}



}
