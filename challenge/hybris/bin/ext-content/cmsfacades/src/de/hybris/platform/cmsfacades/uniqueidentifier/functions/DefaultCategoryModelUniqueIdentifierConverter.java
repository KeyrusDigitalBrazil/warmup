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
package de.hybris.platform.cmsfacades.uniqueidentifier.functions;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.common.service.SessionSearchRestrictionsDisabler;
import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.cmsfacades.uniqueidentifier.EncodedItemComposedKey;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueIdentifierConverter;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Required;

/**
 * Default implementation for conversion of {@link CategoryModel}
 */
public class DefaultCategoryModelUniqueIdentifierConverter implements UniqueIdentifierConverter<CategoryModel>
{

	private CategoryService categoryService;
	private CatalogVersionService catalogVersionService;
	private Converter<CategoryModel, ItemData> categoryModelItemDataConverter;
	private SessionSearchRestrictionsDisabler sessionSearchRestrictionsDisabler;

	@Override
	public String getItemType()
	{
		return CategoryModel._TYPECODE;
	}

	@Override
	public ItemData convert(final CategoryModel categoryModel)
	{
		return getCategoryModelItemDataConverter().convert(categoryModel);
	}

	@Override
	public CategoryModel convert(final ItemData itemData)
	{
		final EncodedItemComposedKey itemComposedKey = new EncodedItemComposedKey
				.Builder(itemData.getItemId()).encoded().build();

		return getSessionSearchRestrictionsDisabler().execute(findCategory(itemComposedKey));
	}

	protected Supplier<CategoryModel> findCategory(final EncodedItemComposedKey itemComposedKey)
	{
		return () -> {
			final CatalogVersionModel catalogVersion = getCatalogVersionService().getCatalogVersion(itemComposedKey.getCatalogId(),
					itemComposedKey.getCatalogVersion());
			return getCategoryService().getCategoryForCode(catalogVersion, itemComposedKey.getItemId());
		};
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

	protected Converter<CategoryModel, ItemData> getCategoryModelItemDataConverter()
	{
		return categoryModelItemDataConverter;
	}

	@Required
	public void setCategoryModelItemDataConverter(final Converter<CategoryModel, ItemData> categoryModelItemDataConverter)
	{
		this.categoryModelItemDataConverter = categoryModelItemDataConverter;
	}

	protected SessionSearchRestrictionsDisabler getSessionSearchRestrictionsDisabler()
	{
		return sessionSearchRestrictionsDisabler;
	}

	@Required
	public void setSessionSearchRestrictionsDisabler(final SessionSearchRestrictionsDisabler sessionSearchRestrictionsDisabler)
	{
		this.sessionSearchRestrictionsDisabler = sessionSearchRestrictionsDisabler;
	}
}
