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
package de.hybris.platform.sap.productconfig.facades.impl;

import de.hybris.platform.commercefacades.product.converters.populator.AbstractProductImagePopulator;
import de.hybris.platform.commercefacades.product.converters.populator.ProductPricePopulator;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ImageDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.productconfig.facades.ConfigurationVariantData;
import de.hybris.platform.sap.productconfig.facades.ConfigurationVariantFacade;
import de.hybris.platform.sap.productconfig.services.data.VariantSearchResult;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationVariantSearchService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;



/**
 * Default implementation of the {@link ConfigurationVariantFacade}.
 */
public class ConfigurationVariantFacadeImpl implements ConfigurationVariantFacade
{

	private static final Logger LOG = Logger.getLogger(ConfigurationVariantFacadeImpl.class);
	private ProductConfigurationVariantSearchService variantSerachService;
	private ProductService productService;
	private AbstractProductImagePopulator imagePopulator;
	private ProductPricePopulator pricePopulator;

	@Override
	public List<ConfigurationVariantData> searchForSimilarVariants(final String configId, final String productCode)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Calling search service for similar variants with configId=" + configId + " and productCode=" + productCode);
		}
		List<ConfigurationVariantData> result;
		final List<VariantSearchResult> variants = getVariantSerachService().getVariantsForConfiguration(configId, productCode);
		if (CollectionUtils.isEmpty(variants))
		{
			result = Collections.emptyList();
			LOG.debug("Variant search did't match any variant");
		}
		else
		{

			result = new ArrayList(variants.size());
			for (final VariantSearchResult variantSearchResult : variants)
			{
				final ConfigurationVariantData variantData = createVariantData(variantSearchResult.getProductCode());
				result.add(variantData);
			}
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Variant search matched " + result.size() + " variants");
			}
		}
		return result;
	}

	protected ConfigurationVariantData createVariantData(final String variantId)
	{
		final ConfigurationVariantData variantData = new ConfigurationVariantData();
		variantData.setProductCode(variantId);
		final ProductModel product = getProductService().getProductForCode(variantId);
		variantData.setName(product.getName());
		addImageData(variantData, product);
		addPriceData(variantData, product);
		return variantData;
	}


	protected void addPriceData(final ConfigurationVariantData variantData, final ProductModel product)
	{
		final ProductData productData = new ProductData();
		getPricePopulator().populate(product, productData);
		variantData.setPrice(productData.getPrice());
	}

	protected void addImageData(final ConfigurationVariantData variantData, final ProductModel product)
	{
		final ProductData productData = new ProductData();
		getImagePopulator().populate(product, productData);
		final Collection<ImageData> images = productData.getImages();
		if (!CollectionUtils.isEmpty(images))
		{
			ImageData imageToUse = null;
			for (final ImageData image : images)
			{
				if (useImage(imageToUse, image))
				{
					imageToUse = image;
				}
			}
			variantData.setImageData(imageToUse);
		}
	}

	protected boolean useImage(final ImageData imageToUse, final ImageData image)
	{
		boolean useImage;
		if (null == image || !ImageDataType.PRIMARY.equals(image.getImageType()))
		{
			useImage = false;
		}
		else
		{
			if (imageToUse != null)
			{
				final int imageToUseGalleryIndex = getIntFromIntegerNullSafe(imageToUse.getGalleryIndex());
				final int imageGalleryIndex = getIntFromIntegerNullSafe(image.getGalleryIndex());
				useImage = imageToUseGalleryIndex > imageGalleryIndex;
			}
			else
			{
				useImage = true;
			}

		}
		return useImage;
	}

	protected int getIntFromIntegerNullSafe(final Integer integer)
	{
		int result = 0;
		if (null != integer)
		{
			result = integer.intValue();
		}
		return result;

	}

	protected ProductConfigurationVariantSearchService getVariantSerachService()
	{
		return variantSerachService;
	}

	/**
	 * @param variantSerachService
	 *           the service counter part of this facade
	 */
	@Required
	public void setVariantSerachService(final ProductConfigurationVariantSearchService variantSerachService)
	{
		this.variantSerachService = variantSerachService;
	}


	protected ProductService getProductService()
	{
		return productService;
	}


	/**
	 * @param productService
	 *           inject product service, to handle product master data
	 */
	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	protected AbstractProductImagePopulator getImagePopulator()
	{
		return imagePopulator;
	}

	/**
	 * @param imagePopulator
	 *           inject image populator to handle variant images
	 */
	@Required
	public void setImagePopulator(final AbstractProductImagePopulator imagePopulator)
	{
		this.imagePopulator = imagePopulator;
	}

	protected ProductPricePopulator getPricePopulator()
	{
		return pricePopulator;
	}

	/**
	 * @param pricePopulator
	 *           inject price populator to handle variant prices
	 */
	@Required
	public void setPricePopulator(final ProductPricePopulator pricePopulator)
	{
		this.pricePopulator = pricePopulator;
	}

}
