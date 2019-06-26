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
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.restrictions.CMSCategoryRestrictionModel;

import java.util.Arrays;
import java.util.Collection;

public class CMSCategoryRestrictionModelBuilder
{

	private final CMSCategoryRestrictionModel model;

	private CMSCategoryRestrictionModelBuilder()
	{
		model = new CMSCategoryRestrictionModel();
	}

	private CMSCategoryRestrictionModelBuilder(CMSCategoryRestrictionModel model)
	{
		this.model = model;
	}

	protected CMSCategoryRestrictionModel getModel()
	{
		return this.model;
	}

	public static CMSCategoryRestrictionModelBuilder aModel()
	{
		return new CMSCategoryRestrictionModelBuilder();
	}

	public static CMSCategoryRestrictionModelBuilder fromModel(CMSCategoryRestrictionModel model)
	{
		return new CMSCategoryRestrictionModelBuilder(model);
	}

	public CMSCategoryRestrictionModelBuilder withCatalogVersion(CatalogVersionModel model)
	{
		getModel().setCatalogVersion(model);
		return this;
	}

	public CMSCategoryRestrictionModelBuilder withUid(String uid)
	{
		getModel().setUid(uid);
		return this;
	}

	public CMSCategoryRestrictionModelBuilder withName(String name)
	{
		getModel().setName(name);
		return this;
	}

	public CMSCategoryRestrictionModelBuilder withRecursive(Boolean isRecursive)
	{
		getModel().setRecursive(isRecursive);
		return this;
	}

	public CMSCategoryRestrictionModelBuilder withCategories(Collection<CategoryModel> categories)
	{
		getModel().setCategories(categories);
		return this;
	}

	public CMSCategoryRestrictionModelBuilder withPages(AbstractPageModel... pages)
	{
		getModel().setPages(Arrays.asList(pages));
		return this;
	}

	public CMSCategoryRestrictionModel build()
	{
		return this.getModel();
	}
}
