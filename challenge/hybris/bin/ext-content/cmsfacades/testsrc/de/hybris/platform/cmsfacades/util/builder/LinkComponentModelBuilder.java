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

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.enums.LinkTargets;
import de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.Locale;


public class LinkComponentModelBuilder
{
	private final CMSLinkComponentModel model;

	private LinkComponentModelBuilder()
	{
		model = new CMSLinkComponentModel();
	}

	private LinkComponentModelBuilder(final CMSLinkComponentModel model)
	{
		this.model = model;
	}

	protected CMSLinkComponentModel getModel()
	{
		return this.model;
	}

	public static LinkComponentModelBuilder aModel()
	{
		return new LinkComponentModelBuilder();
	}

	public static LinkComponentModelBuilder fromModel(final CMSLinkComponentModel model)
	{
		return new LinkComponentModelBuilder(model);
	}

	public LinkComponentModelBuilder withUid(final String uid)
	{
		getModel().setUid(uid);
		return this;
	}

	public LinkComponentModelBuilder withCatalogVersion(final CatalogVersionModel catalogVersion)
	{
		getModel().setCatalogVersion(catalogVersion);
		return this;
	}

	public LinkComponentModelBuilder withLinkName(final String name, final Locale locale)
	{
		getModel().setLinkName(name, locale);
		return this;
	}

	public LinkComponentModelBuilder withProduct(final ProductModel product)
	{
		getModel().setProduct(product);
		return this;
	}

	public LinkComponentModelBuilder withCategory(final CategoryModel category)
	{
		getModel().setCategory(category);
		return this;
	}

	public LinkComponentModelBuilder withContentPage(final ContentPageModel contentPage)
	{
		getModel().setContentPage(contentPage);
		return this;
	}

	public LinkComponentModelBuilder withUrl(final String url)
	{
		getModel().setUrl(url);
		return this;
	}

	public LinkComponentModelBuilder withExternal(final boolean external)
	{
		getModel().setExternal(external);
		return this;
	}

	public LinkComponentModelBuilder withTarget(final LinkTargets target)
	{
		getModel().setTarget(target);
		return this;
	}

	public CMSLinkComponentModel build()
	{
		return this.getModel();
	}
}
