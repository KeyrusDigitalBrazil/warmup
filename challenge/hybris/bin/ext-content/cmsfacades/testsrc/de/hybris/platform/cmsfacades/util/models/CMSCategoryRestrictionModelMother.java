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
package de.hybris.platform.cmsfacades.util.models;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.restrictions.CMSCategoryRestrictionModel;
import de.hybris.platform.cms2.servicelayer.daos.CMSRestrictionDao;
import de.hybris.platform.cmsfacades.util.builder.CMSCategoryRestrictionModelBuilder;

import java.util.Arrays;
import java.util.Collection;

public class CMSCategoryRestrictionModelMother extends AbstractModelMother<CMSCategoryRestrictionModel>
{
	public static final String UID_SHOES = "uid-shoes";
	public static final String NAME_SHOES = "name-shoes";

	private CMSRestrictionDao restrictionDao;
	private CategoryModelMother categoryModelMother;

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public CMSCategoryRestrictionModel shoesWithSandalsAndHeels(final CatalogVersionModel catalogVersion,
			final CatalogVersionModel productCatalogVersion, final AbstractPageModel... pages)
	{
		return getFromCollectionOrSaveAndReturn( //
				() -> (Collection) getRestrictionDao().findRestrictionsById(UID_SHOES, catalogVersion), //
				() -> CMSCategoryRestrictionModelBuilder.aModel() //
				.withUid(UID_SHOES) //
				.withCatalogVersion(catalogVersion) //
				.withName(NAME_SHOES) //
				.withRecursive(false) //
				.withCategories(Arrays.asList(getCategoryModelMother().createSandalsCategory(productCatalogVersion), getCategoryModelMother().createHeelsCategory(productCatalogVersion))) //
				.withPages(pages) //
				.build());
	}
	
	public CategoryModelMother getCategoryModelMother()
	{
		return categoryModelMother;
	}
	
	public void setCategoryModelMother(final CategoryModelMother categoryModelMother)
	{
		this.categoryModelMother = categoryModelMother;
	}

	public CMSRestrictionDao getRestrictionDao()
	{
		return restrictionDao;
	}

	public void setRestrictionDao(final CMSRestrictionDao restrictionDao)
	{
		this.restrictionDao = restrictionDao;
	}
}
