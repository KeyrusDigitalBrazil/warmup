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
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.customerinterestsfacades.data.ProductInterestEntryData;
import de.hybris.platform.customerinterestsfacades.data.ProductInterestRelationData;
import de.hybris.platform.customerinterestsfacades.futurestock.ExtendedFutureStockFacade;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


public class ProductInterestRelationPopulator
		implements Populator<Entry<ProductModel, List<ProductInterestEntryData>>, ProductInterestRelationData>
{
	private Converter<ProductModel, ProductData> productConverter;
	private ExtendedFutureStockFacade futureStockFacade;
	private static final String BACK_IN_STOCK = "BACK_IN_STOCK";
	private Converter<ProductModel, ProductData> productPriceAndStockConverter;

	@Override
	public void populate(final Entry<ProductModel, List<ProductInterestEntryData>> source,
			final ProductInterestRelationData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		final ProductData productData = getProductConverter().convert(source.getKey());
		getProductPriceAndStockConverter().convert(source.getKey(), productData);
		target.setProductInterestEntry(source.getValue());
		final Optional<ProductInterestEntryData> optional = target.getProductInterestEntry().stream()
				.filter(entry -> BACK_IN_STOCK.equalsIgnoreCase(entry.getInterestType())).findAny();
		if (optional.isPresent())
		{
			productData.setFutureStocks(getFutureStockFacade().getFutureAvailability(source.getKey()));
		}
		target.setProduct(productData);


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
	
	protected ExtendedFutureStockFacade getFutureStockFacade()
	{
		return futureStockFacade;
	}

	@Required
	public void setFutureStockFacade(ExtendedFutureStockFacade futureStockFacade)
	{
		this.futureStockFacade = futureStockFacade;
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
