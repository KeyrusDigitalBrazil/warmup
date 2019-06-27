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
package de.hybris.platform.cmssmarteditwebservices.products.facade.populator;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cmsfacades.data.CategoryData;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.springframework.beans.factory.annotation.Required;

/**
 * Basic class for populating {@link de.hybris.platform.cmssmarteditwebservices.data.CategoryData} from {@link CategoryData} data.  
 */
public class CategoryDataPopulator implements Populator<CategoryData, de.hybris.platform.cmssmarteditwebservices.data.CategoryData>
{
	private UniqueItemIdentifierService uniqueItemIdentifierService;
	private CatalogVersionService catalogVersionService;
	private CategoryService categoryService;
	
	@Override
	public void populate(final CategoryData source,
			final de.hybris.platform.cmssmarteditwebservices.data.CategoryData target) throws ConversionException
	{
		final CatalogVersionModel catalogVersion = getCatalogVersionService().getCatalogVersion(source.getCatalogId(),
				source.getCatalogVersion());

		final CategoryModel category = getCategoryService().getCategoryForCode(catalogVersion, source.getCode());

		getUniqueItemIdentifierService().getItemData(category).ifPresent(itemData -> target.setUid(itemData.getItemId()));
		
		target.setCode(source.getCode());
		target.setName(source.getName());
		target.setDescription(source.getDescription());
		target.setCatalogId(source.getCatalogId());
		target.setCatalogVersion(source.getCatalogVersion());
	}


	protected UniqueItemIdentifierService getUniqueItemIdentifierService()
	{
		return uniqueItemIdentifierService;
	}

	@Required
	public void setUniqueItemIdentifierService(final UniqueItemIdentifierService uniqueItemIdentifierService)
	{
		this.uniqueItemIdentifierService = uniqueItemIdentifierService;
	}

	protected CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}

	@Required
	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}

	protected CategoryService getCategoryService()
	{
		return categoryService;
	}

	@Required
	public void setCategoryService(final CategoryService categoryService)
	{
		this.categoryService = categoryService;
	}
}
