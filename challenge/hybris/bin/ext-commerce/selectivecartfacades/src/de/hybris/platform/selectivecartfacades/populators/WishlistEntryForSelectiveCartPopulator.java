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
package de.hybris.platform.selectivecartfacades.populators;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.selectivecartfacades.data.Wishlist2EntryData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.wishlist2.model.Wishlist2EntryModel;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Populates {@link Wishlist2EntryModel} to {@link Wishlist2EntryData}
 */
public class WishlistEntryForSelectiveCartPopulator implements Populator<Wishlist2EntryModel, Wishlist2EntryData>
{
	private Converter<ProductModel, ProductData> productConverter;
	private Converter<ProductModel, ProductData> productPriceAndStockConverter;

	@Override
	public void populate(final Wishlist2EntryModel source, final Wishlist2EntryData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		final ProductData productData = getProductConverter().convert(source.getProduct());
		getProductPriceAndStockConverter().convert(source.getProduct(), productData);

		target.setProduct(productData);
		target.setAddedDate(source.getAddedDate());
		target.setQuantity(source.getQuantity());
	}

	protected Converter<ProductModel, ProductData> getProductConverter()
	{
		return productConverter;
	}

	@Required
	public void setProductConverter(final Converter<ProductModel, ProductData> productConverter)
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

}
