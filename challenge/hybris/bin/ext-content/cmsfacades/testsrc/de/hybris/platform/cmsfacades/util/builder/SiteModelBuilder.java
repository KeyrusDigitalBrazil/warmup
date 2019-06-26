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
package de.hybris.platform.cmsfacades.util.builder;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.store.BaseStoreModel;

import java.util.Date;
import java.util.List;
import java.util.Locale;


public class SiteModelBuilder
{
	private final CMSSiteModel model;

	private SiteModelBuilder(final CMSSiteModel model)
	{
		this.model = model;
	}

	private SiteModelBuilder()
	{
		this.model = new CMSSiteModel();
	}

	public CMSSiteModel getModel()
	{
		return model;
	}

	public static SiteModelBuilder aModel()
	{
		return new SiteModelBuilder();
	}

	public static SiteModelBuilder fromModel(final CMSSiteModel model)
	{
		return new SiteModelBuilder(model);
	}

	public SiteModelBuilder withName(final String name)
	{
		getModel().setName(name);
		return this;
	}

	public SiteModelBuilder withName(final String name, final Locale locale)
	{
		getModel().setName(name, locale);
		return this;
	}

	public SiteModelBuilder withActive(final Boolean isActive)
	{
		getModel().setActive(isActive);
		return this;
	}

	public SiteModelBuilder from(final Date activeFrom)
	{
		getModel().setActiveFrom(activeFrom);
		return this;
	}

	public SiteModelBuilder until(final Date activeUntil)
	{
		getModel().setActiveUntil(activeUntil);
		return this;
	}

	public SiteModelBuilder withDefaultCatalog(final CatalogModel defaultCatalog)
	{
		getModel().setDefaultCatalog(defaultCatalog);
		return this;
	}

	public SiteModelBuilder withLanguage(final LanguageModel defaultLanguage)
	{
		getModel().setDefaultLanguage(defaultLanguage);
		return this;
	}

	public SiteModelBuilder withUid(final String uid)
	{
		getModel().setUid(uid);
		return this;
	}

	public SiteModelBuilder withPreviewRUL(final String previewRUL)
	{
		getModel().setPreviewURL(previewRUL);
		return this;
	}

	public SiteModelBuilder withRedirectUrl(final String redirectUrl)
	{
		getModel().setRedirectURL(redirectUrl);
		return this;
	}

	public SiteModelBuilder withStores(final List<BaseStoreModel> baseStoreModels)
	{
		getModel().setStores(baseStoreModels);
		return this;
	}

	public SiteModelBuilder withContentCatalogs(final List<ContentCatalogModel> contentCatalogs)
	{
		getModel().setContentCatalogs(contentCatalogs);
		return this;
	}

	public CMSSiteModel build()
	{
		return this.getModel();
	}
}
