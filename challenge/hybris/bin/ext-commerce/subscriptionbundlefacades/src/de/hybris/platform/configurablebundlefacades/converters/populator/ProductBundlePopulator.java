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

package de.hybris.platform.configurablebundlefacades.converters.populator;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.commercefacades.product.converters.populator.AbstractProductPopulator;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.configurablebundlefacades.data.BundleTemplateData;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;


/**
 * ProductBundlePopulator to populate all the product data and the bundleTemplate data that belongs to the package
 *
 * @param <SOURCE>
 *           ProductModel
 * @param <TARGET>
 *           ProductData
 * @deprecated since 6.5 - {@link de.hybris.platform.configurablebundlefacades.converters.populator.ProductSoldIndividuallyPopulator} is
 * used for populating {@link ProductData#soldIndividually} field, for other functionality implement your own populator.
 */
@Deprecated
public class ProductBundlePopulator<SOURCE extends ProductModel, TARGET extends ProductData> extends
		AbstractProductPopulator<SOURCE, TARGET>
{

	private Converter<BundleTemplateModel, BundleTemplateData> bundleTemplateConverter;

	private Converter<ProductModel, ProductData> productConverter;

	@Override
	public void populate(final SOURCE productModel, final TARGET productData) throws ConversionException
	{
		validateParameterNotNullStandardMessage("productData", productData);
		validateParameterNotNullStandardMessage("productModel", productModel);

		List<BundleTemplateData> bundleTemplateDataList = new ArrayList<>();

		if (productModel != null && productModel.getBundleTemplates() != null)
		{
			//from the given product Id get component Ids in which the product is part of
			bundleTemplateDataList = productModel.getBundleTemplates().stream()
					.map(BundleTemplateModel::getParentTemplate)
					.filter(parent -> parent != null)
					.filter(parent -> parent.getChildTemplates() != null)
					.map(BundleTemplateModel::getChildTemplates)
					.flatMap(Collection::stream)
					.map(this::convertTemplate)
					.collect(Collectors.toList());
		}
		productData.setSoldIndividually(BooleanUtils.toBoolean(productModel.getSoldIndividually())); // NOSONAR
		productData.setBundleTemplates(bundleTemplateDataList);
	}

	@Nonnull
	protected BundleTemplateData convertTemplate(@Nonnull final BundleTemplateModel bundleTemplateModel)
	{
		//Populate the bundleTemplate data for the component.
		final BundleTemplateData bundleTemplateData = bundleTemplateConverter.convert(bundleTemplateModel);

		//Populate the product data that belong to that component.
		final List<ProductData> productDataList = Converters.convertAll(bundleTemplateModel.getProducts(), productConverter);

		//Bind the product data to bundleTemplate data
		bundleTemplateData.setProducts(productDataList);

		return bundleTemplateData;
	}


	protected Converter<BundleTemplateModel, BundleTemplateData> getBundleTemplateConverter()
	{
		return bundleTemplateConverter;
	}

	@Required
	public void setBundleTemplateConverter(final Converter<BundleTemplateModel, BundleTemplateData> bundleTemplateConverter)
	{
		this.bundleTemplateConverter = bundleTemplateConverter;
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
}
