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
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;

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
import de.hybris.platform.sap.saprevenuecloudorder.constants.SaprevenuecloudorderConstants;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.BillItem;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.Bills;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.Charge;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.RatingPeriod;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.Subscription;
import de.hybris.platform.sap.saprevenuecloudorder.service.SapRevenueCloudSubscriptionService;
import de.hybris.platform.sap.saprevenuecloudorder.util.SapRevenueCloudSubscriptionUtil;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.subscriptionfacades.data.SubscriptionData;
import de.hybris.platform.subscriptionfacades.data.SubscriptionPricePlanData;
import de.hybris.platform.subscriptionfacades.data.UsageChargeData;
import de.hybris.platform.subscriptionfacades.data.UsageUnitData;
import de.hybris.platform.subscriptionservices.enums.SubscriptionStatus;
import de.hybris.platform.subscriptionservices.model.BillingFrequencyModel;
import de.hybris.platform.subscriptionservices.model.BillingPlanModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;
import de.hybris.platform.subscriptionservices.model.UsageUnitModel;

/**
 * Populate DTO {@link SubscriptionData} with data from {@link Subscription}.
 *
 * @param <SOURCE> source class
 * @param <TARGET> target class
 */

public class DefaultSAPRevenueCloudSubscriptionDetailPopulator implements Populator<Subscription,SubscriptionData> {
	
	private static final Logger LOG = Logger.getLogger(DefaultSAPRevenueCloudSubscriptionDetailPopulator.class);
	
	private CMSSiteService cmsSiteService;
	private SapRevenueCloudProductService sapRevenueCloudProductService;
	private Populator<ProductModel, ProductData> productUrlPopulator;
	private Populator <ProductModel, ProductData> subscriptionProductPricePlanPopulator;
	private SapRevenueCloudSubscriptionService sapRevenueCloudSubscriptionService;
	private PriceDataFactory priceDataFactory;
	private CommonI18NService commonI18NService;

	@Override
	public void populate(Subscription source, SubscriptionData target) throws ConversionException
	{
		validateParameterNotNullStandardMessage("source", source);
		validateParameterNotNullStandardMessage("target", target);
		
		target.setDescription("SAP Subscription For Product: "
				+ source.getSnapshots().get(0).getItems().get(0).getProduct().getCode());
		target.setId(source.getSubscriptionId());//
		target.setDocumentNumber(source.getDocumentNumber());
		target
				.setProductCode(source.getSnapshots().get(0).getItems().get(0).getProduct().getCode());
		target.setCustomerId(source.getCustomer().getId());
		target.setStartDate(SapRevenueCloudSubscriptionUtil.formatDate(source.getValidFrom()));
		if (source.getValidUntil() != null && !source.getValidUntil().isEmpty()) 
		{
			target.setEndDate(SapRevenueCloudSubscriptionUtil.formatDate(source.getValidUntil()));
			target.setValidTillDate(source.getValidUntil());
		}
		target.setVersion(source.getMetaData().getVersion());
		populateStatus(source, target);
		final String ratePlanId = source.getSnapshots().get(0).getItems().get(0).getRatePlan().getId();
		target.setRatePlanId(ratePlanId);
		final CatalogVersionModel currentCatalog = getCmsSiteService().getCurrentCatalogVersion();
		final SubscriptionPricePlanModel pricePlanModel = getSapRevenueCloudProductService()
				.getSubscriptionPricePlanForId(ratePlanId, currentCatalog);
		final ProductModel productModel = pricePlanModel.getProduct();
		target.setName(pricePlanModel.getProduct().getName());
		final ProductData productData = new ProductData();
		getProductUrlPopulator().populate(productModel, productData);
		target.setProductUrl(productData.getUrl());
		
		populatePricePlan(productModel,productData);
		populateCurrentUsage(source.getSubscriptionId(), target);
		//Get Current Usage
		BillingFrequencyModel billingFrequencyModel = getSapRevenueCloudSubscriptionService().getBillingFrequency(productModel);
		if(null!=billingFrequencyModel)
		{
			target.setBillingFrequency(billingFrequencyModel.getNameInCart());
			BillingPlanModel billingPlanModel = productModel.getSubscriptionTerm().getBillingPlan();
			populateBillingFrequency(billingPlanModel,target);
		}
		else 
		{
			LOG.error(productModel.getCode() + " doesnt contain subscription term");
		}
		target.setPricePlan((SubscriptionPricePlanData)productData.getPrice());
		
	}
	
	protected void populateStatus(final Subscription sapSubscription, final SubscriptionData subscriptionData) 
	{
		SubscriptionStatus status = SubscriptionStatus.ACTIVE;
		if (sapSubscription.getCancellationReason() != null && !sapSubscription.getCancellationReason().isEmpty()) 
		{
			status = SubscriptionStatus.CANCELLED;
		}
		subscriptionData.setStatus(status);
	}
	
