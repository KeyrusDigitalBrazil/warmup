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
package de.hybris.platform.cmsfacades.catalogversions.populator;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cmsfacades.catalogversions.service.PageDisplayConditionService;
import de.hybris.platform.cmsfacades.data.CatalogVersionData;
import de.hybris.platform.cmsfacades.resolvers.sites.SiteThumbnailResolver;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.springframework.beans.factory.annotation.Required;


/**
 * Populates a {@Link CatalogVersionData} dto from a {@Link CatalogVersionModel} item
 */
public class ContentCatalogVersionModelPopulator implements Populator<CatalogVersionModel, CatalogVersionData>
{
	private SiteThumbnailResolver siteThumbnailResolver;
	private PageDisplayConditionService pageDisplayConditionService;

	@Override
	public void populate(final CatalogVersionModel source, final CatalogVersionData target) throws ConversionException
	{
		getSiteThumbnailResolver().resolveHomepageThumbnailUrl(source)
		.ifPresent(thumbnailUrl -> target.setThumbnailUrl(thumbnailUrl));
		target.setPageDisplayConditions(getPageDisplayConditionService().getDisplayConditions(source));
	}

	protected SiteThumbnailResolver getSiteThumbnailResolver()
	{
		return siteThumbnailResolver;
	}

	@Required
	public void setSiteThumbnailResolver(final SiteThumbnailResolver siteThumbnailResolver)
	{
		this.siteThumbnailResolver = siteThumbnailResolver;
	}

	protected PageDisplayConditionService getPageDisplayConditionService()
	{
		return pageDisplayConditionService;
	}

	@Required
	public void setPageDisplayConditionService(final PageDisplayConditionService pageDisplayConditionService)
	{
		this.pageDisplayConditionService = pageDisplayConditionService;
	}

}
