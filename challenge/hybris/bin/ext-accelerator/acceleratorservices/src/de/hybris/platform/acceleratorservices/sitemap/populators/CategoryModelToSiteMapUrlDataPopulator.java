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
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.apache.commons.lang.StringEscapeUtils;


public class CategoryModelToSiteMapUrlDataPopulator implements Populator<CategoryModel, SiteMapUrlData>
{
	private UrlResolver<CategoryModel> urlResolver;

	@Override
	public void populate(final CategoryModel categoryModel, final SiteMapUrlData siteMapUrlData) throws ConversionException
	{
		final String relUrl = StringEscapeUtils.escapeXml(getUrlResolver().resolve(categoryModel));
		siteMapUrlData.setLoc(relUrl);
	}

	public UrlResolver<CategoryModel> getUrlResolver()
	{
		return urlResolver;
	}

	public void setUrlResolver(final UrlResolver<CategoryModel> urlResolver)
	{
		this.urlResolver = urlResolver;
	}

}
