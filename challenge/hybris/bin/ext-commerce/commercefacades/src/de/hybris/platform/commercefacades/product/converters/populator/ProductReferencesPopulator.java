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
package de.hybris.platform.commercefacades.product.converters.populator;

import de.hybris.platform.catalog.model.ProductReferenceModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.ProductReferenceData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Populates product references (List of {@link ProductReferenceData}) for product ({@link ProductData}).
 */
public class ProductReferencesPopulator implements Populator<ProductModel, ProductData>
{
	private Converter<ProductReferenceModel, ProductReferenceData> productReferenceConverter;

	@Override
	public void populate(final ProductModel source, final ProductData target) throws ConversionException
	{
		final List<ProductReferenceData> productReferences = new ArrayList<ProductReferenceData>();
		for (final ProductReferenceModel ref : source.getProductReferences())
		{
			productReferences.add(productReferenceConverter.convert(ref));
		}

		target.setProductReferences(productReferences);

	}

	@Required
	public void setProductReferenceConverter(final Converter<ProductReferenceModel, ProductReferenceData> productReferenceConverter)
	{
		this.productReferenceConverter = productReferenceConverter;
	}

	protected Converter<ProductReferenceModel, ProductReferenceData> getProductReferenceConverter()
	{
		return productReferenceConverter;
	}
}
