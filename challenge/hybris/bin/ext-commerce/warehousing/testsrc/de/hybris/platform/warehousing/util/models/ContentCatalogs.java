/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.warehousing.util.models;

import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.warehousing.util.builder.ContentCatalogModelBuilder;
import de.hybris.platform.warehousing.util.dao.impl.ContentCatalogDaoImpl;
import org.springframework.beans.factory.annotation.Required;


public class ContentCatalogs extends AbstractItems<ContentCatalogModel>
{
	public static final String CONTENTCATALOG_ID = "contentCatalog_online";
	private ContentCatalogDaoImpl contentCatalogDao;
	private CmsSites cmsSites;
	private CatalogVersions catalogVersions;

	public ContentCatalogModel contentCatalog_online()
	{
		return getOrCreateContentCatalog(CONTENTCATALOG_ID);
	}


	protected ContentCatalogModel getOrCreateContentCatalog(final String id)
	{
		return getOrSaveAndReturn(() -> getContentCatalogDao().getByCode(id), () -> ContentCatalogModelBuilder.aModel()
				.withId(id)
				.withDefaultCatalog(true)
				.build());
	}

	public ContentCatalogDaoImpl getContentCatalogDao()
	{
		return contentCatalogDao;
	}

	@Required
	public void setContentCatalogDao(final ContentCatalogDaoImpl contentCatalogDao)
	{
		this.contentCatalogDao = contentCatalogDao;
	}

	public CmsSites getCmsSites()
	{
		return cmsSites;
	}

	@Required
	public void setCmsSites(final CmsSites cmsSites)
	{
		this.cmsSites = cmsSites;
	}

	public CatalogVersions getCatalogVersions()
	{
		return catalogVersions;
	}

	@Required
	public void setCatalogVersions(final CatalogVersions catalogVersions)
	{
		this.catalogVersions = catalogVersions;
	}

}
