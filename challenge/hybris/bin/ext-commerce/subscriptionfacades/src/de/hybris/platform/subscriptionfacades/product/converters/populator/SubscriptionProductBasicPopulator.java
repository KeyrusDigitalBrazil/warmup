/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.subscriptionfacades.product.converters.populator;

import de.hybris.platform.commercefacades.product.converters.populator.AbstractProductPopulator;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.subscriptionfacades.data.SubscriptionTermData;
import de.hybris.platform.subscriptionservices.model.SubscriptionTermModel;

import de.hybris.platform.subscriptionservices.subscription.SubscriptionProductService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Populate the product data with the most basic product data.
 *
 * @param <SOURCE> source class
 * @param <TARGET> target class
 */
public class SubscriptionProductBasicPopulator<SOURCE extends ProductModel, TARGET extends ProductData> extends
		AbstractProductPopulator<SOURCE, TARGET>
{
	private Converter<SubscriptionTermModel, SubscriptionTermData> subscriptionTermConverter;
	private SubscriptionProductService subscriptionProductService;

	@Override
	public void populate(final SOURCE productModel, final TARGET productData) throws ConversionException
	{
		final SubscriptionTermModel subscriptionTermModel = productModel.getSubscriptionTerm();
		if (StringUtils.isEmpty(productData.getItemType()))
		{
			productData.setItemType(getSubscriptionProductService().isSubscription(productModel)
					? "SubscriptionProduct"
					: ProductModel._TYPECODE);
		}
		if (subscriptionTermModel != null)
		{
			productData.setSubscriptionTerm(getSubscriptionTermConverter().convert(subscriptionTermModel));
		}
	}

	protected Converter<SubscriptionTermModel, SubscriptionTermData> getSubscriptionTermConverter()
	{
		return subscriptionTermConverter;
	}

	@Required
	public void setSubscriptionTermConverter(final Converter<SubscriptionTermModel, SubscriptionTermData> subscriptionTermConverter)
	{
		this.subscriptionTermConverter = subscriptionTermConverter;
	}

	/**
	 * @return subscription product service.
	 */
	protected SubscriptionProductService getSubscriptionProductService()
	{
		return subscriptionProductService;
	}

	@Required
	public void setSubscriptionProductService(final SubscriptionProductService subscriptionProductService)
	{
		this.subscriptionProductService = subscriptionProductService;
	}
}
