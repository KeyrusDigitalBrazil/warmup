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

import com.sap.hybris.saprevenuecloudproduct.service.SapRevenueCloudProductService;

import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.Subscription;
import de.hybris.platform.sap.saprevenuecloudorder.util.SapRevenueCloudSubscriptionUtil;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.subscriptionfacades.data.SubscriptionData;
import de.hybris.platform.subscriptionservices.enums.SubscriptionStatus;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;

/**
 * Populate DTO {@link SubscriptionData} with data from {@link Subscription}.
 *
 * @param <SOURCE> source class
 * @param <TARGET> target class
 */

public class DefaultSAPRevenueCloudSubscriptionPopulator implements Populator<Subscription,SubscriptionData>
{
	private UserService userService;
	private SapRevenueCloudProductService sapRevenueCloudProductService;
	private CMSSiteService cmsSiteService;
	private Populator<ProductModel, ProductData> productUrlPopulator;

	@Override
	public void populate(Subscription source, SubscriptionData target) throws ConversionException 
	{
		validateParameterNotNullStandardMessage("source", source);
		validateParameterNotNullStandardMessage("target", target);
		
		final String customerId = ((CustomerModel) userService.getCurrentUser()).getRevenueCloudCustomerId();
		target.setCustomerId(customerId);
		target.setId(source.getSubscriptionId());
		populateStatus(source, target);
		final String ratePlanId = source.getSnapshots().get(0).getItems().get(0).getRatePlan().getId();
		final SubscriptionPricePlanModel pricePlanModel = getSapRevenueCloudProductService()
				.getSubscriptionPricePlanForId(ratePlanId, getCmsSiteService().getCurrentCatalogVersion());
		if(pricePlanModel != null) 
		{
			target.setName(pricePlanModel.getProduct().getName());
			final ProductData productData = new ProductData();
			getProductUrlPopulator().populate(pricePlanModel.getProduct(), productData);
			target.setProductUrl(productData.getUrl());
			target.setStartDate(SapRevenueCloudSubscriptionUtil.formatDate(source.getValidFrom()));
			if (source.getValidUntil() != null && !source.getValidUntil().isEmpty())
			{
				target.setEndDate(SapRevenueCloudSubscriptionUtil.formatDate(source.getValidUntil()));
			}
			target.setDocumentNumber(source.getDocumentNumber());
		}
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
	
	public SapRevenueCloudProductService getSapRevenueCloudProductService()
	{
		return sapRevenueCloudProductService;
	}

	public void setSapRevenueCloudProductService(SapRevenueCloudProductService sapRevenueCloudProductService) 
	{
		this.sapRevenueCloudProductService = sapRevenueCloudProductService;
	}

	public UserService getUserService()
	{
		return userService;
	}

	public void setUserService(UserService userService) 
	{
		this.userService = userService;
	}


	public Populator<ProductModel, ProductData> getProductUrlPopulator() 
	{
		return productUrlPopulator;
	}

	public void setProductUrlPopulator(Populator<ProductModel, ProductData> productUrlPopulator)
	{
		this.productUrlPopulator = productUrlPopulator;
	}
	

	public CMSSiteService getCmsSiteService() 
	{
		return cmsSiteService;
	}

	public void setCmsSiteService(CMSSiteService cmsSiteService) 
	{
		this.cmsSiteService = cmsSiteService;
	}
}
