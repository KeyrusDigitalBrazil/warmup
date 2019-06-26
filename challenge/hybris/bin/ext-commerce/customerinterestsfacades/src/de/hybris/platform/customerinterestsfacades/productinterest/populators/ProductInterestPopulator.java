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
package de.hybris.platform.customerinterestsfacades.productinterest.populators;


import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.customerinterestsfacades.data.ProductInterestData;
import de.hybris.platform.customerinterestsservices.model.ProductInterestModel;
import de.hybris.platform.notificationfacades.facades.NotificationPreferenceFacade;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


public class ProductInterestPopulator implements Populator<ProductInterestModel, ProductInterestData>
{
	private AbstractPopulatingConverter<ProductModel, ProductData> productConverter;
	private Converter<ProductModel, ProductData> productPriceAndStockConverter;
	private NotificationPreferenceFacade notificationPreferenceFacade;

	@Override
	public void populate(final ProductInterestModel source, final ProductInterestData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setExpiryDate(source.getExpiryDate());
		target.setNotificationType(source.getNotificationType());

		final ProductData productData = getProductConverter().convert(source.getProduct());
		getProductPriceAndStockConverter().convert(source.getProduct(), productData);

		target.setProduct(productData);
		target.setCreationDate(source.getCreationtime());

		target.setNotificationChannels(
				getNotificationPreferenceFacade().getValidNotificationPreferences(source.getNotificationChannels()));
	}


	protected AbstractPopulatingConverter<ProductModel, ProductData> getProductConverter()
	{
		return productConverter;
	}

	@Required
	public void setProductConverter(final AbstractPopulatingConverter<ProductModel, ProductData> productConverter)
	{
		this.productConverter = productConverter;
	}

	protected Converter<ProductModel, ProductData> getProductPriceAndStockConverter()
	{
		return productPriceAndStockConverter;
	}

	@Required
	public void setProductPriceAndStockConverter(final Converter<ProductModel, ProductData> productPriceAndStockConverter)
	{
		this.productPriceAndStockConverter = productPriceAndStockConverter;
	}

	protected NotificationPreferenceFacade getNotificationPreferenceFacade()
	{
		return notificationPreferenceFacade;
	}

	@Required
	public void setNotificationPreferenceFacade(final NotificationPreferenceFacade notificationPreferenceFacade)
	{
		this.notificationPreferenceFacade = notificationPreferenceFacade;
	}

}