	protected void populatePricePlan(final ProductModel productModel,
			final ProductData productData)
	{
		if (productModel != null)
		{
			getSubscriptionProductPricePlanPopulator().populate(productModel,productData);
		}
	}
	
	
	protected void populateCurrentUsage(String subscriptionId,SubscriptionData subscriptionData)
	{
		String currentDate = SapRevenueCloudSubscriptionUtil.dateToString(Calendar.getInstance().getTime());
		List<Bills> bills = sapRevenueCloudSubscriptionService.getSubscriptionCurrentUsage(subscriptionId, currentDate);
		for(Bills bill : bills)
		{
			for(BillItem billItem : bill.getBillItems())
			{
				if(subscriptionId.equals(billItem.getSubscriptionId()))
				{
					subscriptionData.setCurrentUsages(populateBillingCharges(billItem));
				}
			}
		}
	}
	
	protected List<UsageChargeData> populateBillingCharges(BillItem billItem)
	{
		List<UsageChargeData> chargeEntries = new ArrayList<>();
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
		return chargeEntries;
	}
	
	
	protected void populateBillingFrequency(BillingPlanModel billingPlanModel, SubscriptionData subscriptionDetails)
	{
		switch (billingPlanModel.getId())
		{
			case SaprevenuecloudorderConstants.CALENDAR_MONTHLY:
				subscriptionDetails.setContractFrequency("Months");
				break;
			case SaprevenuecloudorderConstants.ANNIVERSARY_MONTHLY:
				subscriptionDetails.setContractFrequency("Months");
				break;
			case SaprevenuecloudorderConstants.CALENDAR_QUARTERLY:
				subscriptionDetails.setContractFrequency("Quarters");
				break;
			case "anniversary_quarterly":
				subscriptionDetails.setContractFrequency("Quarters");
				break;
			case SaprevenuecloudorderConstants.CALENDAR_HALF_YEARLY:
				subscriptionDetails.setContractFrequency("Half-Years");
				break;
			case "anniversary_half_yearly":
				subscriptionDetails.setContractFrequency("Half-Years");
				break;
			case SaprevenuecloudorderConstants.CALENDAR_YEARLY:
				subscriptionDetails.setContractFrequency("Years");
				break;
			case "anniversary_yearly":
				subscriptionDetails.setContractFrequency("Years");
				break;
			default:
				LOG.warn(String.format("Unknown frequency code \"%s\"", billingPlanModel.getId()));
				break;
		}
	}
	
	
	public PriceDataFactory getPriceDataFactory() {
		return priceDataFactory;
	}

	public void setPriceDataFactory(PriceDataFactory priceDataFactory) {
		this.priceDataFactory = priceDataFactory;
	}

	public CommonI18NService getCommonI18NService() {
		return commonI18NService;
	}

	public void setCommonI18NService(CommonI18NService commonI18NService) {
		this.commonI18NService = commonI18NService;
	}

	public SapRevenueCloudSubscriptionService getSapRevenueCloudSubscriptionService() {
		return sapRevenueCloudSubscriptionService;
	}

	public void setSapRevenueCloudSubscriptionService(
			SapRevenueCloudSubscriptionService sapRevenueCloudSubscriptionService) {
		this.sapRevenueCloudSubscriptionService = sapRevenueCloudSubscriptionService;
	}

	public Populator<ProductModel, ProductData> getSubscriptionProductPricePlanPopulator() {
		return subscriptionProductPricePlanPopulator;
	}

	public void setSubscriptionProductPricePlanPopulator(
			Populator<ProductModel, ProductData> subscriptionProductPricePlanPopulator) {
		this.subscriptionProductPricePlanPopulator = subscriptionProductPricePlanPopulator;
	}

	public Populator<ProductModel, ProductData> getProductUrlPopulator() {
		return productUrlPopulator;
	}

	public void setProductUrlPopulator(Populator<ProductModel, ProductData> productUrlPopulator) {
		this.productUrlPopulator = productUrlPopulator;
	}

	public SapRevenueCloudProductService getSapRevenueCloudProductService() {
		return sapRevenueCloudProductService;
	}

	public void setSapRevenueCloudProductService(SapRevenueCloudProductService sapRevenueCloudProductService) {
		this.sapRevenueCloudProductService = sapRevenueCloudProductService;
	}

	public CMSSiteService getCmsSiteService() {
		return cmsSiteService;
	}

	public void setCmsSiteService(CMSSiteService cmsSiteService) {
		this.cmsSiteService = cmsSiteService;
	}
	

}
