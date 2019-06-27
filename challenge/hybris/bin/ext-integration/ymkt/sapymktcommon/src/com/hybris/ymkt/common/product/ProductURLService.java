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
package com.hybris.ymkt.common.product;

import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 * Convenience service to generate the product URL and image URL.
 */
public class ProductURLService
{
	private static final Logger LOG = LoggerFactory.getLogger(ProductURLService.class);

	protected String imagePrefix;
	protected String navPrefix;
	protected String navSuffix;

	protected ProductService productService;

	/**
	 * Provide the URL of the product image if available.
	 *
	 * @param product
	 *           {@link ProductModel} to get image URL from.
	 * @return {@link Optional} image URL if the product has one.
	 */
	public Optional<String> getProductImageURL(final ProductModel product)
	{
		return Optional.ofNullable(product.getPicture()) //
				.map(MediaModel::getURL) //
				.map(this.imagePrefix::concat);
	}

	/**
	 * Provide the URL of the product shop detail page.
	 *
	 * @param product
	 *           {@link ProductModel} to get detail page URL from.
	 * @return URL accessible by web user.
	 */
	public String getProductURL(final ProductModel product)
	{
		return getProductURL(product.getCode());
	}

	/**
	 * Provide the URL of the product shop detail page.
	 *
	 * @param productCode
	 *           {@link ProductModel#getCode()} to get detail page URL from.
	 * @return URL accessible by web user.
	 */
	public String getProductURL(final String productCode)
	{
		try
		{
			final StringBuilder sb = new StringBuilder();
			sb.append(this.navPrefix);
			sb.append(URLEncoder.encode(productCode, "UTF8"));
			sb.append(this.navSuffix);
			return sb.toString();
		}
		catch (final UnsupportedEncodingException e)
		{
			throw new IllegalStateException(e);
		}
	}

	@Required
	public void setImagePrefix(final String imagePrefix)
	{
		LOG.debug("imagePrefix={}", imagePrefix);
		this.imagePrefix = imagePrefix.intern();
	}

	@Required
	public void setNavPrefix(final String navPrefix)
	{
		LOG.debug("navPrefix={}", navPrefix);
		this.navPrefix = navPrefix.intern();
	}

	@Required
	public void setNavSuffix(final String navSuffix)
	{
		LOG.debug("navSuffix={}", navSuffix);
		this.navSuffix = navSuffix.intern();
	}

	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}
}
