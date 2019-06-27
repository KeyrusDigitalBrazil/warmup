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
package de.hybris.platform.acceleratorservices.sitemap.populators;

import de.hybris.platform.acceleratorservices.sitemap.data.SiteMapUrlData;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import java.util.Collections;

import org.apache.commons.lang.StringEscapeUtils;


public class ProductModelToSiteMapUrlDataPopulator implements Populator<ProductModel, SiteMapUrlData>
{
	private UrlResolver<ProductModel> urlResolver;

	@Override
	public void populate(final ProductModel productModel, final SiteMapUrlData siteMapUrlData) throws ConversionException
	{
		final String relUrl = StringEscapeUtils.escapeXml(getUrlResolver().resolve(productModel));
		siteMapUrlData.setLoc(relUrl);
		if (productModel.getPicture() != null)
		{
			siteMapUrlData.setImages(Collections.singletonList(productModel.getPicture().getURL()));
		}
	}

	public UrlResolver<ProductModel> getUrlResolver()
	{
		return urlResolver;
	}

	public void setUrlResolver(final UrlResolver<ProductModel> urlResolver)
	{
		this.urlResolver = urlResolver;
	}
}
