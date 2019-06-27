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
package de.hybris.platform.acceleratorcms.preview.strategies.impl;

import de.hybris.platform.acceleratorcms.preview.strategies.PreviewContextInformationLoaderStrategy;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.cms2.model.preview.PreviewDataModel;

import org.springframework.beans.factory.annotation.Required;


public class CatalogVersionsPreviewStrategy implements PreviewContextInformationLoaderStrategy
{

	private CatalogVersionService catalogVersionService;

	@Override
	public void initContextFromPreview(final PreviewDataModel preview)
	{
		getCatalogVersionService().setSessionCatalogVersions(preview.getCatalogVersions());
	}

	public CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}

	@Required
	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}
}
