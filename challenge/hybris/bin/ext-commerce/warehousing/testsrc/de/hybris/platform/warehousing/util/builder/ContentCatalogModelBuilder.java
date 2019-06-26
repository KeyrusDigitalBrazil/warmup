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
package de.hybris.platform.warehousing.util.builder;

import com.google.common.collect.Lists;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;

import java.util.Locale;


public class ContentCatalogModelBuilder
{
	private final ContentCatalogModel model;

	private ContentCatalogModelBuilder()
	{
		model = new ContentCatalogModel();
	}

	private ContentCatalogModel getModel()
	{
		return this.model;
	}

	public static ContentCatalogModelBuilder aModel()
	{
		return new ContentCatalogModelBuilder();
	}

	public ContentCatalogModel build()
	{
		return getModel();
	}

	public ContentCatalogModelBuilder withId(final String id)
	{
		getModel().setId(id);
		return this;
	}

	public ContentCatalogModelBuilder withActiveCatalogVersion(final CatalogVersionModel version)
	{
		getModel().setActiveCatalogVersion(version);
		return this;
	}

	public ContentCatalogModelBuilder withCmsSites(final CMSSiteModel... sites)
	{
		getModel().setCmsSites(Lists.newArrayList(sites));
		return this;
	}

	public ContentCatalogModelBuilder withName(final String name, final Locale locale)
	{
		getModel().setName(name, locale);
		return this;
	}

	public ContentCatalogModelBuilder withDefaultCatalog(final Boolean defaultCatalog)
	{
		getModel().setDefaultCatalog(defaultCatalog);
		return this;
	}

}
